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
    private float totalTime = 0;
    private int wordCount = 0;

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
        if (millis() >= nTime) {
            Pair<String, Float> nextSet = timedTextParser.nextInstance();
            totalTime += nextSet.b;
            wordCount++;
            nWord = nextSet.a;
            nTime += nextSet.b;
//            System.out.println(nWord + " : " + nextSet.b);
        }
//        style(currentStyle);
        text(nWord, 0, 0, width, height);
        text((wordCount * 100000.0F)  / totalTime, width / 2, 100);
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
        if (keyCode == UP) {
            timedTextParser.incSpeed();
        } else if (keyCode == DOWN) {
            timedTextParser.decSpeed();
        }
    }
}

class TimedTextParser extends TextParser {

    private Iterator<Float> timeIterator;
    float baseDelay = 255.0F;
    private float scale = 7.0F;
    //    private float scale = 3.33F; //speed in 100 WPM
    private static String[] keys = new  String[] {"," , "-" , "—", ":" , "?", ".", "\"", "”"};
    private static float[] times = new  float[] {3.5F, 2.5F, 2, 3.5F, 4, 8, 4, 4} ;

    private static FloatDict charTimes = new FloatDict(keys, times);

    TimedTextParser(File textFile) {
        super(textFile);
        timeIterator = getTimes(getWords(currentSentence), baseDelay / scale).iterator();
    }

    Pair<String, Float> nextInstance() {
        String nextWord = nextWord();
        float nextTime = nextWord != null ? timeIterator.next() : 0F;
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
    public void nextSentence() {
        currentSentence = sentenceIterator.next();
        wordIterator = getWords(currentSentence).iterator();
        float var = baseDelay / scale;
//        System.out.println("Divisor: " + baseDelay + ", Dividend: " + scale + ", result: " + var);
        timeIterator = getTimes(getWords(currentSentence), var).iterator();
    }

    void incSpeed() {
        scale++;
    }

    void decSpeed() {
        scale--;
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

