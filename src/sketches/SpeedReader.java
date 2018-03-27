package sketches;

import processing.core.PApplet;

import java.text.BreakIterator;
import java.util.*;

public class SpeedReader extends PApplet {

    @Override
    public void settings() {
        size(400, 400, P2D);
    }

    private Iterator<String> wordIterator;
    private Iterator<String> sentenceIterator;
    private Iterator<Float> timeIterator;
    private int baseDelay = 600; //100 WPM
    private float scale = 5.0F;
    private float sentenceDelay = baseDelay * 4 / scale;
    private float nTime = 0;
    private String word = "";
    private String nWord = "";

    @Override
    public void setup() {
        sentenceIterator = getSentences(textToBeRead).iterator();
        String firstSentence = sentenceIterator.next();
        wordIterator = getWords(firstSentence).iterator();
        timeIterator = getTimes(getWords(firstSentence), baseDelay / scale).iterator();
        textAlign(CENTER, CENTER);
        textSize(height / 8);
//        frameRate(2);
        background(100);
//        nextWord();
        nTime += millis();
    }


    @Override
    public void draw() {
//        noLoop();
        background(100);
        ellipse(mouseX, mouseY, 15, 15);
        if (millis() >= nTime) {
            word = nWord;
            nWord = nextWord();
        }
        text(word, 0, 0, width, height);
    }
    /*
    Get timer for the word, display the word for the given time, increment both.
     */

    private String nextWord() {
        String nextWord = "";
        if (wordIterator.hasNext()) {
            nextWord = wordIterator.next();
            nTime += timeIterator.next();
        } else if (sentenceIterator.hasNext()) {
            String nextSentence = sentenceIterator.next();
            wordIterator = getWords(nextSentence).iterator();
            timeIterator = getTimes(getWords(nextSentence), baseDelay / scale).iterator();
            nextWord = wordIterator.next();
//            timeIterator.next();
            nTime += sentenceDelay;
        }
        return nextWord;
    }

    private static List<String> getWords(String text) {
//        BreakIterator breakIterator = BreakIterator.getWordInstance();
//        return parseTextToList(text, breakIterator);
        String[] array = text.split(" ");
        return new ArrayList<>(Arrays.asList(array));
    }

    private static List<Float> getTimes(List<String> words, float baseValue) {
//        List<Float> times = new ArrayList<>();
        List<Float> times = new TimeList(baseValue);
        TimeList timeList = (TimeList) times;
        for (String s : words) {
            Character c = s.charAt(s.length() - 1);
            if (c >= 48 && c <= 57) {
//                times.add(baseDelay * 2 / scale);
                timeList.multByBase(2);
            } else {
                switch (c) {
                    case ',':
                        timeList.multByBase(2);
                        break;
                    case '-':
                        timeList.multByBase(1.5F);
                        break;
                    case '—':
                        timeList.multByBase(2);
                        break;
                    case ':':
                        timeList.multByBase(2);
                        break;
                    default:
                        timeList.multByBase(1);
                        break;
                }
            }
        }
        return times;
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

    /*  Put a space after each dash to allow separate word recognition\
        Got rid of citations which take substantial time to read with no benefit.
     */
    private String textToBeRead = "Current approaches to object recognition make essential use of machine learning methods. To improve " +
            "their performance, we can collect larger datasets, learn more powerful models, and use better " +
            "techniques for preventing overfitting. Until recently, datasets of labeled images were relatively " +
            "small — on the order of tens of thousands of images. Simple recognition tasks can be solved quite well with datasets of this size, " +
            "especially if they are augmented with label- preserving transformations. For example, the currentbest " +
            "error rate on the MNIST digit- recognition task (<0.3%) approaches human performance. " +
            "But objects in realistic settings exhibit considerable variability, so to learn to recognize them it is " +
            "necessary to use much larger training sets. And indeed, the shortcomings of small image datasets " +
            "have been widely recognized, but it has only recently become possible to collect " +
            "labeled datasets with millions of images. The new larger datasets include LabelMe, which " +
            "consists of hundreds of thousands of fully- segmented images, and ImageNet, which consists of " +
            "over 15 million labeled high- resolution images in over 22,000 categories. " +
            "To learn about thousands of objects from millions of images, we need a model with a large learning " +
            "capacity. However, the immense complexity of the object recognition task means that this problem " +
            "cannot be specified even by a dataset as large as ImageNet, so our model should also have lots " +
            "of prior knowledge to compensate for all the data we don’t have. Convolutional neural networks " +
            "(CNNs) constitute one such class of models. Their capacity can be controlled " +
            "by varying their depth and breadth, and they also make strong and mostly correct assumptions " +
            "about the nature of images (namely, stationarity of statistics and locality of pixel dependencies). " +
            "Thus, compared to standard feedforward neural networks with similarly- sized layers, CNNs have " +
            "much fewer connections and parameters and so they are easier to train, while their theoretically- best " +
            "performance is likely to be only slightly worse. " +
            "Despite the attractive qualities of CNNs, and despite the relative efficiency of their local architecture, " +
            "they have still been prohibitively expensive to apply in large scale to high- resolution images. Luckily, " +
            "current GPUs, paired with a highly- optimized implementation of 2D convolution, are powerful " +
            "enough to facilitate the training of interestingly- large CNNs, and recent datasets such as ImageNet " +
            "contain enough labeled examples to train such models without severe overfitting. " +
            "The specific contributions of this paper are as follows: we trained one of the largest convolutional " +
            "neural networks to date on the subsets of ImageNet used in the ILSVRC- 2010 and ILSVRC- 2012 " +
            "competitions and achieved by far the best results ever reported on these datasets. We wrote a " +
            "highly- optimized GPU implementation of 2D convolution and all the other operations inherent in " +
            "training convolutional neural networks, which we make available publicly1 " +
            ". Our network contains " +
            "a number of new and unusual features which improve its performance and reduce its training time, " +
            "which are detailed in Section 3. The size of our network made overfitting a significant problem, even " +
            "with 1.2 million labeled training examples, so we used several effective techniques for preventing " +
            "overfitting, which are described in Section 4. Our final network contains five convolutional and " +
            "three fully- connected layers, and this depth seems to be important: we found that removing any " +
            "convolutional layer (each of which contains no more than 1% of the model’s parameters) resulted in " +
            "inferior performance. " +
            "In the end, the network’s size is limited mainly by the amount of memory available on current GPUs " +
            "and by the amount of training time that we are willing to tolerate. Our network takes between five " +
            "and six days to train on two GTX 580 3GB GPUs. All of our experiments suggest that our results " +
            "can be improved simply by waiting for faster GPUs and bigger datasets to become available.";

    static void test() {
        List test = new TimeList(1);
        ((TimeList) test).multByBase(2);
    }
}

class TimeList extends ArrayList<Float> {
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