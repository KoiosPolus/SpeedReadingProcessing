package KGUI;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;
import java.util.List;
import KGUI.Component.Timer;

import static processing.core.PApplet.*;

public class KGUI implements KGUIConstants {
    Component activeElement;
    PVector editPVector;
    boolean editMode = false, isEditingVertex = false, snapToGrid = true, editVertex = false;
    public List<Component> UIElementList;
    public List<Component> EditedElements;
    public List<Region> RegionList;
    PApplet applet;
    Mouse mouse;
    Timer keyTimer;

    public KGUI(PApplet parent) {
        applet = parent;
        keyTimer = new Timer(applet,0);
        applet.strokeJoin(ROUND);
        applet.strokeCap(ROUND);
        UIElementList = new ArrayList<>();
        EditedElements = new ArrayList<>();
        RegionList = new ArrayList<>();
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
        for (Component e : UIElementList) {
            e.onMouseOver();
            e.onMouseClick();
            e.renderEditMode(mouse);
            e.editVertex(mouse);
            e.render();
        }
    }

    public TextField textField(int x, int y, int scaleX_, int scaleY_, Region Parent) {
        TextField tf = new TextField(x, y, scaleX_, scaleY_, Parent, this);
        UIElementList.add(tf);
        return tf;
    }

    public TextField textField(int x, int y, int scaleX_, int scaleY_) {
        TextField tf = new TextField(x, y, scaleX_, scaleY_, null, this);
        UIElementList.add(tf);
        return tf;
    }

    public TextField textField(Region Parent) {
        TextField tf = new TextField(0, 0, 100, 20, Parent, this);
        UIElementList.add(tf);
        return tf;
    }

    public Button button(Region parent_, int xPercent, int yPercent, int scaleX, int scaleY, Executable target_, String... statesText) {
        Button b = new Button(parent_, xPercent, yPercent, scaleX, scaleY, target_, this, statesText);
        UIElementList.add(b);
        return b;
    }

    public Region region(int x, int y, int scaleX_, int scaleY_, int mode) {
        Region r = new Region(this, x, y, scaleX_, scaleY_, mode);
        UIElementList.add(r);
        RegionList.add(r);
        return r;
    }

    public DropDownTree dropDownTree(int x, int y, int scaleX_, int scaleY_, Region parent) {
        DropDownTree d = new DropDownTree(x, y, RELATIVE, scaleX_, scaleY_, RELATIVE, parent, this);
        UIElementList.add(d);
        return d;
    }

    public DropDownTree dropDownTree(int x, int y, int scaleX_, int scaleY_) {
        DropDownTree d = new DropDownTree(x, y, RELATIVE, scaleX_, scaleY_, RELATIVE, null, this);
        UIElementList.add(d);
        return d;
    }

    void keyHandler() {
        if (applet.keyPressed && keyTimer.fired()) {
            keyTimer = new Timer(applet, 1);
            String type;
            if (applet.keyCode == 120) {
                editMode = !editMode;
                if (editMode == false) {
                    postEditModeCorrections();
                }
            } else if (applet.keyCode == 121) {
                snapToGrid = !snapToGrid;
                applet.println("snapToGrid: " + snapToGrid);
            } else if (applet.keyCode == 122) {
                editVertex = !editVertex;
                applet.println("editVertex: " + editVertex);
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
                if (e instanceof Component) {
                    Component re = (Component) e;
                    re.region = null;
                    for (Region r : RegionList) {
                        if (re.isWithinRegion(r)) {
                            re.region = r;
                            re.calcRelPos();
                            println(e + " is now a member of " + r);
                            break;
                        }
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
////KGUI.UI tempElement;
//void mousePressed() {
//  if (activeElement != null && activeElement instanceof KGUI.TextField) {
//    activeElement = null;
//  }
//}
////void mouseDragged() {
////  activeElement = tempElement;
////}
//void mouseReleased() {
//  if (activeElement != null && activeElement instanceof KGUI.Button || editMode) {
//    activeElement = null;
//  }
//}


