import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by liukaichi on 4/30/2016.
 */
public class Tokenizer
{
    StopWords stopWords;
    //HashSet<String> tokens;
    Index tokens;
    Map<Integer, Double> scores;
    Dictionary dictionary;
    PorterStemmer stemmer = new PorterStemmer();

    public Tokenizer(String directory)
    {
        scores = new TreeMap<>();
        stopWords = new StopWords();
        //tokens = new HashSet<>();
        tokens = new Index();
        dictionary = new Dictionary();

        parseDictionary(directory);
    }

    public void addToken(String newToken, int document)
    {
        // TODO: 4/30/2016 does ' count as a punctuation symbol?
        try
        {
            newToken = newToken.replaceAll("[^a-zA-Z]", "").toLowerCase().trim();
        } catch (NullPointerException e)
        {
            System.out.println(e.getStackTrace());
        }
        // TODO: 4/30/2016 Do numbers count as tokens?

        /*if (stopWords.contains(newToken))
        {
            tokens.addWord(newToken, document);
        }
        else if (newToken.matches("[a-zA-Z]+"))
        {
            tokens.addWord(stemmer.stem(newToken), document);
        }*/

        //just add everything for HW 3
        tokens.addWord(newToken, document);
    }

    public String[] dehyphenate(String word)
    {
        String result[] = new String[2];
        String concatenatedString = word.replaceAll("-", "");
        if (dictionary.contains(concatenatedString))
        {
            result[0] = concatenatedString;
            result[1] = "";
        } else
        {
            result[0] = word.substring(0, word.indexOf('-'));
            result[1] = word.substring(word.indexOf('-') + 1);
        }
        return result;
    }

    public List<QueryResponse> query(String query)
    {
        scores = new TreeMap<>();
        ArrayList<QueryResponse> result = new ArrayList<>();
        String wordList[] = query.split(" ");
        ArrayList<String> dehyphenatedTerms = new ArrayList<>();
        for (String word : wordList)
        {
            if (word.contains("-"))
            {
                for (String dehyphenatedWord : dehyphenate(word))
                {
                    dehyphenatedTerms.add(dehyphenatedWord.toLowerCase());
                }
            } else
                dehyphenatedTerms.add(word.toLowerCase());

        }

        for (String word : dehyphenatedTerms)
        {
            if (stopWords.contains(word)) updateScores(word);
            else
            {
                String stem = stemmer.stem(word);
                updateScores(stem);
            }
        }

        //get top 10
        ArrayList<Map.Entry<Integer, Double>> sortingList = new ArrayList<>(scores.entrySet());
        Collections.sort(sortingList, new Comparator<Map.Entry<Integer, Double>>()
        {
            @Override public int compare(Map.Entry<Integer, Double> e1, Map.Entry<Integer, Double> e2)
            {
                int res = e2.getValue().compareTo(e1.getValue());
                return res != 0 ? res : 1;
            }
        });
        List<Map.Entry<Integer, Double>> list = sortingList.subList(0, 10);
        for (Map.Entry<Integer, Double> entry : list)
        {
            result.add(new QueryResponse(entry.getKey(), getDocument(entry.getKey()), entry.getValue()));
        }
        return result;
    }

    private String getDocument(Integer key)
    {
        try
        {
            FileInputStream stream = new FileInputStream(new File("To_be_posted\\Doc (" + key + ").txt"));

            int content;
            StringBuilder snippet = new StringBuilder();
            while ((content = stream.read()) != -1)
            {
                snippet.append((char) content);
                if ((char) content == '.')
                    break;
            }
            return snippet.toString();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map)
    {

        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(

        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    public void updateScores(String word)
    {
        //for each document that the word appears in
        if (!tokens.contains(word))
            return;
        for (Integer document : tokens.getWord(word).keySet())
        {
            //if there's already a score, add onto it.
            if (scores.containsKey(document))
            {
                scores.put(document, scores.get(document) + getScore(word, document));
            }
            //otherwise make a new entry.
            else
                scores.put(document, getScore(word, document));

        }
    }

    private double getScore(String word, Integer document)
    {
        double result = 0;
        //TF
        double wordFrequency = tokens.getWord(word).get(document);
        double maxFrequency = tokens.getMaxFreq(document);
        double TF = wordFrequency / maxFrequency;
        double numDocuments = tokens.getDocumentCount();
        double numDocsWithWord = tokens.getNumDocsWithWord(word);
        double IDF = Math.log(numDocuments / numDocsWithWord) / Math.log(2);
        //IDF
        result = TF * IDF;
        return result;
    }

    private void parseDictionary(String directory)
    {
        File folder = new File(directory);
        File[] files = folder.listFiles();
        if (folder.isDirectory() && files != null)
        {

            for (File file : files)
            {
                int documentNumber = -1;
                try
                {
                    // Read the file in
                    Pattern p = Pattern.compile("(\\d+)");
                    Matcher m = p.matcher(file.getName());
                    while (m.find())
                    {

                        documentNumber = Integer.parseInt(m.group(1));
                    }
                    BufferedReader in = new BufferedReader(new FileReader(file)); //THIS IS THE WIKI FILE
                    StringBuffer str = new StringBuffer();
                    String nextLine = "";
                    while ((nextLine = in.readLine()) != null)
                        str.append(nextLine + "\n");
                    in.close();
                    //save it to a bin tree.
                    StringTokenizer st = new StringTokenizer(str.toString());//create a string
                    while (st.hasMoreTokens())
                    {
                        nextLine = st.nextToken();
                        //remove capitalization, punctuation symbols, and hyphens.
                        if (nextLine.contains("-")) //we assume there are no words with more than one '-'.
                        {
                            String wordToAdd[] = dehyphenate(nextLine);
                            for (String word : wordToAdd)
                            {
                                addToken(word, documentNumber);
                            }
/*
                            String concatenatedString = nextLine.replaceAll("-", "");
                            if (dictionary.contains(concatenatedString) )
                            {
                                addToken(concatenatedString, documentNumber);
                            }
                            else
                            {
                                //not concatenated
                                addToken(nextLine.substring(0,nextLine.indexOf('-')), documentNumber);
                                addToken(nextLine.substring(nextLine.indexOf('-') + 1, nextLine.length()), documentNumber);
                            }*/
                        } else
                        {
                            addToken(nextLine, documentNumber);
                        }
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }

    }

}
