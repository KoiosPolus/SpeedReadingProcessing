package KGUI;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.Objects;

import static processing.core.PApplet.color;
import static processing.core.PApplet.lerp;
import static processing.core.PApplet.map;

public class Region extends Component implements Executable {
    private ArrayList<Component> MemberList;
    final int bezel = 10;
    private boolean hidden = false, hasMoved = false;
    private Timer execTimer;
    private float execInterval = 0.5F;
    private PVector pPos, pPosEnd;
    private int vectorType;

    Region(KGUI gui, int startX, int startY, int endX, int endY, int mode) {
        MemberList = new ArrayList<>();
        vectorType = mode;
        pos = new PVector(startX, startY);
        posEnd = new PVector(startX + endX, startY + endY);
        pPos = pos.copy();
        pPosEnd = posEnd.copy();
        hasColor = true;
        selectable = false;
        editable = false;
        this.gui = gui;
        this.app = gui.applet;
        this.mouse = gui.mouse;
        this.mousePos = mouse.pos;
        execTimer = new Timer(app, execInterval);
    }

    PVector getVector(int x, int y, int mode) {
        float newX = x;
        float newY = y;
        if (mode == RELATIVE) {
            newX = map(x, 0, app.width, 0, 1);
            newY = map(y, 0, app.height, 0, 1);
        }
        return new PVector(newX, newY);
    }

    public final MenuBar createMenu() {
        MenuBar menu = new MenuBar(gui, this, (int) pos.x, (int) pos.y, (int) posEnd.x, (int) (pos.y + 30), ABSOLUTE);
        MemberList.add(menu);
        return menu;
    }

    @Override
    public void render() {
        this.transition();
        this.applyStyle();
        this.renderShape();
        for (Component e : this.MemberList) {
            e.render();
        }
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

    void transition() {
        float moveDist = posEnd.x - pos.x;
        if (execTimer.percent() < 100) {
            hasMoved = true;
            if (hidden) {
                pos.x = lerp(pPos.x - moveDist, pPos.x, execTimer.percent() / 100);
                posEnd.x = lerp(pPosEnd.x - moveDist, pPosEnd.x, execTimer.percent() / 100);
            } else {
                pos.x = lerp(pPos.x, pPos.x - moveDist, execTimer.percent() / 100);
                posEnd.x = lerp(pPosEnd.x, pPosEnd.x - moveDist, execTimer.percent() / 100);
            }
        } else {
            hasMoved = false;
        }
    }

    void applyStyle() {
        //stroke(boarder1);
        app.noStroke();
        app.fill(color(primaryCol, primaryAlpha));
        app.strokeWeight(boarderWidth1);
    }

    void renderShape() {
        app.rect(pos.x, pos.y, posEnd.x - pos.x, posEnd.y - pos.y, 0, 0, 0, 0);
    }

    public class MenuBar extends Region {

        MenuBar(KGUI gui, Region region, int startX, int startY, int endX, int endY, int mode) {
            super(gui, startX, startY, endX, endY, mode);
            this.region = region;
            System.out.println(pos);
            System.out.println(posEnd);
            calcRelPos(0, 0, RELATIVE, endX -  startX, 50, ABSOLUTE);
        }

        @Override
        void applyStyle() {
            app.noStroke();
            app.fill(color(0, 255, 0, secondaryAlpha));
            app.strokeWeight(boarderWidth2);
        }

        class MenuComponent extends Transmitter {

            @Override
            public void render() {
                adjustRelPos();
                transition();
                applyStyle();
                renderShape();
                for (Component e : MemberList) {
                    e.render();
                }
            }

            @Override
            boolean changeState() {
                return false;
            }
        }
    }

}

