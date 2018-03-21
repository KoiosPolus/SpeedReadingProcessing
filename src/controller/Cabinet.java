package controller;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Cabinet {
    float d, h, w, rotStep;
    PVector pos;
    PApplet parent;

    Cabinet(PApplet parent, double d, double h, double w) {
        this.parent = parent;
        this.rotStep = (float) 0.01;
        pos = new PVector(parent.width/2, parent.height/2);
        this.d = (float) d;
        this.h = (float) h;
        this.w = (float) w;
    }

    public double[] getDim() {
        return new double[] {d, w, h};
    }

    public void render(PApplet parent) {
        float angle = parent.frameCount*rotStep;
        parent.pushMatrix();
        parent.translate(pos.x, pos.y);
        parent.rotateX(-PConstants.PI/6);
        parent.rotateY(angle);
        parent.box(d, w, h);
        parent.popMatrix();
    }
}
