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

import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;

public class Paperchase implements Serializable {
    private UUID id;
    private User user;
    private String name;
    private Timestamp timestamp;
    private List<Mark> marks;

    public Paperchase(UUID id, User user, String name, Timestamp timestamp, List<Mark> marks) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.timestamp = timestamp;
        this.marks = new LinkedList<Mark>();
        this.marks.addAll(marks);
    }

    public Paperchase(UUID id, User user, String name, Timestamp timestamp) {
        this(user, name, timestamp);
        this.id = id;
    }

    public Paperchase(User user, String name, Timestamp timestamp) {
        this.user = user;
        this.name = name;
        this.timestamp = timestamp;

        marks = new ArrayList<Mark>();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Paperchase jsonToObject(String result, Context context) {
        UUID id = null;
        User user = null;
        String paperchaseName = null;
        Timestamp timestamp = null;
        List<Mark> marks = null;

        Users users = new Users(context);

        try {
            InputStream input = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
            JsonReader reader = new JsonReader(new InputStreamReader(input, "UTF-8"));

            if (reader.peek() == JsonToken.BEGIN_OBJECT)
                reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("PID") && reader.hasNext()) {
                    id = UUID.fromString(reader.nextString());
                } else if (name.equals("UID") && reader.hasNext()) {
                    user = users.Get(UUID.fromString(reader.nextString()));
                } else if (name.equals("Name") && reader.hasNext()) {
                    paperchaseName = reader.nextString();
                } else if (name.equals("Timestamp") && reader.hasNext()) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date parsedTimeStamp = dateFormat.parse(reader.nextString());
                        timestamp = new Timestamp(parsedTimeStamp.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (name.equals("Marks") && reader.hasNext()) {
                    marks = new ArrayList<Mark>();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        marks.add(Mark.jsonToObject(reader));
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (id != null && user != null && paperchaseName != null && timestamp != null && marks != null)
            return new Paperchase(id, user, paperchaseName, timestamp, marks);
        else
            return null;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return getName();
    }

    public void addMark(Mark mark) {
        marks.add(mark);
    }

    public void removeMark(Mark mark) {
        marks.remove(mark);
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public void setMarks(List<Mark> marks) {
        this.marks = marks;
    }

    public void objectToOutputStream(OutputStream os) {
        OutputStreamWriter osw = new OutputStreamWriter(os);
        JsonWriter writer = new JsonWriter(osw);

        try {
            writer.beginObject();

            writer.name("PID").value(this.getId().toString());
            writer.name("UID").value(this.getUser().getId().toString());
            writer.name("Name").value(this.getName());
            writer.name("Timestamp").value(this.getTimestamp().toString());
            writer.name("Marks").beginArray();
            for (Mark m : this.getMarks()) {
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
        } catch (Exception e) {
            Log.d("Paperchase", "Paperchase konnte nicht in Json überführt werden.");
        }
    }
}
