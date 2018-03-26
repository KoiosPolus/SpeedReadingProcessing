package KGUI;

import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.*;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.map;

public class DropDownTree extends Component {
    ArrayList<folder> MemberList;
    int scale = 14;

    DropDownTree(int x, int y, int MODE1, int endX, int endY, int MODE2, Region parent_, KGUI gui) {
        //pos = new PVector(x, y);
        //posEnd = new PVector(x + endX, y + endY);
        hasColor = false;
        selectable = false;
        MemberList = new ArrayList<folder>();
        this.gui = gui;
        app = gui.applet;
        mouse = gui.mouse;
        mousePos = mouse.pos;
        region = parent_;
        calcRelPos(x, y, MODE1, endX, endY, MODE2);
    }

    public folder createFolder(String title) {
        folder newFolder = createSubFolder(null, title);
        MemberList.add(newFolder);
        return newFolder;
    }

    folder createSubFolder(folder parent, String title) {
        return new folder(this, parent, title);
    }

    public void render() {
        //renderDepth();
        confirmPos();
        drawBox();
        drawMembers();
    }

    void drawMembers() {
        PVector origin = pos.copy();
        PVector step = new PVector(0, scale + offset2);
        for (folder m : MemberList) {
            step.y = m.render(new PVector(origin.x + offset1, origin.y + offset1)) + offset2;
            origin.add(step);
        }
    }

    void drawBox() {
        app.stroke(overlay2, overlayAlpha2);
//        app.strokeWeight(outlineWidth);
        app.fill(overlay1, overlayAlpha2);
        app.rect(pos.x, pos.y, posEnd.x - pos.x, posEnd.y - pos.y);
    }

    public class folder extends Button implements Executable {
        folder parent;
        ArrayList<folder> children;
        DropDownTree enclosure;
        int r, folderHeight;
        float execInterval = 0.2F;
        PVector[] vertices;
        Timer execTimer;
        String title;
//        KGUI gui;

        folder(DropDownTree enclosure_, folder parent_, String title_) {
            gui = enclosure_.gui;
            title = title_;
            enclosure = enclosure_;
            parent = parent_;
            app = enclosure.app;
            mouse = enclosure.mouse;
            mousePos = mouse.pos;
            children = new ArrayList<folder>();
            target = this;
            vertices = new PVector[3];
            selectable = true;
            states = 2;
            state = 0;
            execTimer = new Timer(app, execInterval);
            pos = new PVector(0, 0);
            posEnd = new PVector(0, 0);
            r = (int) (enclosure.scale / 2.0);
        }

        public boolean exec() {
            if (execTimer.fired()) {
                execTimer = new Timer(app, execInterval);
                return true;
            }
            return false;
        }

        public folder createFolder(String title) {
            folder newFolder = createSubFolder(this, title);
            this.add(newFolder);
            return newFolder;
        }

        public void add(folder newElm) {
            this.children.add(newElm);
        }

        int render(PVector origin) {
            folderHeight = r * 2;
            pos.set(-r, -r).add(origin);
            posEnd.set(r, r).add(origin);

            app.pushMatrix();
            app.translate(origin.x, origin.y);
            if (state == 1) {
                app.rotate(map(execTimer.percent(), 0, 100, 0, PI / 2));
            } else if (state == 0) {
                app.rotate(map(execTimer.percent(), 0, 100, PI / 2, 0));
            }
            renderShape();
            app.popMatrix();
            renderText();
            drawChildren();
            return folderHeight;
        }

        void renderShape() {
            registerClick();

            app.fill(color2);
            app.strokeWeight(boarderWidth2);
            app.stroke(boarder2);

            float angleInc = 2 * PI / vertices.length;
            for (int i = 0; i < vertices.length; i++) {
                float a = angleInc * i;
                vertices[i] = new PVector(r * cos(a), r * sin(a));
            }
            app.triangle(vertices[0].x, vertices[0].y, vertices[1].x, vertices[1].y, vertices[2].x, vertices[2].y);
            if (isMouseOver()) {
                int overlay = app.mousePressed ? overlay1 : overlay2;
                if (app.mousePressed && selectable) gui.activeElement = this;
                app.fill(color(overlay, overlayAlpha1));
                app.triangle(vertices[0].x, vertices[0].y, vertices[1].x, vertices[1].y, vertices[2].x, vertices[2].y);
            }
        }

        void drawChildren() {
            PVector start = pos.copy();
            PVector step = new PVector(0, r * 2 + offset2);
            int childrenDisplayed = 0;
            if (state == 0) {
                childrenDisplayed = (int) map(execTimer.percent(), 0, 100, children.size(), 0);
            } else if (state == 1) {
                childrenDisplayed = (int) map(execTimer.percent(), 0, 100, 0, children.size());
            }
            for (int i = 0; i < childrenDisplayed; i++) {
                folder c = children.get(i);
                start.add(step);
                folderHeight += c.render(new PVector(start.x + offset1, start.y + offset2)) + offset2;
            }
        }

        void renderText() {
            app.fill(0);
            app.textSize(abs(r * 2));
            app.textAlign(LEFT, CENTER);
            app.text(title, posEnd.x + offset2, pos.y - offset2, enclosure.posEnd.x - posEnd.x - offset2, posEnd.y - pos.y + offset2);
        }
    }
}
