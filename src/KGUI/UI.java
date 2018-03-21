package KGUI;

import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.lerp;

public abstract class UI implements KGUIConstants {
    PVector pos, posEnd;
    private int colour, overlayColour;
    private final int depthOffset = 1, editModeDotRadii = 15, gridSize = 20;
    boolean hasColor, selectable = true, editable = true;
    static KGUI gui;
    static PApplet app;
    static KGUI.Mouse mouse;
    static PVector mousePos;

    abstract public void render();

    final public void onMouseClick() {
        if (!gui.editMode && app.mousePressed) {
            //activeElement = null;
        }
        if (isMouseOver() && app.mousePressed && (selectable || (gui.editMode && editable))) {
            gui.activeElement = this;
            //println(this);
            //println(activeElement.getClass());
        }
    }

    public void onMouseOver() {
        if (isMouseOver() || gui.activeElement == this) {
            overlayColour = app.color(255, 50);
        } else {
            overlayColour = app.color(100, 50);
        }
    }

    boolean isMouseOver() {
        //println(this.getClass());
        if (mousePos.x > pos.x && mousePos.y > pos.y && mousePos.x < posEnd.x && mousePos.y < posEnd.y) {
            return true;
        } else {
            return false;
        }
    }

    void printSettings() {
        if (this instanceof RegionComponent) {
            RegionComponent obj = (RegionComponent)this;
            System.out.println(obj + " is a member of " + obj.region);
        }
        System.out.println(this.getClass() + "'s settings have changed as follows: \nstartX: " + pos.x + "\nstartY: " + pos.y + "\nstreachX: " + (posEnd.x - pos.x) + "\nstreachY: " + (posEnd.y - pos.y));
    }

    void renderDepth() {
        PVector posRelative = PVector.sub(posEnd, pos);
        app.noStroke();
        if (hasColor) {
            System.out.println(colour);
            app.fill(colour);
            app.rect(pos.x, pos.y, posRelative.x, posRelative.y);
        }
        app.fill(overlayColour);
        app.rect(pos.x, pos.y, posRelative.x, posRelative.y);

        app.stroke(200, 200);
        app.line(pos.x, pos.y, posEnd.x, pos.y);
        app.line(pos.x, pos.y, pos.x, posEnd.y);
        app.line(posEnd.x - depthOffset, posEnd.y - depthOffset, pos.x + depthOffset, posEnd.y - depthOffset);
        app.line(posEnd.x - depthOffset, posEnd.y - depthOffset, posEnd.x - depthOffset, pos.y);
        app.stroke(0, 200);
        app.line(pos.x+depthOffset, pos.y+depthOffset, posEnd.x, pos.y+depthOffset);
        app.line(pos.x+depthOffset, pos.y+depthOffset, pos.x+depthOffset, posEnd.y);
        app.line(posEnd.x, posEnd.y, pos.x + depthOffset, posEnd.y);
        app.line(posEnd.x, posEnd.y, posEnd.x - depthOffset, pos.y+depthOffset);
    }

    void renderEditMode(KGUI.Mouse mouse) {
        if (gui.editMode) {
            app.noFill();
            app.stroke(editColor);
            app.rect(0, 0, app.width, app.height);
            if (gui.activeElement == this) {
                maintainEditedElements(this);
                if (gui.editVertex) {
                    if (PVector.dist(mousePos, pos) < editModeDotRadii && app.mousePressed) {
                        gui.editPVector = pos;
                    } else if (PVector.dist(mousePos, posEnd) < editModeDotRadii && app.mousePressed) {
                        gui.editPVector = posEnd;
                    }
                    app.noStroke();
                    app.fill(200, 0, 0, 150);
                    app.ellipse(pos.x, pos.y, editModeDotRadii, editModeDotRadii);
                    app.ellipse(posEnd.x, posEnd.y, editModeDotRadii, editModeDotRadii);
                } else {
                    mouse.setKind(MOVE);
                    if (app.mousePressed) {
                        editPos();
                    }
                }
            }
        }
    }

    void editVertex(KGUI.Mouse mouse) {
        if (gui.editPVector != null && app.mousePressed) {
            PVector newPos = mousePos.copy();
            newPos = snapToGrid(newPos);
            gui.editPVector.set(newPos);
        } else {
            gui.editPVector = null;
        }
    }

    private void editPos() {
        //if (isMouseOver() && referencePos == null) {
        //  referencePos = gui.getMouse().getPos();
        //} else if (!isMouseOver()) {
        //  referencePos = null;
        //}
        PVector referencePos = new PVector((pos.x + posEnd.x) / 2, (pos.y + posEnd.y) / 2);
        PVector deltaPos = PVector.sub(mousePos, referencePos);
        deltaPos = snapToGrid(deltaPos);
        pos.add(deltaPos);
        posEnd.add(deltaPos);
        app.line(referencePos.x, referencePos.y, mousePos.x, mousePos.y);
    }

    private PVector snapToGrid(PVector vec) {
        if (gui.snapToGrid) {
            //newPos.mult(1/gridSize);
            vec.x = PApplet.round(vec.x/gridSize)*gridSize;
            vec.y = PApplet.round(vec.y/gridSize)*gridSize;
            //newPos.mult(gridSize);
        }
        return vec;
    }

    private void maintainEditedElements(UI elem) {
        boolean isMember = false;
        for (UI e : gui.EditedElements) {
            if (elem == e) {
                isMember = true;
                break;
            }
        }
        if (!isMember) {
            gui.EditedElements.add(elem);
        }
    }

    static class Timer {
        int currentTime, prevTime;
        float seconds;
        PApplet applet;

        Timer(PApplet applet, float seconds) {
            this.seconds = seconds;
            currentTime = prevTime = applet.millis();
            this.applet = applet;
        }

        float percent() {
            currentTime = applet.millis();
            return PApplet.constrain(PApplet.map(currentTime - prevTime, 0, seconds * 1000, 0, 100), 0, 100);
        }

        boolean fired() {
            if (percent() >= 100) {
                //println("fired");
                return true;
            } else {
                return false;
            }
        }
    }
}
