import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Created by liukaichi on 4/30/2016.
 */
public class Dictionary
{
    HashSet<String> dictionary;
    public Dictionary()
    {
        dictionary = new HashSet<>();
        try
        {
            // Read the unorder file in
            BufferedReader in = new BufferedReader(new FileReader("src\\dictionary.txt")); //THIS IS THE FILE THAT CONTAINS THE STOPWORDS
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
                if (nextLine.matches("[a-zA-Z'.]*"))
                    dictionary.add(nextLine.trim());
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean contains(String string)
    {
        return dictionary.contains(string);
    }
}
