import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Scanner;

/**
 * Created by liukaichi on 4/30/2016.
 */
public class Main
{
    public static void main(String[] args)
    {
        String query;
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter a query: ");
        query = reader.nextLine(); // Scans the next token of the input as an int.

        Tokenizer tokenizer = new Tokenizer(".\\To_be_posted");
        List<QueryResponse> results = tokenizer.query(query);

        StringWriter output = new StringWriter();
        output.append(String.format("Searched terms: %s\n", query));
        try
        {
            int rank = 1;
            for (QueryResponse response : results)
            {
                output.append(String.format("%d.\tID: %s\t | Ranking Score: %f | \t%s\n",
                        rank,
                        response.getId(),
                        response.getRankScore(),
                        response.getSnippet()
                        ));
                rank++;
            }
            output.append("\n\n");
            if ((new File("QueryResults.txt")).exists())
            {
                Files.write(Paths.get("QueryResults.txt"), output.toString().getBytes(), StandardOpenOption.APPEND);
            }
            else
            {
                Files.write(Paths.get("QueryResults.txt"), output.toString().getBytes(), StandardOpenOption.CREATE);
            }

        tokenizer.tokens.printWordFrequencies();

        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }
}
