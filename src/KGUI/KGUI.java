package KGUI;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;
import static processing.core.PApplet.*;

public class KGUI implements KGUIConstants {
    Component activeElement;
    PVector editPVector;
    boolean editMode = false, isEditingVertex = false, snapToGrid = true, editVertex = false;
    public ArrayList<Component> ComponentList;
    public ArrayList<Component> EditedElements;
    public ArrayList<Region> RegionList;
    PApplet applet;
    Mouse mouse;
    Component.Timer keyTimer;

    public KGUI(PApplet parent) {
        applet = parent;
        keyTimer = new Component.Timer(applet,0);
//        applet.GUIList.add(this);
        applet.strokeJoin(ROUND);
        applet.strokeCap(ROUND);
        ComponentList = new ArrayList<Component>();
        EditedElements = new ArrayList<Component>();
        RegionList = new ArrayList<Region>();
        mouse = new Mouse(this);
        mouse.update();
    }

    public Mouse getMouse() {
        return mouse;
    }

    public void render() {
        //println("Rendering GUI with: " + GUIElementList.size() + " elements");
        mouse.update();
        keyHandler();
        for (Component e : ComponentList) {
            //for (int i = 0 ; i < ComponentList.size() ; i++) {
            //  Component e = ComponentList.get(i);
            e.onMouseOver();
            e.onMouseClick();
            e.renderEditMode(mouse);
            e.editVertex(mouse);
            e.render();
        }
    }

    public TextField textField(int x, int y, int scaleX_, int scaleY_, Region Parent) {
        TextField tf = new TextField(x, y, scaleX_, scaleY_, Parent, this);
        ComponentList.add(tf);
        return tf;
    }

    public TextField textField(int x, int y, int scaleX_, int scaleY_) {
        TextField tf = new TextField(x, y, scaleX_, scaleY_, null, this);
        ComponentList.add(tf);
        return tf;
    }

    public TextField textField(Region Parent) {
        TextField tf = new TextField(0, 0, 100, 20, Parent, this);
        ComponentList.add(tf);
        return tf;
    }

    public Button button(Region parent_, int xPercent, int yPercent, int scaleX, int scaleY, Executable target_, String... statesText) {
        Button b = new Button(parent_, xPercent, yPercent, scaleX, scaleY, target_, this, statesText);
        ComponentList.add(b);
        return b;
    }

    public Region region(int x, int y, int scaleX_, int scaleY_, int mode) {
        Region r = new Region(this, x, y, scaleX_, scaleY_, mode);
        ComponentList.add(r);
        RegionList.add(r);
        return r;
    }

    public DropDownTree dropDownTree(int x, int y, int scaleX_, int scaleY_, Region parent) {
        DropDownTree d = new DropDownTree(x, y, RELATIVE, scaleX_, scaleY_, RELATIVE, parent, this);
        ComponentList.add(d);
        return d;
    }

    public DropDownTree dropDownTree(int x, int y, int scaleX_, int scaleY_) {
        DropDownTree d = new DropDownTree(x, y, RELATIVE, scaleX_, scaleY_, RELATIVE, null, this);
        ComponentList.add(d);
        return d;
    }

    void keyHandler() {
        if (applet.keyPressed && keyTimer.fired()) {
            keyTimer = new Component.Timer(applet, 1);
            String type;
            if (applet.keyCode == 120) {
                editMode = !editMode;
                if (!editMode) {
                    postEditModeCorrections();
                }
            } else if (applet.keyCode == 121) {
                snapToGrid = !snapToGrid;
                println("snapToGrid: " + snapToGrid);
            } else if (applet.keyCode == 122) {
                editVertex = !editVertex;
                println("editVertex: " + editVertex);
            }
            if (activeElement != null && activeElement instanceof TextField) {
                TextField tf = (TextField) activeElement;
                if (applet.keyCode >= 48 && applet.keyCode <= 57) {
                    //ValidCharList.add(key);
                    type = "number";
                    tf.pushChar(applet.key, type);
                } else if (applet.keyCode >= 58 && applet.keyCode <= 90) {
                    //ValidCharList.add(key);
                    type = "string";
                    tf.pushChar(applet.key, type);
                } else if (applet.key == ' ') {
                    type = "string";
                    tf.pushChar(applet.key, type);
                } else if (applet.keyCode == BACKSPACE) {
                    tf.removeChar();
                }
            }
        }
    }

    void postEditModeCorrections() {
//        for (KGUI g : applet.GUIList) { //(g.EditedElements -> EditedElements)
            for (Component e : EditedElements) {
                e.printSettings();
                e.region = null;
                for (Region r : RegionList) {
                    if (e.isWithinRegion(r)) {
                        e.region = r;
                        e.calcRelPos();
                        println(e + " is now a member of " + r);
                        break;
                    }
                }
            }
            EditedElements.clear();
//        }
    }

    class Mouse {
        public PVector pos = new PVector(0, 0);
        boolean cursorNeedsUpdate = false;
        int kind;
        PApplet app;

        Mouse(KGUI gui) {
            app = gui.applet;
        }

        void update() {
            pos.set(app.mouseX, app.mouseY);
            if (cursorNeedsUpdate) {
                app.cursor(kind);
                cursorNeedsUpdate = false;
            } else {
                app.cursor(ARROW);
            }
        }

        void setKind(int newKind) {
            kind = newKind;
            cursorNeedsUpdate = true;
        }

        PVector getPos() {
            return pos;
        }
    }
}
