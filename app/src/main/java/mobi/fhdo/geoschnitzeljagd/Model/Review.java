package mobi.fhdo.geoschnitzeljagd.Model;

import java.util.UUID;

public class Review
{
    private UUID id;
    private User user;
    private Paperchase paperchase;

    private int difficulty;
    private int exciting;
    private int environment;
    private int length;
    private String comment;

    public Review(UUID id, User user, Paperchase paperchase, int difficulty, int exciting, int environment, int length, String comment)
    {
        this.id = id;
        this.user = user;
        this.paperchase = paperchase;
        this.difficulty = difficulty;
        this.exciting = exciting;
        this.environment = environment;
        this.length = length;
        this.comment = comment;
    }

    public UUID getId()
    {
        return id;
    }

    public User getUser()
    {
        return user;
    }

    public Paperchase getPaperchase()
    {
        return paperchase;
    }

    public int getDifficulty()
    {
        return difficulty;
    }

    public int getExciting()
    {
        return exciting;
    }

    public int getEnvironment()
    {
        return environment;
    }

    public int getLength()
    {
        return length;
    }

    public String getComment()
    {
        return comment;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public void setPaperchase(Paperchase paperchase)
    {
        this.paperchase = paperchase;
    }

    public void setDifficulty(int difficulty)
    {
        this.difficulty = difficulty;
    }

    public void setExciting(int exciting)
    {
        this.exciting = exciting;
    }

    public void setEnvironment(int environment)
    {
        this.environment = environment;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }
}
