package KGUI;

import processing.core.PApplet;
import processing.core.PConstants;

public interface KGUIConstants extends PConstants {
    int color1 = PApplet.color(255), //-3098279, //color(100, 250, 120),
            boarder1 = -14541024, //color(50, 200, 70),
            color2 = -9459492, //color(50, 70, 200),
            boarder2 = PApplet.color(100, 120, 250),
            overlay1 = PApplet.color(200),
            overlay2 = PApplet.color(100),
            editColor = PApplet.color(255, 45, 66),
    offset1 = 15,
            offset2 = 5,
            textSpacing = 6,
            boarderWidth1 = 2,
            boarderWidth2 = 1,
            outlineWidth = 5,
            colorAlpha1 = 200,
            colorAlpha2 = 100,
            overlayAlpha1 = 150,
            overlayAlpha2 = 100,
            bezel1 = 10,
            bezel2 = 10,
            bezel3 = 10,
            bezel4 = 10;

    int ABSOLUTE = 0,
            RELATIVE = 1,
            NORTH = 2,
            SOUTH = 3,
            EAST = 4,
            WEST= 5;

    String ddtFolder = "KGUI.DropDownTree.folder";
}
