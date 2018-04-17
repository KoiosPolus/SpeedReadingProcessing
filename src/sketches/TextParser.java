package sketches;

import java.io.*;
import java.text.BreakIterator;
import java.util.*;
import java.util.regex.Pattern;

public class TextParser {
    ListIterator<String> wordIterator;
    ListIterator<String> sentenceIterator;
    String currentSentence;
    private BufferedReader bufferedReader;
    boolean endReached = false;

    public TextParser(File textFile) {
        //TODO Add in a file selector
        //TODO Add compatibility for more file types
        try {
            bufferedReader = new BufferedReader(new FileReader(textFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid File directory: " + textFile.getAbsolutePath());
        }
        sentenceIterator = nextSection().listIterator();
        nextSentence(); //TODO although nextSentence is overwritten in the derived class, it's not being properly called by the super constructor
//        nextSection();
//        String firstSentence = sentenceIterator.next();
//        wordIterator = getWords(firstSentence).iterator();
//        timeIterator = getTimes(getWords(firstSentence), baseDelay / scale).iterator();
//        wordIterator.next();
//        timeIterator.next();
    }

    public String nextWord() {
        String nextWord;
        if (wordIterator.hasNext()) {
            nextWord = wordIterator.next();
        } else {
            if (sentenceIterator.hasNext()) {
                nextSentence();
            } else if (!endReached) {
                sentenceIterator = nextSection().listIterator();
            } else {
                return null;
            }
            return nextWord();
        }
        return nextWord;
    }

    List<String> nextSection() {
        List<String> nextSection = new ArrayList<>();
        try {
            if (bufferedReader.ready()) {
                nextSection = getSentences(softenText(bufferedReader.readLine()));
                System.out.println(nextSection);
            } else {
//                System.out.println(getSentences(softenText(bufferedReader.readLine())));
                bufferedReader.close();
                endReached = true;
                System.out.println("End of document reached");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nextSection;
    }

    public void nextSentence() {
        if (sentenceIterator.hasNext())
            currentSentence = sentenceIterator.next();
        refreshSentence();
    }

    public void previousSentence() {
        if (sentenceIterator.hasPrevious())
            currentSentence = sentenceIterator.previous();
        refreshSentence();
    }

    public void refreshSentence() {
        wordIterator = getWords(currentSentence).listIterator();
    }

    public String getCurrentSentence() {
        return currentSentence;
    }

    public boolean hasNext() {
        return sentenceIterator.hasNext();
    }

    public boolean hasPrevious() {
        return sentenceIterator.hasPrevious();
    }

    public static List<String> getWords(String text) {
//        BreakIterator breakIterator = BreakIterator.getWordInstance();
//        return parseTextToList(text, breakIterator);
        String[] array = text.split(" ");
        return new ArrayList<>(Arrays.asList(array));
    }

    public static List<String> getSentences(String text) {
        BreakIterator breakIterator = BreakIterator.getSentenceInstance(Locale.US);
        return parseTextToList(text, breakIterator);
    }

    public static List<String> getSimpleWords(String text) {
        BreakIterator breakIterator = BreakIterator.getWordInstance(Locale.US);
        return parseTextToList(text, breakIterator);
    }

    public static List<String> parseTextToList(String text, BreakIterator breakIterator) {
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

    static String softenText(String content) {
//        System.out.println(content);
        Pattern p = Pattern.compile("[-—]"); // Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        return p.matcher(content).replaceAll("- ");
    }
}


