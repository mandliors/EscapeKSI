package maze;

import gameobjects.shapes.ColoredCube;
import world.World;
import math.*;
import gameobjects.shapes.ColoredPlain;
import gameobjects.GameObject;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Maze implements Serializable
{
    public enum CellType { WALL, EMPTY }

    public static final String defaultMaze =
            "####################" +
            "#                  #" +
            "#  ###             #" +
            "# #   #            #" +
            "# #   #            #" +
            "# #   ##########   #" +
            "# #   #       # #  #" +
            "# #   #      #   # #" +
            "# #   #      #   # #" +
            "# ####       #  ## #" +
            "# #   #      #   # #" +
            "# #   #      #   # #" +
            "# #   #       # #  #" +
            "# #   ##########   #" +
            "# #   #            #" +
            "# #   #            #" +
            "#  ###             #" +
            "#                  #" +
            "#                  #" +
            "####################" ;

    private static final double CELL_SIZE = 4;
    private static final double WALL_HEIGHT = 5;
    private static final double FLOOR_CELL_SCALE = 0.5;

    private final CellType[] maze;
    private transient List<GameObject> objects;

    private final int MAZE_SIZE;
    private Color wallColor = new Color(0, 255, 0);
    private Color floorColor = new Color(40, 40, 40);
    private Color ceilingColor = new Color(40, 40, 40);

    public Maze(String maze) { this(convertCharToMaze(maze)); }
    public Maze(CellType[] maze) { this.MAZE_SIZE = (int)Math.sqrt(maze.length); this.maze = maze; this.objects = new ArrayList<>(); }

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
            maze.objects = new ArrayList<>();
            in.close();
            return maze;
        } catch(Exception e) { e.printStackTrace(); return null; }
    }

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
                    objects.add(wall);
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
                objects.add(floor);

                // ceiling cell
                GameObject ceiling = new ColoredPlain(new Vec3(startPos + j * CELL_SIZE * FLOOR_CELL_SCALE, WALL_HEIGHT, startPos + i * CELL_SIZE * FLOOR_CELL_SCALE), new Vec3(CELL_SIZE * FLOOR_CELL_SCALE, 0.0, CELL_SIZE * FLOOR_CELL_SCALE), new Vec3(0.0), ceilingColor);
                ceiling.setRenderingPriority(0);
                World.addGameObject(ceiling);
                objects.add(ceiling);
            }
        }
    }

    public void destroy() { objects.forEach(World::removeGameObject); }

    private static CellType[] convertCharToMaze(String maze)
    {
        int mazeSize = (int)Math.sqrt(maze.length());
        CellType[] newMaze = new CellType[mazeSize * mazeSize];

        // if the maze is invalid, use the default maze
        if (maze.length() != mazeSize * mazeSize)
            maze = defaultMaze;

        for (int i = 0; i < mazeSize; i++)
        {
            for (int j = 0; j < mazeSize; j++)
            {
                int idx = i * mazeSize + j;
                switch (maze.charAt(idx))
                {
                    case '#' -> newMaze[idx] = CellType.WALL;
                    case ' ' -> newMaze[idx] = CellType.EMPTY;
                    default -> newMaze[idx] = CellType.EMPTY;
                }
            }
        }

        return newMaze;
    }
}
