package mobi.fhdo.geoschnitzeljagd.Model;


import java.sql.Timestamp;
import java.util.UUID;

public class PaperchaseCompleted
{
    private User user;
    private Paperchase paperchase;
    private Timestamp startTime;
    private Timestamp endTime;

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Paperchase getPaperchase()
    {
        return paperchase;
    }

    public void setPaperchase(Paperchase paperchase)
    {
        this.paperchase = paperchase;
    }

    public Timestamp getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Timestamp startTime)
    {
        this.startTime = startTime;
    }

    public Timestamp getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Timestamp endTime)
    {
        this.endTime = endTime;
    }
}
