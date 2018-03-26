package KGUI;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Objects;

import static processing.core.PApplet.color;
import static processing.core.PApplet.lerp;
import static processing.core.PApplet.map;

public class Region extends Component implements Executable {
    ArrayList<DropDownTree.folder> MemberList;
    Executable target = null;
    String buttonText;
    final int bezel = 10;
    private boolean hidden = false, hasMoved = false;
    private Component.Timer execTimer;
    private float execInterval = 0.5F;
    private PVector pPos, pPosEnd;
    private int vectorType;

    Region(KGUI gui, int startX, int startY,int endX, int endY, int mode) {
        vectorType = mode;
        pos = new PVector(startX, startY);
        posEnd = new PVector(startX + endX, startY + endY);
        pPos = pos.copy();
        pPosEnd = posEnd.copy();
        hasColor = true;
        selectable = false;
        editable = false;
        this.gui = gui;
        Component.app = gui.applet;
        Component.mouse = gui.mouse;
        Component.mousePos = Component.mouse.pos;
        execTimer = new Component.Timer(Component.app, execInterval);
    }

    static PVector getVector(int x, int y, int mode) {
        float newX = x;
        float newY = y;
        if (mode == KGUIConstants.RELATIVE) {
            newX = map(x, 0, Component.app.width, 0, 1);
            newY = map(y, 0, Component.app.height, 0, 1);
        }
        return new PVector(newX, newY);
    }

    final MenuBar createMenu() {
        return new MenuBar(Component.gui, (int) pos.x, (int) pos.y, (int) posEnd.x,  (int) (pos.y + 100), KGUIConstants.ABSOLUTE);
    }

    public void render() {
        //renderDepth();
        renderBox();

        //if (activeElement == this) {
        //  //activeElement = null;
        //}
    }

    boolean hasMoved() {
        return hasMoved;
    }

    public boolean exec() {
        if (execTimer.fired()) {
            execTimer = new Component.Timer(Component.app, execInterval);
            hidden = !hidden;
            return true;
        }
        return false;
    }

    void renderBox() {
        float moveDist = posEnd.x - pos.x;
        if (execTimer.percent() < 100) {
            hasMoved = true;
            if (hidden) {
                pos.x =  lerp(pPos.x - moveDist, pPos.x, execTimer.percent()/100);
                posEnd.x =  lerp(pPosEnd.x - moveDist, pPosEnd.x, execTimer.percent()/100);
            } else {
                pos.x =  lerp(pPos.x, pPos.x - moveDist, execTimer.percent()/100);
                posEnd.x =  lerp(pPosEnd.x, pPosEnd.x - moveDist, execTimer.percent()/100);
            }
        } else {
            hasMoved = false;
        }

        //stroke(boarder1);
        Component.app.noStroke();
        Component.app.fill(PApplet.color(KGUIConstants.color1, KGUIConstants.colorAlpha1));
        Component.app.strokeWeight(KGUIConstants.boarderWidth1);
        //println(pos.x, pos.y, posEnd.x - pos.x, posEnd.y - pos.y, 0, bezel2, bezel3, 0);
        Component.app.rect(pos.x, pos.y, posEnd.x - pos.x, posEnd.y - pos.y, 0, 0, 0, 0);
        //if (isMouseOver()) {
        //  int overlay = mousePressed ? overlay2 : overlay1;
        //  fill(color(overlay, overlayAlpha));
        //  noStroke();
        //  rect(pos.x, pos.y, posEnd.x - pos.x, posEnd.y - pos.y, 0, bezel2, bezel3, 0);
        //}
    }

    public class MenuBar extends Region {

        MenuBar(KGUI gui, int startX, int startY,int endX, int endY, int mode) {
            super(gui, startX, startY,endX, endY, mode);
        }

        @Override
        public void render() {
            renderBox();
        }
    }

    class MenuComponent extends Component {

        @Override
        public void render() {

        }
    }
}