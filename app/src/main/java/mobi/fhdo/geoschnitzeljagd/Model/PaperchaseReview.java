package mobi.fhdo.geoschnitzeljagd.Model;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import mobi.fhdo.geoschnitzeljagd.DataManagers.Paperchases;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;

public class PaperchaseReview
{
    private UUID id;
    private User user;
    private Paperchase paperchase;

    private int difficulty;
    private int exciting;
    private int environment;
    private int length;
    private String comment;

    public PaperchaseReview(UUID id, User user, Paperchase paperchase, int difficulty, int exciting, int environment, int length, String comment)
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

    public PaperchaseReview(User user, Paperchase paperchase, int difficulty, int exciting, int environment, int length, String comment)
    {
        this.setId(UUID.randomUUID());
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

    public void objectToOutputStream(OutputStream os)
    {
        OutputStreamWriter osw = new OutputStreamWriter(os);
        JsonWriter writer = new JsonWriter(osw);

        try
        {
            /*conn.setDoOutput(true);
            mobi.fhdo.geoschnitzeljagd.Model.PaperchaseReview p = new mobi.fhdo.geoschnitzeljagd.Model.PaperchaseReview(UUID.randomUUID(),new User("Test","Test"),new Paperchase("Paper"),1,2,3,4,"Test Rewiev");
            p.objectToOutputStream(conn.getOutputStream());*/

            writer.beginObject();
            writer.name("PRID").value(this.getId().toString());
            writer.name("PID").value(this.paperchase.getId().toString());
            writer.name("UID").value(this.user.getId().toString());

            writer.name("Difficulty").value(this.getDifficulty());
            writer.name("Exciting").value(this.getExciting());
            writer.name("Environment").value(this.getEnvironment());
            writer.name("Length").value(this.getLength());
            writer.name("Comment").value(this.getComment());
            writer.endObject();

            writer.flush();
            osw.flush();
            writer.close();
            osw.close();
        }
        catch (Exception e)
        {
            Log.d("PaperchaseReview", "PaperchaseReview konnte nicht in Json überführt werden.");
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static PaperchaseReview jsonToObject(String result, Context context)
    {
        UUID id = null;
        User user = null;
        Paperchase paperchase = null;

        int difficulty = -1;
        int exciting = -1;
        int environment = -1;
        int length = -1;
        String comment = null;

        Users users = new Users(context);
        Paperchases paperchases = new Paperchases(context);

        try
        {
            InputStream input = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
            JsonReader reader = new JsonReader(new InputStreamReader(input, "UTF-8"));

            if (reader.peek() == JsonToken.BEGIN_OBJECT)
                reader.beginObject();

            while (reader.hasNext())
            {
                String name = reader.nextName();
                if (name.equals("PRID") && reader.hasNext())
                {
                    id = UUID.fromString(reader.nextString());
                }
                else if (name.equals("PID") && reader.hasNext())
                {
                    paperchase = paperchases.Get(UUID.fromString(reader.nextString()));
                }
                else if (name.equals("UID") && reader.hasNext())
                {
                    user = users.Get(UUID.fromString(reader.nextString()));
                }
                else if (name.equals("Difficulty") && reader.hasNext())
                {
                    difficulty = reader.nextInt();
                }
                else if (name.equals("Exciting") && reader.hasNext())
                {
                    exciting = reader.nextInt();
                }
                else if (name.equals("Environment") && reader.hasNext())
                {
                    environment = reader.nextInt();
                }
                else if (name.equals("Length") && reader.hasNext())
                {
                    length = reader.nextInt();
                }
                else if (name.equals("Comment") && reader.hasNext())
                {
                    comment = reader.nextString();
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

        if (id != null && user != null && paperchase != null && difficulty != -1 && exciting != -1 && environment != -1 && length != -1 && comment != null)
            return new PaperchaseReview(id, user, paperchase, difficulty, exciting, environment, length, comment);
        else
            return null;
    }

    // PaperchaseReview Parsen Beispiel
/*
        users.getWritableDatabase();
        User u = new User("ddddddddddddddd", "t");

        try
        {
            users.CreateOrUpdate(u);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Paperchase b = new Paperchase(u, "test");
        Paperchases pp = new Paperchases(this);
        pp.getWritableDatabase();
        pp.create(b);

        String result = "{\"PRID\":\"e6246ec6-e6f9-4b8e-886c-336fa514542f\",\"PID\":\"" + b.getId() + "\",\"UID\":\"" + u.getId() + "\",\"Difficulty\":1,\"Exciting\":2,\"Environment\":3,\"Length\":4,\"Comment\":\"Test Rewiev\"}";
        mobi.fhdo.geoschnitzeljagd.Model.PaperchaseReview p = mobi.fhdo.geoschnitzeljagd.Model.PaperchaseReview.jsonToObject(result, this);
        Log.d("Paperchase:", p.toString());
*/
}
