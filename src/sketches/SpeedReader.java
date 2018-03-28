package sketches;

import processing.core.PApplet;
import processing.core.PStyle;

import java.io.*;
import java.text.BreakIterator;
import java.util.*;
import java.util.regex.Pattern;

public class SpeedReader extends PApplet {

    @Override
    public void settings() {
        size(400, 400, P2D);
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
        text((float) (wordCount) / totalTime * 100000, width / 2, 100);
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

class TimedTextParser {

    private Iterator<String> wordIterator;
    private Iterator<String> sentenceIterator;
    private Iterator<Float> timeIterator;
    private int baseDelay = 260; //~100 WPM delay, adjusted for average word-specific delays
    private float scale = 10.0F; //speed in 100 WPM
    private BufferedReader bufferedReader;
    private ArrayList<Integer> timeCount = new ArrayList<>();

    TimedTextParser(File textFile) {
        //TODO Add in a file selector
        //TODO Add compatibility for more file types
        try {
            bufferedReader = new BufferedReader(new FileReader(textFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid File directory: " + textFile.getAbsolutePath());
        }
        sentenceIterator = nextSection().iterator();
        nextSentence();
//        nextSection();
//        String firstSentence = sentenceIterator.next();
//        wordIterator = getWords(firstSentence).iterator();
//        timeIterator = getTimes(getWords(firstSentence), baseDelay / scale).iterator();
//        wordIterator.next();
//        timeIterator.next();
    }

    public Pair<String, Float> nextInstance() {
        String nextWord;
        float nextTime;
        if (wordIterator.hasNext() && timeIterator.hasNext()) {
            nextWord = wordIterator.next();
            nextTime = timeIterator.next();
//            adjustwpm(timeCount);
        } else {
            if (sentenceIterator.hasNext()) {
                nextSentence();
            } else {
                nextSection();
            }
            return nextInstance();
        }
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

    public void incSpeed() {
        scale++;
    }
    public void decSpeed() {
        scale--;
    }

    private List<String> nextSection() {
        List<String> nextSection = new ArrayList<>();
        try {//TODO the buffer size of Buffered reader is so huge that I have no idea what happens at the end of a line.
            nextSection = getSentences(softenText(bufferedReader.readLine()));
//            nextSection.remove(nextSection.size() - 1);
            System.out.println(nextSection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nextSection;
    }

    private void nextSentence() {
        String nextSentence = sentenceIterator.next();
        wordIterator = getWords(nextSentence).iterator();
        timeIterator = getTimes(getWords(nextSentence), baseDelay / scale).iterator();
    }

    private static List<String> getWords(String text) {
//        BreakIterator breakIterator = BreakIterator.getWordInstance();
//        return parseTextToList(text, breakIterator);
        String[] array = text.split(" ");
        return new ArrayList<>(Arrays.asList(array));
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
            switch (c) {
                case ',':
                    delay += 3.5F;
                    break;
                case '-':
                    delay += 1.5F;
                    break;
                case '—':
                    delay += 2;
                    break;
                case ':':
                    delay += 3.5;
                    break;
                case '?':
                    delay += 4;
                    break;
                case '.':
                    delay += 6;
                    break;
                case '"':
                    delay += 4;
                    break;
                case '”':
                    delay += 4;
                    break;
                default:
                    delay += 1;
                    break;
            }
        }
        return delay;
    }

    private static List<String> getSentences(String text) {
        BreakIterator breakIterator = BreakIterator.getSentenceInstance(Locale.US);
        return parseTextToList(text, breakIterator);
    }

    private static List<String> getSimpleWords(String text) {
        BreakIterator breakIterator = BreakIterator.getWordInstance(Locale.US);
        return parseTextToList(text, breakIterator);
    }

    private static List<String> parseTextToList(String text, BreakIterator breakIterator) {
        List<String> words = new ArrayList<>();
        breakIterator.setText(text);
        int lastIndex = breakIterator.first();
        while (BreakIterator.DONE != lastIndex) {
            int firstIndex = lastIndex;
            lastIndex = breakIterator.next();
            if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
                words.add(text.substring(firstIndex, lastIndex));
            }
        }
        return words;
    }

    private static String softenText(String content) {
        Pattern p = Pattern.compile("[-—]"); // Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        return p.matcher(content).replaceAll("- ");
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
            this.add(baseValue * mult);
        }
    }
}
