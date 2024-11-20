package main.java.leaderboard;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Leaderboard implements Serializable
{
    public static class Record implements Serializable
    {
        public String name;
        public double time;

        public Record(String name, double time) { this.name = name; this.time = time; }
    }

    private static Leaderboard singleton = new Leaderboard();

    private List<Record> records = new ArrayList<>();

    public static Leaderboard get() { return singleton; }
    public static void addRecord(Record record)
    {
        singleton.records.add(record);
        Collections.sort(singleton.records, Comparator.comparingDouble((Record r) -> r.time));
    }
    public static List<Record> getRecords() { return singleton.records; }

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
