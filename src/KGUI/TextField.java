package KGUI;

import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.abs;
import static processing.core.PApplet.textWidth;

public class TextField extends Component {
    Timer typeCursorBlinkTimer;
    int typeCursorBlinkTime = 5;
    float ctpo = 0;
    //Cursor Text Position Offset
    boolean isRenderingTypeCursor = false;
    ArrayList<Character> embeddedText;
    String restrictedType = null;

    TextField(int x, int y, int endX, int endY, Region parent_, KGUI gui_) {
        pos = new PVector(x, y);
        posEnd = new PVector(x + endX, y + endY);
        hasColor = false;
        typeCursorBlinkTimer = new Timer(app, typeCursorBlinkTime);
        embeddedText = new ArrayList<>();
        gui = gui_;
        app = gui.applet;
        mouse = gui.mouse;
        mousePos = mouse.pos;
        region = parent_;
        calcRelPos();
    }

    public void render() {
        renderDepth();
        renderText();
        confirmPos();
        if (isMouseOver()) {
            mouse.setKind(TEXT);
        }
        if (gui.activeElement == this) {
            renderTypeCursor();
        }
    }

    void renderTypeCursor() {
        int yOffSet = 3, xOffSet = 5;
        if (typeCursorBlinkTimer.fired()) {
            //println("fired: " + isRenderingTypeCursor);
            typeCursorBlinkTimer = new Timer(app, typeCursorBlinkTime);
            isRenderingTypeCursor = !isRenderingTypeCursor;
        }

        if (isRenderingTypeCursor) {
            //println(textWidth("d"));
            app.line(pos.x + xOffSet + ctpo, pos.y + yOffSet, pos.x + xOffSet + ctpo, posEnd.y - yOffSet);
        }
    }


    void pushChar(char a, String type) {
        if (type == restrictedType || restrictedType == null) {
            embeddedText.add(a);
        }
    }

    void removeChar() {
        if (embeddedText.size() > 0) {
            embeddedText.remove(embeddedText.size() - 1);
        }
    }

    void renderText() {
        String embeddedTextString = "";
        for (int i = 0; i < embeddedText.size(); i++) {
            embeddedTextString += embeddedText.get(i);
        }
        app.fill(0);
        app.textAlign(LEFT, TOP);
        app.textSize(abs(posEnd.y - pos.y - 6));
        ctpo = textWidth(embeddedTextString);
        app.text(embeddedTextString, pos.x + 5, pos.y);
        //text(embeddedTextString, pos.x+5, pos.y, posEnd.x, posEnd.y);
    }
}