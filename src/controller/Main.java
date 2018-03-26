package controller;

import processing.core.PApplet;
import sketches.GUITests;
import sketches.SpeedReader;

public class Main {
    public static void main(String[] args) {
        String loc = "--editor-location";
        String display  = "--display";
        String color = "--window-color";
        String windowLoc = "--location";
//        PApplet.runSketch(new String[] {""}, new GUITests());
//        PApplet.runSketch(new String[] {""}, new SpeedReader());
//        PApplet.main(new String[]{"SpeedReader"});
        PApplet.main("sketches.SpeedReader");
    }
}