package mobi.fhdo.geoschnitzeljagd.Model;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class User implements Serializable
{

    private UUID id;
    private String username;
    private String password;

    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }

    private Timestamp timestamp;

    public User(String username, String password)
    {
        setId(UUID.randomUUID());
        setUsername(username);
        setPassword(password);

        java.util.Date date = new java.util.Date();
        setTimestamp(new Timestamp(date.getTime()));
    }

    public User(UUID id, String username, String password, Timestamp timestamp)
    {
        this(username, password);
        this.timestamp = timestamp;
        setId(id);
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void objectToOutputStream(OutputStream os)
    {
        OutputStreamWriter osw = new OutputStreamWriter(os);
        JsonWriter writer = new JsonWriter(osw);

        try
        {
            writer.beginObject();

            writer.name("UID").value(this.getId().toString());
            writer.name("Username").value(this.getUsername());
            writer.name("Password").value(this.getPassword());
            writer.name("Timestamp").value(this.getTimestamp().toString());


/*
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd hh:mm:ss:SSS");

            Date parsedTimeStamp = dateFormat.parse("2014-08-22 15:02:51:580");

            Timestamp timestamp = new Timestamp(parsedTimeStamp.getTime());

            assertEquals(1408737771580l, timestamp.getTime());
*/

            writer.endObject();


            writer.flush();
            osw.flush();
            writer.close();
            osw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public User jsonToObject(InputStream is) throws IOException
    {
        UUID id = null;
        String username = null;
        String password = null;
        Timestamp timestamp = null;

        InputStreamReader isr = new InputStreamReader(is);
        JsonReader reader = new JsonReader(isr);

        reader.beginObject();
        while (reader.hasNext())
        {
            String name = reader.nextName();
            if (name.equals("UID"))
            {
                id = UUID.fromString(reader.nextString());
            } else if (name.equals("Username"))
            {
                username = reader.nextString();
            } else if (name.equals("Password"))
            {
                password = reader.nextString();
            } else if (name.equals("Timestamp"))
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
            } else
            {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new User(id, username, password, timestamp);
    }

}
