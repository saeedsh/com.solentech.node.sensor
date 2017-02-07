package com.solentech.node.sensor;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Saeed Shariati on 2/7/17.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0)
            return;
        try {
            SensorAPI sensor = new SensorAPI(args[0]);
            ArrayList<Tag> tags = sensor.ReadTags();
            System.out.println("=============================");
            System.out.println("Number of tags: " + tags.size());
            System.out.println("-----------------------------");
            for (Tag t : tags) {
                System.out.println(t.toString());
            }
            System.out.println("=============================");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
