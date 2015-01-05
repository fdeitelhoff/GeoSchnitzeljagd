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
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import mobi.fhdo.geoschnitzeljagd.DataManagers.Paperchases;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;

public class PaperchaseCompleted
{
    private User user;
    private Paperchase paperchase;
    private Timestamp startTime;
    private Timestamp endTime;

    public PaperchaseCompleted(User user, Paperchase paperchase, Timestamp startTime, Timestamp endTime)
    {
        this.user = user;
        this.paperchase = paperchase;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public PaperchaseCompleted()
    {
        this.setPaperchase(new Paperchase("Name"));
        this.setUser(new User("Test", "Test"));

        java.util.Date date = new java.util.Date();
        setStartTime(new Timestamp(date.getTime()));
        setEndTime(new Timestamp(date.getTime()));
    }

    public PaperchaseCompleted(User user, Paperchase paperchase, Timestamp startTime) {
        this.user = user;
        this.paperchase = paperchase;
        this.startTime = startTime;
    }

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

    public void objectToOutputStream(OutputStream os)
    {
        OutputStreamWriter osw = new OutputStreamWriter(os);
        JsonWriter writer = new JsonWriter(osw);

        try
        {
            writer.beginObject();
            writer.name("PID").value(this.paperchase.getId().toString());
            writer.name("UID").value(this.user.getId().toString());

            writer.name("StartTime").value(this.getStartTime().toString());
            writer.name("EndTime").value(this.getEndTime().toString());
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
    public static PaperchaseCompleted jsonToObject(String result, Context context)
    {
        User user = null;
        Paperchase paperchase = null;
        Timestamp startTime = null;
        Timestamp endTime = null;

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
                if (name.equals("PID") && reader.hasNext())
                {
                    paperchase = paperchases.Get(UUID.fromString(reader.nextString()));
                }
                else if (name.equals("UID") && reader.hasNext())
                {
                    user = users.Get(UUID.fromString(reader.nextString()));
                }
                else if (name.equals("StartTime") && reader.hasNext())
                {
                    try
                    {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date parsedTimeStamp = dateFormat.parse(reader.nextString());
                        startTime = new Timestamp(parsedTimeStamp.getTime());
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (name.equals("EndTime") && reader.hasNext())
                {
                    try
                    {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date parsedTimeStamp = dateFormat.parse(reader.nextString());
                        endTime = new Timestamp(parsedTimeStamp.getTime());
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
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

        if (user != null && paperchase != null && startTime != null && endTime != null)
            return new PaperchaseCompleted(user, paperchase, startTime, endTime);
        else
            return null;
    }

    // PaperchaseCompleted Parsen Beispiel
/*
        users.getWritableDatabase();
        User u = new User("eeeeeeeeeeeeeee", "t");

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


        String result = "{\"PID\":\"" + b.getId() + "\",\"UID\":\"" + b.getUser().getId() + "\",\"StartTime\":\"2015-01-05 04:56:35.027\",\"EndTime\":\"2015-01-05 04:56:35.027\"}";
        mobi.fhdo.geoschnitzeljagd.Model.PaperchaseCompleted p = mobi.fhdo.geoschnitzeljagd.Model.PaperchaseCompleted.jsonToObject(result, this);
        Log.d("Paperchase:", p.toString());
        */
}
