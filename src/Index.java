import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liukaichi on 5/2/2016.
 */
public class Index
{
    Map<String, Map<Integer, Integer>> index;
    Map<Integer, Integer> maxFrequencies;

    public Index()
    {
        index = new HashMap<>();
        maxFrequencies = new HashMap<>();
    }

    void addWord(String word, int document)
    {
        //word is already in there.
        if (index.containsKey(word))
        {
            //document has at least one occurrence already
            if (index.get(word).containsKey(document))
            {
                index.get(word).put(document, index.get(word).get(document) + 1);
            }
            else
            {
                index.get(word).put(document, 1);

            }
        }
        //new word. make a new one.
        else {
            Map<Integer, Integer> newFrequencyMap = new HashMap<>();
            newFrequencyMap.put(document, 1);
            index.put(word, newFrequencyMap);
        }

        if (maxFrequencies.containsKey(document))
        {
            int oldMaxFreq = maxFrequencies.get(document);
            int newFrequency = index.get(word).get(document);
            if (oldMaxFreq < newFrequency)
            {
                maxFrequencies.put(document, newFrequency);
            }
        }
        else maxFrequencies.put(document, index.get(word).get(document));
    }

    public Map<Integer, Integer> getWord(String word)
    {
        return index.get(word);
    }

    public int getMaxFreq(Integer document)
    {
        return maxFrequencies.get(document);
    }

    public int getDocumentCount()
    {
        return maxFrequencies.size();
    }

    public int getNumDocsWithWord(String word)
    {
        return index.get(word).size();
    }

    public boolean contains(String word)
    {
        return index.containsKey(word);
    }

    public void printWordFrequencies()
    {
        try
        {
            StringWriter output = new StringWriter();
            for (String entry : index.keySet())
            {
                output.append(entry + '\t' + getFrequencies(index.get(entry)) + '\n');
            }

                Files.write(Paths.get("FrequencyResults.txt"), output.toString().getBytes(), StandardOpenOption.CREATE);

        }
        catch(Exception e)
        {

        }
    }

    private int getFrequencies(Map<Integer, Integer> integerIntegerMap)
    {
        int result = 0;
        for (Integer doc : integerIntegerMap.keySet())
        {
            result += integerIntegerMap.get(doc);
        }
        return result;
    }
}
