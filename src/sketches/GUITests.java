package sketches;

import KGUI.KGUI;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;
import KGUI.Region;
import KGUI.DropDownTree;
import static KGUI.KGUIConstants.RELATIVE;

public class GUITests extends PApplet{

    List<KGUI> GUIList;
    ArrayList<Cabinet> CabinetList;
    KGUI MainGui;
    PGraphics rendition;

    public void settings() {
        size(600, 600, FX2D);
//        prepareExitHandler();
    }

    public void setup() {
        //surface.placeWindow(new int[] {650, 200}, new int[] {0, 0});
        surface.setLocation(650, 200);
        CabinetList = new ArrayList<Cabinet>();
        CabinetList.add(new Cabinet(this, 5.0, 3.0, 5.0));
//        libraryWin = new LibraryWindow();
//        propertiesWin = new PropertiesWindow();
        loadMainWindowGUI();
    }

    boolean setLoc = true;
    public void pre() {
        if (setLoc) {
            frame.setLocation(500, 200);
            setLoc = false;
        }
        println("fired");
    }

    public void draw() {
        background(200);
        MainGui.render();
//        draw3DRendition();
    }

    void loadMainWindowGUI() {
        MainGui = new KGUI(this);
        Region sidePanel = MainGui.region(0, 0, width/3, height, RELATIVE);
        MainGui.button(sidePanel, 80, 0, 30, 30, sidePanel, "<", ">");
        DropDownTree ddt = MainGui.dropDownTree(5, 10, 95, 99, sidePanel);
        DropDownTree.folder mem;
        String[] folderNames = {"Ipsum", "Dolor", "Sit amet", "Ipsum", "Dolor", "Sit amet"};
        for (int i = 0; i < 2; i++) {
            mem = ddt.createFolder("Lorem");
            for (String s: folderNames) {
                mem.createFolder(s);
            }
        }
    }

    void draw3DRendition() {
        rendition = createGraphics(width, height, FX3D);
        float angle =frameCount * 0.02F;
        double[] cDim = CabinetList.get(0).getDim();

        rendition.beginDraw();
        rendition.background(157);
        rendition.lights();
        rendition.translate(width/2, height/2);
        rendition.rotateY(angle);
        rendition.rotateX(-PI/8*cos(angle));
        rendition.rotateZ(-PI/8*sin(angle));

        rendition.box(cDim[0], cDim[1], cDim[2]);

        rendition.endDraw();
        image(rendition, 0, 0);
    }
}
