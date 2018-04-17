package sketches;

import com.sun.jdi.IntegerValue;

import java.io.File;
import java.util.HashMap;

public class CommonWordsHashTable {
    private TextParser textParser;

    HashMap<String, Integer> createHashTable(File file) {
        HashMap<String, Integer> frequencyTable = new HashMap<>();
        textParser = new TextParser(file);
        System.out.println(textParser.getCurrentSentence());
        while (textParser.hasNext()) {
            textParser.nextSentence();
            String currentLine = textParser.getCurrentSentence();
            String[] cvs = currentLine.split(",");
            System.out.println(cvs);
            frequencyTable.put(cvs[0], Integer.parseInt(cvs[1]));
        }
        return frequencyTable;
    }
}
