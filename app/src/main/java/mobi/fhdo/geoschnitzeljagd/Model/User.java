package mobi.fhdo.geoschnitzeljagd.Model;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

public class User implements Serializable
{

    private int id;
    private String username;
    private String password;

    public User(String username, String password)
    {
        setUsername(username);
        setPassword(password);
    }

    public User(int id, String username, String password)
    {
        this(username, password);

        setId(id);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
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

            writer.name("id").value(this.getId());
            writer.name("username").value(this.getUsername());
            writer.name("password").value(this.getPassword());

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
        int id = -1;
        String username = null;
        String password = null;

        InputStreamReader isr = new InputStreamReader(is);
        JsonReader reader = new JsonReader(isr);

        reader.beginObject();
        while (reader.hasNext())
        {
            String name = reader.nextName();
            if (name.equals("id"))
            {
                id = reader.nextInt();
            } else if (name.equals("username"))
            {
                username = reader.nextString();
            } else if (name.equals("password"))
            {
                password = reader.nextString();
            } else
            {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new User(id, username, password);
    }

}
