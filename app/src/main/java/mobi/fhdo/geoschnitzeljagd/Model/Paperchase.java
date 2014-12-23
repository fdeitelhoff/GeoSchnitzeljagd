package mobi.fhdo.geoschnitzeljagd.Model;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Paperchase implements Serializable
{

    private UUID id;
    private User user;
    private String name;
    private Timestamp timestamp;
    private List<Mark> marks;

    public Paperchase(UUID id, User user, String name, Timestamp timestamp)
    {
        this(user, name, timestamp);
        this.id = id;
    }

    public Paperchase(User user, String name, Timestamp timestamp)
    {
        this.user = user;
        this.name = name;
        this.timestamp = timestamp;

        marks = new ArrayList<Mark>();
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }


    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    public void addMark(Mark mark)
    {
        marks.add(mark);
    }

    public void removeMark(Mark mark)
    {
        marks.remove(mark);
    }

    public List<Mark> getMarks()
    {
        return marks;
    }


    public void objectToOutputStream(OutputStream os)
    {
        OutputStreamWriter osw = new OutputStreamWriter(os);
        JsonWriter writer = new JsonWriter(osw);

        try
        {
            writer.beginObject();

            writer.name("PID").value(this.getId().toString());
            writer.name("UID").value(this.getUser().getId().toString());
            writer.name("Name").value(this.getName());
            writer.name("Timestamp").value(this.getTimestamp().toString());
            writer.name("Marks").beginArray();
            for (Mark m: this.getMarks())
            {
                writer.beginObject();
                writer.name("MID").value(m.getId().toString());
                writer.name("PID").value(this.getId().toString());
                writer.name("Latitude").value(m.getLatitude());
                writer.name("Longitude").value(m.getLongitude());
                writer.name("Hint").value(m.getHint());
                writer.name("Sequence").value(m.getSequence());
                writer.endObject();
            }
            writer.endArray();


            writer.endObject();

            writer.flush();
            osw.flush();
            writer.close();
            osw.close();
        }
        catch (Exception e)
        {
            Log.d("Paperchase", "Paperchase konnte nicht in Json überführt werden.");
        }
    }

    public static Paperchase jsonToObject(JsonReader reader) throws IOException
    {
        return new Paperchase(null, "", new Timestamp(65));
    }
}
