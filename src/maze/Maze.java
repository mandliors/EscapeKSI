package maze;

import actors.Enemy;
import actors.Player;
import assets.AssetManager;
import coin.Coin;
import collision.Collision;
import gameobjects.shapes.ColoredCube;
import rendering.Renderer;
import rendering.RendererString;
import world.World;
import math.*;
import gameobjects.shapes.ColoredPlain;
import gameobjects.GameObject;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Maze implements Serializable
{
    public enum CellType { WALL, EMPTY, COIN, PLAYER, ENEMY }
    private class Cell
    {
        public int x, y;
        public Cell(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return x == cell.x && y == cell.y;
        }

        @Override
        public int hashCode() { return Objects.hash(x, y); }
    }

    public static final String defaultMaze =
            "####################" +
            "#0  ####          x#" +
            "# #  x#    ###### ##" +
            "# ##### ## #   #  ##" +
            "# #   #  #x# #   #x#" +
            "#   # ## ### ### # #" +
            "#####     #  #   # #" +
            "#   # ### # ## ### #" +
            "# #   #   # #      #" +
            "# ##### # # # ######" +
            "#x  #x  #   # #    #" +
            "### ### ## ## # ## #" +
            "#   #       #   #  #" +
            "# ### ##### ##### ##" +
            "#     #   #        #" +
            "# ##### # ### ## # #" +
            "# #     #     #  # #" +
            "# ## # #### # # ## #" +
            "#o   #      #   #x #" +
            "####################" ;

    private static final double CELL_SIZE = 4;
    private static final double WALL_HEIGHT = 4;
    private static final double FLOOR_CELL_SCALE = 0.5;
    private static final double PATHFIND_DELTA_IN_SECONDS = 0.2;

    private final CellType[] maze;
    private final String coinTexturePath;
    private final String enemyTexturePath;
    private transient List<GameObject> walls;
    private transient List<GameObject> floorAndCeiling;
    private transient List<Coin> coins;
    private transient Player player;
    private transient Enemy enemy;
    private transient int maxCoinCount;

    private final int MAZE_SIZE;
    private Color wallColor = new Color(191, 172, 44);
    private Color floorColor = new Color(77, 67, 23);
    private Color ceilingColor = new Color(77, 67, 23);

    private double secondsAfterLastPathfind;

    public Maze(String mazeString, String coinTexturePath, String enemyTexturePath)
    {
        MAZE_SIZE = (int)Math.sqrt(mazeString.length());
        maze = new CellType[MAZE_SIZE * MAZE_SIZE];
        this.coinTexturePath = coinTexturePath;
        this.enemyTexturePath = enemyTexturePath;

        // if the maze is invalid, use the default maze
        if (mazeString.length() != MAZE_SIZE * MAZE_SIZE)
            mazeString = defaultMaze;

        for (int i = 0; i < MAZE_SIZE; i++)
        {
            for (int j = 0; j < MAZE_SIZE; j++)
            {
                int idx = i * MAZE_SIZE + j;
                switch (mazeString.charAt(idx))
                {
                    case '#' -> maze[idx] = CellType.WALL;
                    case ' ' -> maze[idx] = CellType.EMPTY;
                    case 'x' -> maze[idx] = CellType.COIN;
                    case 'o' -> maze[idx] = CellType.PLAYER;
                    case '0' -> maze[idx] = CellType.ENEMY;
                }
            }
        }
        build();
    }

    public void update(double dt)
    {
        // check for collisions with walls
        Collision collision = null;
        for (GameObject wall : walls)
        {
            if (Collision.collideWith(player, wall))
            {
                Collision newCollision = new Collision(player, wall);
                if (collision == null)
                    collision = newCollision;
                else
                {
                    double distSqrPrev = player.getPosition().subtract(collision.getGameObject().getPosition()).getLengthSquared();
                    double distSqrNew = player.getPosition().subtract(newCollision.getGameObject().getPosition()).getLengthSquared();
                    collision = distSqrPrev < distSqrNew ? collision : newCollision;
                }
            }
        }

        // check for collision with coins
        List<Collision> collisions = new ArrayList<>();
        for (GameObject coin : coins)
            if (Collision.collideWith(player, coin))
                collisions.add(new Collision(player, coin));

        // resolve collisions with the closest wall
        if (collision != null)
            player.translate(collision.getDisplacement());

        // resolve collisions with coins
        for (Collision c : collisions)
        {
            GameObject coin = c.getGameObject();
            coins.remove(coin);
            World.removeGameObject(coin);
        }

        // check player-enemy collision, and update enemy direction if 1 second has passed
        Cell enemyCell = pos2Cell(enemy.getPosition().xz());
        Cell playerCell = pos2Cell(player.getPosition().xz());
        if (enemyCell.equals(playerCell))
            gameOver();
        else if ((secondsAfterLastPathfind += dt) > PATHFIND_DELTA_IN_SECONDS)
        {
            secondsAfterLastPathfind -= PATHFIND_DELTA_IN_SECONDS;

            List<Cell> path = pathfindInMaze(enemyCell, playerCell);

            // no path
            if (path == null) return;
            // game over
            if (path.isEmpty())
            {
                gameOver();
                return;
            }

            Cell nextCell = path.get(1);
            if (enemyCell.y > nextCell.y)
                enemy.setMoveDirection(Enemy.MoveDirection.UP);
            else if (enemyCell.x < nextCell.x)
                enemy.setMoveDirection(Enemy.MoveDirection.RIGHT);
            else if (enemyCell.y < nextCell.y)
                enemy.setMoveDirection(Enemy.MoveDirection.DOWN);
            else if (enemyCell.x > nextCell.x)
                enemy.setMoveDirection(Enemy.MoveDirection.LEFT);
        }

        Renderer.addString(new RendererString(
                String.format("Primes: %d/%d", maxCoinCount - coins.size(), maxCoinCount),
                Color.YELLOW,
                false
        ));
    }

    public void saveToFile(String path)
    {
        try
        {
            File file = new File(path);
            File parentDirs = file.getParentFile();

            if (parentDirs != null && !parentDirs.exists())
                parentDirs.mkdirs();

            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(f);
            out.writeObject(this);
            out.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public static Maze loadFromFile(String path)
    {
        try
        {
            FileInputStream f = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(f);
            Maze maze = (Maze)in.readObject();
            maze.build();
            in.close();
            return maze;
        } catch(Exception e) { e.printStackTrace(); return null; }
    }

    public Player getPlayer() { return  player; }

    public CellType getCell(int x, int y)
    {
        if (!Meth.isBetween(x, 0, MAZE_SIZE - 1) || !Meth.isBetween(y, 0, MAZE_SIZE - 1)) return CellType.EMPTY;
        else return maze[y * MAZE_SIZE + x];
    }

    public void setCell(int x, int y, CellType type)
    {
        if (!Meth.isBetween(x, 0, MAZE_SIZE - 1) || !Meth.isBetween(y, 0, MAZE_SIZE - 1)) return;

        maze[y * MAZE_SIZE + x] = type;
        destroy();
        build();
    }

    public Color getWallColor() { return wallColor; }
    public Color getFloorColor() { return floorColor; }
    public Color getCeilingColor() { return ceilingColor; }

    public void setWallColor(Color color) { wallColor = color; }
    public void setFloorColor(Color color) { floorColor = color; }
    public void setCeilingColor(Color color) { ceilingColor = color; }

    public void build()
    {
        AssetManager.loadTexture("_maze_coin", coinTexturePath);
        AssetManager.loadTexture("_maze_enemy", enemyTexturePath);

        walls = new ArrayList<>();
        floorAndCeiling = new ArrayList<>();
        coins = new ArrayList<>();
        maxCoinCount = 0;

        secondsAfterLastPathfind = 0.0;

        // add the walls for rendering
        double startPos = -MAZE_SIZE / 2.0 * CELL_SIZE + CELL_SIZE / 2.0;
        for (int i = 0; i < MAZE_SIZE; i++)
        {
            for (int j = 0; j < MAZE_SIZE; j++)
            {
                if (maze[i * MAZE_SIZE + j] == CellType.WALL)
                {
                    GameObject wall = new ColoredCube(new Vec3(startPos + j * CELL_SIZE, WALL_HEIGHT / 2, startPos + i * CELL_SIZE), new Vec3(CELL_SIZE, WALL_HEIGHT, CELL_SIZE), new Vec3(0.0), wallColor);
                    World.addGameObject(wall);
                    walls.add(wall);
                }
            }
        }

        // add the ceiling and the floor for rendering
        startPos = -MAZE_SIZE / 2.0 * CELL_SIZE + CELL_SIZE / 2.0 * FLOOR_CELL_SCALE;
        for (int i = 0; i < MAZE_SIZE / FLOOR_CELL_SCALE; i++)
        {
            for (int j = 0; j < MAZE_SIZE / FLOOR_CELL_SCALE; j++)
            {
                // floor cell
                GameObject floor = new ColoredPlain(new Vec3(startPos + j * CELL_SIZE * FLOOR_CELL_SCALE, 0.0, startPos + i * CELL_SIZE * FLOOR_CELL_SCALE), new Vec3(CELL_SIZE * FLOOR_CELL_SCALE, 0.0, CELL_SIZE * FLOOR_CELL_SCALE), new Vec3(0.0), floorColor);
                floor.setRenderingPriority(0);
                World.addGameObject(floor);
                floorAndCeiling.add(floor);

                // ceiling cell
                GameObject ceiling = new ColoredPlain(new Vec3(startPos + j * CELL_SIZE * FLOOR_CELL_SCALE, WALL_HEIGHT, startPos + i * CELL_SIZE * FLOOR_CELL_SCALE), new Vec3(CELL_SIZE * FLOOR_CELL_SCALE, 0.0, CELL_SIZE * FLOOR_CELL_SCALE), new Vec3(0.0), ceilingColor);
                ceiling.setRenderingPriority(0);
                World.addGameObject(ceiling);
                floorAndCeiling.add(ceiling);
            }
        }

        // add player, coins and enemies
        for (int i = 0; i < MAZE_SIZE; i++)
        {
            for (int j = 0; j < MAZE_SIZE; j++)
            {
                CellType cellType = maze[i * MAZE_SIZE + j];
                if (cellType == CellType.PLAYER)
                {
                    player = new Player(new Vec3(
                            -(MAZE_SIZE - 1) * CELL_SIZE / 2.0 + j * CELL_SIZE,
                            WALL_HEIGHT / 2.0 + 0.1,
                            -(MAZE_SIZE - 1) * CELL_SIZE / 2.0 + i * CELL_SIZE
                    ), new Vec3(
                            CELL_SIZE / 2.0, 0.0, CELL_SIZE / 2.0
                    ), new Vec3(0.0, 0.0, -1.0),
                            45.0
                    );
                    player.setCollidable(true);
                    World.addGameObject(player);
                }
                else if (cellType == CellType.COIN)
                {
                    Coin coin = new Coin(new Vec3(
                            -(MAZE_SIZE - 1) * CELL_SIZE / 2.0 + j * CELL_SIZE,
                             WALL_HEIGHT / 3.0,
                            -(MAZE_SIZE - 1) * CELL_SIZE / 2.0 + i * CELL_SIZE
                    ), new Vec3(
                            CELL_SIZE / 4.0, CELL_SIZE / 4.0, CELL_SIZE / 4.0
                    ),
                        "_maze_coin"
                    );
                    World.addGameObject(coin);
                    coins.add(coin);
                }
                else if (cellType == CellType.ENEMY)
                {
                    enemy = new Enemy(new Vec3(
                            -(MAZE_SIZE - 1) * CELL_SIZE / 2.0 + j * CELL_SIZE,
                             WALL_HEIGHT * 3 / 8 + 0.1,
                            -(MAZE_SIZE - 1) * CELL_SIZE / 2.0 + i * CELL_SIZE
                    ), new Vec3(
                            CELL_SIZE / 2.0, WALL_HEIGHT * 3 / 4, CELL_SIZE / 2.0
                    ),
                "_maze_enemy",
                4.0
                    );
                    World.addGameObject(enemy);
                }
            }
        }
        maxCoinCount = coins.size();
    }

    public void destroy()
    {
        walls.forEach(World::removeGameObject);
        floorAndCeiling.forEach(World::removeGameObject);
        coins.forEach(World::removeGameObject);
        World.removeGameObject(player);
        World.removeGameObject(enemy);
    }

    private void gameOver()
    {
        destroy();
        build();
    }

    private List<Cell> pathfindInMaze(Cell start, Cell end)
    {
        // source: chat gpt

        if (!isCellValid(start) || !isCellValid(end))
            return Collections.emptyList();

        Queue<Cell> queue = new LinkedList<>();
        Map<Cell, Cell> parentMap = new HashMap<>();
        Set<Cell> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        parentMap.put(start, null);

        while (!queue.isEmpty())
        {
            Cell current = queue.poll();

            if (current.equals(end))
                return reconstructPath(end, parentMap);

            for (int dir = 0; dir < 4; dir++)
            {
                Cell neighbor = switch (dir)
                {
                    case 0 -> new Cell(current.x - 1, current.y);
                    case 1 -> new Cell(current.x + 1, current.y);
                    case 2 -> new Cell(current.x, current.y - 1);
                    case 3 -> new Cell(current.x, current.y + 1);
                    default -> new Cell(-1, -1);
                };

                if (isCellValid(neighbor) && !visited.contains(neighbor))
                {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return Collections.emptyList();
    }

    private boolean isCellValid(Cell cell)
    {
        if (cell.x < 0 || cell.x >= MAZE_SIZE || cell.y < 0 || cell.y >= MAZE_SIZE)
            return false;

        int index = cell.y * MAZE_SIZE + cell.x;
        return maze[index] != CellType.WALL;
    }

    private List<Cell> reconstructPath(Cell end, Map<Cell, Cell> parentMap)
    {
        List<Cell> path = new LinkedList<>();
        for (Cell at = end; at != null; at = parentMap.get(at))
            path.addFirst(at);

        return path;
    }

    private Vec2 cell2Pos(Cell cell)
    {
        return new Vec2(
                (1 - MAZE_SIZE) * CELL_SIZE / 2.0 + cell.x * CELL_SIZE,
                (1 - MAZE_SIZE) * CELL_SIZE / 2.0 + cell.y * CELL_SIZE
        );
    }

    private Cell pos2Cell(Vec2 pos)
    {
        return new Cell(
                (int)((MAZE_SIZE * CELL_SIZE / 2.0 + pos.getX()) / CELL_SIZE),
                (int)((MAZE_SIZE * CELL_SIZE / 2.0 + pos.getY()) / CELL_SIZE)
        );
    }
}
