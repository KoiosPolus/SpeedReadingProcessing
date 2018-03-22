package KGUI;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.Objects;

import static processing.core.PApplet.color;
import static processing.core.PApplet.lerp;
import static processing.core.PApplet.map;

public class Region extends RegionComponent implements Executable {
    ArrayList<DropDownTree.folder> MemberList;
    Executable target = null;
    String buttonText;
    final int bezel = 10;
    private boolean hidden = false, hasMoved = false;
    private Timer execTimer;
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
        app = gui.applet;
        mouse = gui.mouse;
        mousePos = mouse.pos;
        execTimer = new Timer(app, execInterval);
    }

    static PVector getVector(int x, int y, int mode) {
        float newX = x;
        float newY = y;
        if (mode == RELATIVE) {
            newX = map(x, 0, app.width, 0, 1);
            newY = map(y, 0, app.height, 0, 1);
        }
        return new PVector(newX, newY);
    }

    final MenuBar createMenu() {
        return new MenuBar(gui, (int) pos.x, (int) pos.y, (int) posEnd.x,  (int) (pos.y + 100), ABSOLUTE);
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
            execTimer = new Timer(app, execInterval);
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
        app.noStroke();
        app.fill(color(color1, colorAlpha1));
        app.strokeWeight(boarderWidth1);
        //println(pos.x, pos.y, posEnd.x - pos.x, posEnd.y - pos.y, 0, bezel2, bezel3, 0);
        app.rect(pos.x, pos.y, posEnd.x - pos.x, posEnd.y - pos.y, 0, 0, 0, 0);
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

    class MenuComponent extends RegionComponent {

        @Override
        public void render() {

        }
    }
}

abstract class RegionComponent extends UI {
    Region region = null;
    PVector relPos, pPos, pPosEnd;
    float moveRatio = 1;

    final boolean isWithinRegion(Region r) {
        if (pos.x >= r.pos.x && posEnd.x <= r.posEnd.x && pos.y >= r.pos.y && posEnd.y <= r.posEnd.y) {
            return true;
        } else return false;
    }

    //Translations are performed directly on the passed PVectors
    final void posLerp(PVector begin, PVector end, int direction, float amt) {
        if (begin.x > end.x || begin.y > end.y) {
            throw new RuntimeException("Cannot perform posLerp() on invalid begin/end vectors");
        }
        switch (direction) {
            case NORTH:
                begin.y = lerp(end.y, begin.y, amt);
                end.y = lerp(end.y, begin.y, amt);
                break;
            case SOUTH:
                begin.y = lerp(begin.y, end.y, amt);
                end.y = lerp(begin.y, end.y, amt);
                break;
            case EAST:
                begin.x = lerp(end.x, begin.x, amt);
                end.x = lerp(end.x, begin.x, amt);
                break;
            case WEST:
                begin.x = lerp(begin.x, end.x, amt);
                end.x = lerp(begin.x, end.x, amt);
                break;
                default:
                    throw new RuntimeException("Invalid direction parameter, valid directions are: NORTH, SOUTH, EAST, WEST");
        }
    }

    final void calcRelPos(int... vals) { //(xVal, yVal, MODE, xScale, yScale, MODE)
        if (region != null) {
            if (vals.length == 6) {
                if (vals[2] == RELATIVE) {
                    float x = lerp(region.pos.x + offset2, region.posEnd.x - offset2, vals[0]/100);
                    float y = lerp(region.pos.y + offset2, region.posEnd.y - offset2, vals[1]/100);
                    pos = new PVector(x, y);
                } else {
                    pos = new PVector(region.pos.x + vals[0], region.pos.y + vals[1]);
                }
                if (vals[5] == RELATIVE) {
//                    float x = map(vals[3], 0, 100, region.pos.x + offset2, region.posEnd.x - offset2);
//                    float y = map(vals[4], 0, 100, region.pos.y + offset2, region.posEnd.y - offset2);
                    float x = lerp(region.pos.x + offset2, region.posEnd.x - offset2, vals[3]/100);
                    float y = lerp(region.pos.y + offset2, region.posEnd.y - offset2, vals[4]/100);
                    posEnd = new PVector(x, y);
                } else {
                    posEnd = new PVector(pos.x + vals[3], pos.y + vals[4]);
                }
            }
            relPos = PVector.sub(PVector.add(region.pos, region.posEnd), PVector.add(pos, posEnd)).mult(0.5F);
        }
    }

    final static void adjustRelPos() {
        PVector pRelPos = relPos;
        calcRelPos();
        PVector dRelPos = PVector.sub(relPos, pRelPos).mult(moveRatio);
        pos.add(dRelPos);
        posEnd.add(dRelPos);
    }

    final void confirmPos() {
        if (!Objects.equals(region, null)) {
            if (region.hasMoved()) {
                adjustRelPos();
                calcRelPos();
            } else if (gui.editMode && gui.activeElement == this) {
                calcRelPos();
            }
        }
    }
}