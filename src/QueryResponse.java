/**
 * Created by liukaichi on 5/2/2016.
 */
public class QueryResponse
{
    int id;
    String snippet;
    double rankScore;
    public QueryResponse(int id, String snippet, double rankScore)
    {
        this.id = id;
        this.snippet = snippet;
        this.rankScore = rankScore;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getSnippet()
    {
        return snippet;
    }

    public void setSnippet(String snippet)
    {
        this.snippet = snippet;
    }

    public double getRankScore()
    {
        return rankScore;
    }

    public void setRankScore(double rankScore)
    {
        this.rankScore = rankScore;
    }
}
