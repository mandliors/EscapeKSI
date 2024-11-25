package main.java.leaderboard;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Leaderboard implements Serializable
{
    /**
     * Stores one record in the leaderboard (name and time)
     */
    public static class Record implements Serializable
    {
        /**
         * Name of the player
         */
        public String name;
        /**
         * How long it took for the win
         */
        public double time;

        /**
         * Creates a record with the given name and time
         */
        public Record(String name, double time) { this.name = name; this.time = time; }
    }

    /**
     * The singleton object (the leaderboard should only have one instance)
     */
    private static Leaderboard singleton = new Leaderboard();

    /**
     * The list of the records in the table
     */
    private List<Record> records = new ArrayList<>();

    /**
     * Returns the singleton object
     */
    public static Leaderboard get() { return singleton; }

    /**
     * Adds a record to the record list
     */
    public static void addRecord(Record record)
    {
        singleton.records.add(record);
        Collections.sort(singleton.records, Comparator.comparingDouble((Record r) -> r.time));
    }
    public static List<Record> getRecords() { return singleton.records; }

    /**
     * Loads a leaderboard from a file (assigns it to the singleton instance)
     * @param path The path for the leaderboard
     */
    public static void loadFromFile(String path)
    {
        try
        {
            FileInputStream f = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(f);
            singleton = (Leaderboard)in.readObject();
            in.close();
        } catch(Exception e) { e.printStackTrace(); }
    }
    /**
     * Saves a leaderboard to a file
     * @param path The path for the output leaderboard
     */
    public static void saveToFile(String path)
    {
        try
        {
            File file = new File(path);
            File parentDirs = file.getParentFile();

            if (parentDirs != null && !parentDirs.exists())
                parentDirs.mkdirs();

            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(f);
            out.writeObject(singleton);
            out.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }
}
