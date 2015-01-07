package mobi.fhdo.geoschnitzeljagd.Model;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import mobi.fhdo.geoschnitzeljagd.Activities.newpaperchase;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;

public class Paperchase implements Serializable
{
    private UUID id;
    private User user;
    private String name;
    private Timestamp timestamp;
    private List<Mark> marks;

    public Paperchase(UUID id, User user, String name, Timestamp timestamp, List<Mark> marks)
    {
        this.id = id;
        this.user = user;
        this.name = name;
        this.timestamp = timestamp;
        this.marks = new LinkedList<Mark>();
        this.marks.addAll(marks);
    }

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

    public Paperchase(User user, String name)
    {
        this.user = user;
        this.name = name;
        setId(UUID.randomUUID());
        java.util.Date date = new java.util.Date();
        setTimestamp(new Timestamp(date.getTime()));

        this.marks = new LinkedList<Mark>();
        this.addMark(new Mark(1, 1));
        this.addMark(new Mark(2, 2));
        this.addMark(new Mark(3, 3));
        this.addMark(new Mark(4, 4));
    }

    // Debug Constructor
    public Paperchase(String name)
    {
        this.name = name;
        setId(UUID.randomUUID());

        java.util.Date date = new java.util.Date();
        setTimestamp(new Timestamp(date.getTime()));
        this.setUser(new User("Test", "Test"));

        this.marks = new LinkedList<Mark>();
        this.addMark(new Mark(1, 1));
        this.addMark(new Mark(2, 2));
        this.addMark(new Mark(3, 3));
        this.addMark(new Mark(4, 4));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Paperchase jsonToObject(JsonReader reader, Context context)
    {
        UUID id = null;
        User user = null;
        String paperchaseName = null;
        Timestamp timestamp = null;
        List<Mark> marks = null;

        Users users = new Users(context);

        try
        {
            if (reader.peek() == JsonToken.BEGIN_OBJECT)
                reader.beginObject();

            while (reader.hasNext())
            {
                String name = reader.nextName();
                if (name.equals("PID") && reader.hasNext())
                {
                    id = UUID.fromString(reader.nextString());
                }
                else if (name.equals("UID") && reader.hasNext())
                {
                    user = users.Get(UUID.fromString(reader.nextString()));
                }
                else if (name.equals("Name") && reader.hasNext())
                {
                    paperchaseName = reader.nextString();
                }
                else if (name.equals("Timestamp") && reader.hasNext())
                {
                    try
                    {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date parsedTimeStamp = dateFormat.parse(reader.nextString());
                        timestamp = new Timestamp(parsedTimeStamp.getTime());
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (name.equals("Marks") && reader.hasNext())
                {
                    marks = new ArrayList<Mark>();
                    reader.beginArray();
                    while (reader.hasNext())
                    {
                        marks.add(Mark.jsonToObject(reader));
                    }
                    reader.endArray();
                }
                else
                {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        if (id != null && user != null && paperchaseName != null && timestamp != null && marks != null)
            return new Paperchase(id, user, paperchaseName, timestamp, marks);
        else
            return null;
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

    public void setMarks(List<Mark> marks)
    {
        this.marks = marks;
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
            for (Mark m : this.getMarks())
            {
                m.objectToJsonWriter(writer, this.getId());
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

    // Paperchase Parsen Beispiel
        /*
        String result = "{\"PID\":\"483a3460-335a-47d1-aeb7-8ed94719d344\",\"UID\":\"40dd6a65-3309-432d-bb2d-80d4aaa3914c\",\"Name\":\"TestP\",\"Timestamp\":\"1969-12-31 19:00:00.555\",\"Marks\":[{\"MID\":\"43a61ede-3c69-4560-b8e5-91fc45b35e2d\",\"PID\":\"483a3460-335a-47d1-aeb7-8ed94719d344\",\"Latitude\":1.0,\"Longitude\":1.0,\"Hint\":null,\"Sequence\":0},{\"MID\":\"c5414482-b348-4985-862b-9e07001f81ee\",\"PID\":\"483a3460-335a-47d1-aeb7-8ed94719d344\",\"Latitude\":2.0,\"Longitude\":2.0,\"Hint\":null,\"Sequence\":0},{\"MID\":\"3aacf317-c9e6-4eb7-8716-f9890afa1325\",\"PID\":\"483a3460-335a-47d1-aeb7-8ed94719d344\",\"Latitude\":3.0,\"Longitude\":3.0,\"Hint\":null,\"Sequence\":0},{\"MID\":\"d913fab5-a336-40f8-bb3a-d6ff6783e226\",\"PID\":\"483a3460-335a-47d1-aeb7-8ed94719d344\",\"Latitude\":4.0,\"Longitude\":4.0,\"Hint\":null,\"Sequence\":0}]}";
        Paperchase p = Paperchase.jsonToObject(result, this);
        Log.d("Paperchase:", p.toString());
        */
}
