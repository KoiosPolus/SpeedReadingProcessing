package KGUI;

import javafx.scene.chart.Axis;
import processing.core.PApplet;

import java.util.Arrays;

import static processing.core.PApplet.abs;
import static processing.core.PApplet.color;
import static processing.core.PApplet.println;

public class Button extends Activatable {
    private String buttonText;
    private String[] stateText;
    int bezel = 10, state = 0, states;

    Button(Region region_, int xPercent, int yPercent, int sizeX, int sizeY, Executable target_, KGUI gui_, String... statesText) {
        region = region_;
        target = target_;
        calcRelPos(xPercent, yPercent, RELATIVE, sizeX, sizeY, ABSOLUTE);
        moveRatio = 0.9F;
        hasColor = true;
        states = statesText.length;
        stateText = statesText;
        buttonText = stateText[0];
        gui = gui_;
        app = gui.applet;
        mouse = gui.mouse;
        mousePos = mouse.pos;
    }

    Button() {
    }

    public void render() {
        //renderDepth();
        renderShape();
        renderText();
        confirmPos();
        registerClick();

        if (isMouseOver()) {
            mouse.setKind(HAND);
        }
    }

    void registerClick() {
        if (gui.activeElement == this && !gui.editMode) {
            if (target.exec()) {
                PApplet.println(this + " firing");
                state++;
                state %= states;
            }
            gui.activeElement = null;
        }
    }

    void renderShape() {
        //stroke(boarder1);
        //println(app);
        println(pos);
        app.strokeWeight(boarderWidth2);
        rawShape();
        app.fill(color(overlay1, overlayAlpha1));
        if (isMouseOver()) {
            int overlay = app.mousePressed ? overlay1 : overlay2;
            app.fill(color(overlay, overlayAlpha1));
            rawShape();
        }
    }

    private void rawShape() {
        app.noStroke();
        if (state == 0) {
            app.noFill();
        } else {
            app.fill(color(color1, colorAlpha1));
        }
        app.rect(pos.x, pos.y, posEnd.x - pos.x, posEnd.y - pos.y, bezel1, bezel2, bezel3, bezel4);
    }

    void renderText() {
        app.fill(0);
        app.textSize(abs(posEnd.y - pos.y - textSpacing));
        app.textAlign(CENTER, TOP);
        buttonText = stateText[state];
        app.text(buttonText, pos.x, pos.y, posEnd.x - pos.x, posEnd.y - pos.y);
    }
}