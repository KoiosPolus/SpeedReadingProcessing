package sketches;

import processing.data.FloatDict;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TimedTextParser extends TextParser {

    private ListIterator<Float> timeIterator;
    private float baseDelay = 175F;
    private float scale = 7F;
    private float totalTime;
    private final int sampleSize = 25;
    private List<Float> recentTimes = new ArrayList<>();
    private static String[] keys = new String[]{",", "-", "—", ":", "?", ".", "\"", "”"};
    private static float[] times = new float[]{2.75F, 1.75F, 1, 1.75F, 2, 4, 2, 2};
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
        timeIterator = getTimes(getWords(currentSentence), var).listIterator();
    }

    void incSpeed() {
        scale++;
    }

    void decSpeed() {
        scale--;
    }

    public float getWpm() {
        return sampleSize / (totalTime / 60000);
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