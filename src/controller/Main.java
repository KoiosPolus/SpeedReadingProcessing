package controller;

import processing.core.PApplet;
import sketches.GUITests;
import sketches.SpeedReader;
import sketches.TextParser;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String loc = "--editor-location";
        String display = "--display";
        String color = "--window-color";
        String windowLoc = "--location";

//        TextParser textParser = new TextParser(new File(System.getProperty("user.dir"), "src\\WordFrequencies.csv"));
//        for (int i = 0; i < 3; i++) {
////            textParser.nextSentence();
//            String nextWord = textParser.nextWord();
//            if (nextWord == null) break;
////                System.out.println(nextWord);
//        }

        PApplet.main(SpeedReader.class.getName());

    }
}