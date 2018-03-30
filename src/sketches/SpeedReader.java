package sketches;

import processing.core.PApplet;
import processing.core.PStyle;
import processing.data.FloatDict;

import java.io.*;
import java.util.*;

public class SpeedReader extends PApplet {

    @Override
    public void settings() {
//        size(400, 400, P2D);
        fullScreen(P2D, 3);
    }

    private TimedTextParser timedTextParser;
    private String nWord = "";
    private int nTime = 0;
    private boolean paused = false;

    @Override
    public void setup() {
//        textFile = new File(System.getProperty("user.dir"), "src\\TextToBeRead.txt");
        timedTextParser = new TimedTextParser(new File(System.getProperty("user.dir"), "src\\TextToBeRead.txt"));
        loadStyles();
        textAlign(CENTER, CENTER);
        textSize(height / 8);
        background(100);
        nTime += millis();
    }

    @Override
    public void draw() {
        background(100);
        ellipse(mouseX, mouseY, 15, 15);
//        System.out.println(nTime);
        if (millis() >= nTime && !paused) {
            Pair<String, Float> nextSet = timedTextParser.nextInstance();
            nWord = nextSet.a;
            nTime += nextSet.b;
//            System.out.println(nWord + " : " + nextSet.b);
        }
//        style(currentStyle);
        text(nWord, 0, 0, width, height);
        text(timedTextParser.getWpm(), width / 2, 100);
    }

    PStyle currentStyle = new PStyle();

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

class TimedTextParser extends TextParser {

    private final int sampleSize = 10;
    private ListIterator<Float> timeIterator;
    float baseDelay = 255.0F;
    private float scale = 7.0F;
    private float totalTime = 0;
    List<Float> recentTimes = new ArrayList<>();
    //    private float scale = 3.33F; //speed in 100 WPM
    private static String[] keys = new String[]{",", "-", "—", ":", "?", ".", "\"", "”"};
    private static float[] times = new float[]{3.5F, 2.5F, 2, 3.5F, 4, 8, 4, 4};

    private static FloatDict charTimes = new FloatDict(keys, times);

    TimedTextParser(File textFile) {
        super(textFile);
        timeIterator = getTimes(getWords(currentSentence), baseDelay / scale).listIterator();
        for (int i = 0; i < sampleSize; i++) {
            recentTimes.add(scale * 100);
        }
        totalTime = scale * 100 * sampleSize;
    }

    Pair<String, Float> nextInstance() {
        String nextWord = nextWord();
        float nextTime = nextWord != null ? timeIterator.next() : 0F;
        totalTime += nextTime;
        recentTimes.add(nextTime);
        totalTime -= recentTimes.get(0);
        recentTimes.remove(0);
//        Optional<Float> totalTime = recentTimes.parallelStream().reduce((x, y) -> x + y);
        return Pair.makePair(nextWord, nextTime);
    }

//    private void adjustwpm(int nextTime) {
//        int sampleSize = 100;
//        int goalwpm = 1000;
//        int wpmmargin = 50;
//
//        System.out.println((100 * 60000));
//        if ((60000F)  + wpmmargin < goalwpm) {
//            scale++;
//        } else if (60000 > goalwpm + wpmmargin) {
//            scale--;
//        }
//        System.out.println(scale);
//    }

    @Override
    public void refreshSentence() {
        wordIterator = getWords(currentSentence).listIterator();
        float var = baseDelay / scale;
//        System.out.println("Divisor: " + baseDelay + ", Dividend: " + scale + ", result: " + var);
        timeIterator = getTimes(getWords(currentSentence), var).listIterator();
    }

    void incSpeed() {
        scale++;
    }

    void decSpeed() {
        scale--;
    }

    public float getWpm() {
        return sampleSize / (totalTime / 1000);
    }

    private static List<Float> getTimes(List<String> words, float baseValue) {
        List<Float> times = new TimeList(baseValue);
        for (String s : words) {
            ((TimeList) times).multByBase(calcDelay(s));
        }
        return times;
    }

    private static float calcDelay(String word) {
        float delay = 1 + word.length() * 0.5F;
        Character c = word.charAt(word.length() - 1);
        if (c >= 48 && c <= 57) {
//                times.add(baseDelay + 2 / scale);
            delay += 2;
        } else {
            delay += charTimes.get(c.toString(), 0);
        }
        if (c == '"' || c == '”') {
            c = word.charAt(word.length() - 2);
            delay += charTimes.get(c.toString(), 0);
        }
//        return 1;
        return delay;
    }

    static class TimeList extends ArrayList<Float> {
        private float baseValue;

        TimeList(float baseValue) {
            super();
            this.baseValue = baseValue;
        }

        public void setBaseValue(float baseValue) {
            this.baseValue = baseValue;
        }

        public void multByBase(float mult) {
            this.add(this.baseValue * mult);
        }
    }
}

