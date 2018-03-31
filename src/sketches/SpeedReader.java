package sketches;

import processing.core.PApplet;
import processing.core.PStyle;

import java.io.*;

public class SpeedReader extends PApplet {

    @Override
    public void settings() {
        size(400, 400, P2D);
//        fullScreen(P2D);
    }

    private TimedTextParser timedTextParser;
    private String nWord = "";
    private int nTime = 0;
    private boolean paused = false;
    private int backgroundCol = color(100);

    @Override
    public void setup() {
//        textFile = new File(System.getProperty("user.dir"), "src\\TextToBeRead.txt");
        timedTextParser = new TimedTextParser(new File(System.getProperty("user.dir"), "src\\TextToBeRead.txt"));
        loadStyles();
        textAlign(CENTER, CENTER);
        textSize(height / 8);
        nTime += millis();
    }

    @Override
    public void draw() {
        background(backgroundCol);
        ellipse(mouseX, mouseY, 15, 15);

        renderWord();
        renderSentence();
        text(timedTextParser.getWpm(), width / 2, 100);
    }

    private void renderWord() {
        if (millis() >= nTime && !paused) {
            Pair<String, Float> nextSet = timedTextParser.nextInstance();
            nWord = nextSet.a;
            nTime += nextSet.b;
//            System.out.println(nWord + " : " + nextSet.b);
        }
//        style(currentStyle);
        textSize(height / 8);
        text(nWord, 0, 0, width, height);
    }

    private void renderSentence() {
        textSize(height / 24);
        String currentSentence = timedTextParser.getCurrentSentence();
        int center = currentSentence.indexOf(nWord);
        int max = currentSentence.length();
        int viewRange = 30;
        int x = width / 2;
        int y = height - height / 8;
        text(currentSentence.substring(
                constrain(center - viewRange, 0, max),
                constrain(center + viewRange, 0, max)
        ), x, y);
        setGradient(0, height - height/4, width/2, height, color(backgroundCol), color(backgroundCol, 0), Axis.X);
        setGradient(width/2, height - height/4, width, height, color(backgroundCol, 0), color(backgroundCol), Axis.X);
    }

    private void setGradient(int x1, int y1, float x2, float y2, int c1, int c2, Axis axis) {
        noFill();
        if (x1 >= x2 || y1 >= y2) throw new RuntimeException("End position must be greater than start position.");

        if (axis == Axis.Y) {  // Top to bottom gradient
            for (int i = y1; i <= y2; i++) {
                stroke(lerpColor(c1, c2, map(i, y1, y2, 0, 1)));
                line(x1, i, x2, i);
            }
        } else if (axis == Axis.X) {  // Left to right gradient
            for (int i = x1; i <= x2; i++) {
                stroke(lerpColor(c1, c2, map(i, x1, x2, 0, 1)));
                line(i, y1, i, y2);
            }
        }
    }

    private PStyle currentStyle = new PStyle();

    private void loadStyles() {
        currentStyle.textAlign = CENTER;
        currentStyle.textAlignY = CENTER;
        currentStyle.textSize = height / 8;//TODO call this on WindowResize
        currentStyle.fill = false;
        currentStyle.fillColor = PApplet.color(200);
        currentStyle.ambientB = 0;
        currentStyle.ambientG = 0;
        currentStyle.ambientR = 0;
    }

    public void keyPressed() {
        switch (keyCode) {
            case UP:
                timedTextParser.incSpeed();
                break;
            case DOWN:
                timedTextParser.decSpeed();
                break;
            case LEFT:
                timedTextParser.previousSentence();
                break;
            case RIGHT:
                timedTextParser.nextSentence();
                break;
            case 32:
                paused = !paused;
                nTime = millis();
                timedTextParser.refreshSentence();
                break;
        }
    }
}