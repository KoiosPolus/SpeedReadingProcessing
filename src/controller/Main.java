package controller;

import processing.core.PApplet;
import sketches.SpeedReader;

public class Main {
    public static void main(String[] args) {
        String loc = "--editor-location";
        String display = "--display";
        String color = "--window-color";
        String windowLoc = "--location";
        PApplet.main(SpeedReader.class.getName());
    }
}

class testing {
    char[] couples;

    testing() {
        couples = new char[10];
        orderCouples(couples);
    }

    void orderCouples(char[] unorderedCouples) {
        // create rows for convenience
        //Assign rows to symbolic couples
    }
}