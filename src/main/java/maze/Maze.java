package main.java.maze;

import main.java.actors.Enemy;
import main.java.actors.Player;
import main.java.assets.AssetManager;
import main.java.coin.Coin;
import main.java.collision.Collision;
import main.java.gameobjects.shapes.ColoredCube;
import main.java.input.Input;
import main.java.leaderboard.Leaderboard;
import main.java.meth.Meth;
import main.java.meth.Vec2;
import main.java.meth.Vec3;
import main.java.rendering.Renderer;
import main.java.rendering.RendererString;
import main.java.gameobjects.shapes.ColoredPlain;
import main.java.gameobjects.GameObject;
import main.java.screens.GameScreen;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;
import java.util.List;

public class Maze implements Serializable
{
    /**
     * The types the cells can have
     */
    public enum CellType { WALL, EMPTY, COIN, PLAYER, ENEMY }

    /**
     * Helper class for storing cells and comparing them
     */
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

    /**
     * The default maze
     */
    public static final String defaultMaze =
            "####################" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "#                  #" +
            "####################" ;

    /**
     * Constant for cell size
     */
    private static final double CELL_SIZE = 4;
    /**
     * Constant for wall height
     */
    private static final double WALL_HEIGHT = 4;
    /**
     * Constant for floor cell scale
     */
    private static final double FLOOR_CELL_SCALE = 0.5;
    /**
     * Constant for the max distance below which positions are considered equal (used by the enemy)
     */
    private static final double MAX_DIST_EPSILON = 0.2;

    /**
     * The actual maze data
     */
    private final CellType[] maze;
    /**
     * Path for the enemy texture
     */
    private final String[] enemyTexturePaths;
    /**
     * Enemy texture frame duration
     */
    private final double enemyTextureDuration;
    /**
     * Path for the coin texture
     */
    private final String coinTexturePath;
    /**
     * Path for the enemy sound
     */
    private final String enemySoundPath;
    /**
     * Path for the coin sound
     */
    private final String coinSoundPath;
    /**
     * Path for the death sound
     */
    private final String deathSoundPath;
    /**
     * Wall gameobjects
     */
    private transient List<GameObject> walls;
    /**
     * Floor and ceiling gameobjects
     */
    private transient List<GameObject> floorAndCeiling;
    /**
     * The list of coins in the maze
     */
    private transient List<Coin> coins;
    /**
     * The player object
     */
    private transient Player player;
    /**
     * The enemy object
     */
    private transient Enemy enemy;
    /**
     * The coin count on the untouched maze
     */
    private transient int maxCoinCount;
    /**
     * Time counter need for saving the time when the player wins
     */
    private transient double time;
    /**
     * The target cell for the enemy
     */
    private transient Cell enemyTargetCell;

    /**
     * The size of the loaded maze (the maze is a square)
     */
    private final int MAZE_SIZE;
    /**
     * The color of the wall
     */
    private Color wallColor;
    /**
     * The color of the floor
     */
    private Color floorColor;
    /**
     * The color of the ceiling
     */
    private Color ceilingColor;

    /**
     * Creates a maze
     * @param mazeString The maze data as a string
     * @param wallColor The color of the walls
     * @param floorColor The color of the floor
     * @param ceilingColor The color of the ceiling
     * @param enemyTexturePaths The path for the enemy texture
     * @param enemyTextureDuration The duration of the enemy frames
     * @param coinTexturePath The path for the coin texture
     * @param enemySoundPath The path for the enemy sound
     * @param coinSoundPath The path for the coin pickup sound
     * @param deathSoundPath The path for the death sound
     */
    public Maze(String mazeString, Color wallColor, Color floorColor, Color ceilingColor, String[] enemyTexturePaths, double enemyTextureDuration, String coinTexturePath, String enemySoundPath, String coinSoundPath, String deathSoundPath)
    {
        MAZE_SIZE = (int)Math.sqrt(mazeString.length());
        maze = new CellType[MAZE_SIZE * MAZE_SIZE];
        this.wallColor = wallColor;
        this.floorColor = floorColor;
        this.ceilingColor = ceilingColor;
        this.enemyTexturePaths = enemyTexturePaths;
        this.enemyTextureDuration = enemyTextureDuration;
        this.coinTexturePath = coinTexturePath;
        this.enemySoundPath = enemySoundPath;
        this.coinSoundPath = coinSoundPath;
        this.deathSoundPath = deathSoundPath;

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

    /**
     * Updates the maze (moves objects, checks collisions, win/lost etc.)
     * @return Returns if the game should end (ESCAPE was pressed, name was not input after win etc.)
     */
    public boolean update(double dt)
    {
        // go back to the main menu
        if (Input.isKeyPressed(KeyEvent.VK_ESCAPE))
        {
            AssetManager.getSound("_maze_enemySound").stop();
            AssetManager.getSound("_maze_coinSound").stop();
            return false;
        }

        // update gameobjects
        walls.forEach((var w) -> w.update(dt));
        floorAndCeiling.forEach((var w) -> w.update(dt));
        coins.forEach((var w) -> w.update(dt));
        player.update(dt);
        enemy.update(dt);
        time += dt;

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
            Clip coinSound = AssetManager.getSound("_maze_coinSound");
            coinSound.setMicrosecondPosition(0);
            coinSound.start();

            // all coins have been collected
            if (coins.isEmpty())
            {
                AssetManager.getSound("_maze_enemySound").stop();
                AssetManager.getSound("_maze_coinSound").stop();

                String input = JOptionPane.showInputDialog(
                        null,
                        "Enter your name:",
                        "Save Result",
                        JOptionPane.QUESTION_MESSAGE
                );
                if (input != null)
                {
                    Leaderboard.addRecord(new Leaderboard.Record(input, time));
                    return false;
                }
            }
        }

        // check player-enemy collision, and update enemy direction if 1 second has passed
        Cell enemyCell = pos2Cell(enemy.getPosition().xz());
        Cell playerCell = pos2Cell(player.getPosition().xz());

        // check if enemy went into the wall (in case of low frame rate)
        if (getCell(enemyCell.x, enemyCell.y) == CellType.WALL)
        {
            // reset enemy (put back to its start cell)
            for (int i = 0; i < MAZE_SIZE; i++)
            {
                for (int j = 0; j < MAZE_SIZE; j++)
                {
                    if (getCell(j, i) == CellType.ENEMY)
                    {
                        enemy = new Enemy(new Vec3(
                                -(MAZE_SIZE - 1) * CELL_SIZE / 2.0 + j * CELL_SIZE,
                                WALL_HEIGHT * 3 / 8 + 0.1,
                                -(MAZE_SIZE - 1) * CELL_SIZE / 2.0 + i * CELL_SIZE
                        ), new Vec3(
                                CELL_SIZE / 2.0, WALL_HEIGHT * 3 / 4, CELL_SIZE / 2.0
                        ),
                                "_maze_enemyTexture",
                                4.0
                        );
                        enemyCell = pos2Cell(enemy.getPosition().xz());
                    }
                }
            }
        }
        // check if player and enemy collided
        if (enemyCell.equals(playerCell))
            gameOver();
        // close enough to target cell, time to find the new target cell
        else if (enemy.getPosition().xz().distance(cell2Pos(enemyTargetCell)) < MAX_DIST_EPSILON)
        {
            List<Cell> path = pathfindInMaze(enemyCell, playerCell);

            // no path
            if (path == null) return true;
            // game over
            if (path.isEmpty())
            {
                gameOver();
                return true;
            }

            enemyTargetCell = path.get(1);
            if (enemyCell.y > enemyTargetCell.y)
                enemy.setMoveDirection(Enemy.MoveDirection.UP);
            else if (enemyCell.x < enemyTargetCell.x)
                enemy.setMoveDirection(Enemy.MoveDirection.RIGHT);
            else if (enemyCell.y < enemyTargetCell.y)
                enemy.setMoveDirection(Enemy.MoveDirection.DOWN);
            else if (enemyCell.x > enemyTargetCell.x)
                enemy.setMoveDirection(Enemy.MoveDirection.LEFT);
        }

        // play sound if enemy is in sight
        int dx = enemyCell.x - playerCell.x;
        int dy = enemyCell.y - playerCell.y;
        Clip enemySound = AssetManager.getSound("_maze_enemySound");
        // check if the sound is already playing and stop if the enemy is not in sight
        if (dx != 0 && dy != 0)
            enemySound.stop();
        else if (!enemySound.isRunning() && (dx != 0 || dy != 0))
        {
            boolean inSight = true;

            // same column
            if (dx == 0)
            {
                int step = enemyCell.y > playerCell.y ? 1 : -1;
                for (int y = playerCell.y; y != enemyCell.y; y += step)
                {
                    if (getCell(playerCell.x, y) == CellType.WALL)
                    {
                        inSight = false;
                        break;
                    }
                }
            }
            // same row
            else
            {
                int step = enemyCell.x > playerCell.x ? 1 : -1;
                for (int x = playerCell.x; x != enemyCell.x; x += step)
                {
                    if (getCell(x, playerCell.y) == CellType.WALL)
                    {
                        inSight = false;
                        break;
                    }
                }
            }

            if (inSight)
            {
                enemySound.setMicrosecondPosition(0);
                enemySound.start();
            }
        }

        Renderer.addString(new RendererString(
                String.format("Coins: %d/%d", maxCoinCount - coins.size(), maxCoinCount),
                Color.YELLOW,
                false
        ));

        return true;
    }

    /**
     * Renders all the gameobjects of the maze
     */
    public void render()
    {
        walls.forEach(Renderer::addGameObject);
        floorAndCeiling.forEach(Renderer::addGameObject);
        coins.forEach(Renderer::addGameObject);
        Renderer.addGameObject(player);
        Renderer.addGameObject(enemy);
    }

    /**
     * Saves the maze to a file (.maze is the recommended extension)
     * @param path Path for the output file
     */
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

    /**
     * Loads a maze from file (.maze if the recommended extension)
     * @param path The path for the maze file
     */
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

    /**
     * Returns the player object
     */
    public Player getPlayer() { return player; }

    /**
     * Returns the type of the cell at a specific index
     */
    public CellType getCell(int x, int y)
    {
        if (!Meth.isBetween(x, 0, MAZE_SIZE - 1) || !Meth.isBetween(y, 0, MAZE_SIZE - 1)) return CellType.EMPTY;
        else return maze[y * MAZE_SIZE + x];
    }

    /**
     * Sets the type of the cell at a specific index
     */
    public void setCell(int x, int y, CellType type)
    {
        if (!Meth.isBetween(x, 0, MAZE_SIZE - 1) || !Meth.isBetween(y, 0, MAZE_SIZE - 1)) return;

        maze[y * MAZE_SIZE + x] = type;
        build();
    }

    /**
     * Returns the color of the wall
     */
    public Color getWallColor() { return wallColor; }
    /**
     * Returns the color of the floor
     */
    public Color getFloorColor() { return floorColor; }
    /**
     * Returns the color of the ceiling
     */
    public Color getCeilingColor() { return ceilingColor; }

    /**
     * Sets the color of the wall
     */
    public void setWallColor(Color color) { wallColor = color; }
    /**
     * Sets the color of the floor
     */
    public void setFloorColor(Color color) { floorColor = color; }
    /**
     * Sets the color of the ceiling
     */
    public void setCeilingColor(Color color) { ceilingColor = color; }

    /**
     * Builds the maze: creates the gameobjects based on the maze data and loads all the required resources
     */
    public void build()
    {
        AssetManager.loadTexture("_maze_enemyTexture", enemyTextureDuration, enemyTexturePaths);
        AssetManager.loadTexture("_maze_coinTexture", coinTexturePath);
        AssetManager.loadSound("_maze_enemySound", enemySoundPath);
        AssetManager.loadSound("_maze_coinSound", coinSoundPath);
        AssetManager.loadSound("_maze_deathSound", deathSoundPath);

        walls = new ArrayList<>();
        floorAndCeiling = new ArrayList<>();
        coins = new ArrayList<>();
        maxCoinCount = 0;
        time = 0.0;

        // add the walls for rendering
        double startPos = -MAZE_SIZE / 2.0 * CELL_SIZE + CELL_SIZE / 2.0;
        for (int i = 0; i < MAZE_SIZE; i++)
        {
            for (int j = 0; j < MAZE_SIZE; j++)
            {
                if (maze[i * MAZE_SIZE + j] == CellType.WALL)
                {
                    GameObject wall = new ColoredCube(new Vec3(startPos + j * CELL_SIZE, WALL_HEIGHT / 2, startPos + i * CELL_SIZE), new Vec3(CELL_SIZE, WALL_HEIGHT, CELL_SIZE), new Vec3(0.0), wallColor);
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
                floorAndCeiling.add(floor);

                // ceiling cell
                GameObject ceiling = new ColoredPlain(new Vec3(startPos + j * CELL_SIZE * FLOOR_CELL_SCALE, WALL_HEIGHT, startPos + i * CELL_SIZE * FLOOR_CELL_SCALE), new Vec3(CELL_SIZE * FLOOR_CELL_SCALE, 0.0, CELL_SIZE * FLOOR_CELL_SCALE), new Vec3(0.0), ceilingColor);
                ceiling.setRenderingPriority(0);
                floorAndCeiling.add(ceiling);
            }
        }

        // add the player, coins and the enemy
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
                            CELL_SIZE / 2.0, WALL_HEIGHT / 2.0, CELL_SIZE / 2.0
                    ), new Vec3(0.0, 0.0, -1.0),
                            GameScreen.FOV
                    );
                    player.setCollidable(true);
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
                        "_maze_coinTexture"
                    );
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
                "_maze_enemyTexture",
                4.0
                    );
                }
            }
        }
        maxCoinCount = coins.size();
        enemyTargetCell = pos2Cell(enemy.getPosition().xz());
    }

    /**
     * Game is over, play death sound and restart the game
     */
    private void gameOver()
    {
        AssetManager.getSound("_maze_enemySound").stop();
        AssetManager.getSound("_maze_coinSound").stop();

        Clip sound = AssetManager.getSound("_maze_deathSound");
        sound.setMicrosecondPosition(0);
        sound.start();
        build();
    }

    /**
     * Finds the shortest path in the maze between the start and the end cell using the BFS algorithm
     * @return Returns the list of cells along the path (start and end cells included)
     */
    private List<Cell> pathfindInMaze(Cell start, Cell end)
    {
        // source: chat gpt

        if (!isCellValidAndNotWall(start) || !isCellValidAndNotWall(end))
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

                if (isCellValidAndNotWall(neighbor) && !visited.contains(neighbor))
                {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * Returns if the cell is valid and not a wall
     */
    private boolean isCellValidAndNotWall(Cell cell)
    {
        if (cell.x < 0 || cell.x >= MAZE_SIZE || cell.y < 0 || cell.y >= MAZE_SIZE)
            return false;

        int index = cell.y * MAZE_SIZE + cell.x;
        return maze[index] != CellType.WALL;
    }

    /**
     * Helper function to reconstruct a path used by the pathfind algorithm
     */
    private List<Cell> reconstructPath(Cell end, Map<Cell, Cell> parentMap)
    {
        List<Cell> path = new LinkedList<>();
        for (Cell at = end; at != null; at = parentMap.get(at))
            path.addFirst(at);

        return path;
    }

    /**
     * Converts a cell to a position
     * @return The center of the cell in world space
     */
    private Vec2 cell2Pos(Cell cell)
    {
        return new Vec2(
                (1 - MAZE_SIZE) * CELL_SIZE / 2.0 + cell.x * CELL_SIZE,
                (1 - MAZE_SIZE) * CELL_SIZE / 2.0 + cell.y * CELL_SIZE
        );
    }

    /**
     * Returns the cell in the maze the given position corresponds to (might be invalid if it is outside of the maze
     */
    private Cell pos2Cell(Vec2 pos)
    {
        return new Cell(
                (int)((MAZE_SIZE * CELL_SIZE / 2.0 + pos.getX()) / CELL_SIZE),
                (int)((MAZE_SIZE * CELL_SIZE / 2.0 + pos.getY()) / CELL_SIZE)
        );
    }
}
