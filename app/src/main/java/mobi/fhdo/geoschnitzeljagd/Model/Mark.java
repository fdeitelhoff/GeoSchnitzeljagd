package mobi.fhdo.geoschnitzeljagd.Model;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.Serializable;
import java.util.UUID;

public class Mark implements Serializable {

    private UUID id;
    private UUID paperchaseId;
    private double latitude;
    private double longitude;


    private String hint;
    private int sequence;

    public Mark(UUID id, UUID paperchaseId, double latitude, double longitude, String hint, int sequence) {
        this.id = id;
        this.paperchaseId = paperchaseId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hint = hint;
        this.sequence = sequence;
    }

    public Mark(UUID id, double latitude, double longitude, String hint, int sequence) {
        this(latitude, longitude, hint, sequence);
        this.id = id;
    }

    public Mark(double latitude, double longitude, String hint, int sequence) {
        this(latitude, longitude);
        this.hint = hint;
        this.sequence = sequence;
    }

    public Mark(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = UUID.randomUUID();
    }

    public static Mark jsonToObject(JsonReader reader) {
        UUID id = null;
        UUID paperchaseId = null;
        double latitude = 0;
        double longitude = 0;
        String hint = null;
        int sequence = 0;

        try {
            if (reader.peek() == JsonToken.BEGIN_OBJECT)
                reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("MID") && reader.hasNext()) {
                    id = UUID.fromString(reader.nextString());
                } else if (name.equals("PID") && reader.hasNext()) {
                    paperchaseId = UUID.fromString(reader.nextString());
                } else if (name.equals("Latitude") && reader.hasNext()) {
                    latitude = reader.nextDouble();
                } else if (name.equals("Longitude") && reader.hasNext()) {
                    longitude = reader.nextDouble();
                } else if (name.equals("Hint") && reader.hasNext()) {
                    try {
                        hint = reader.nextString();
                    } catch (Exception e) {
                        hint = "";
                        reader.skipValue();
                    }
                } else if (name.equals("Sequence") && reader.hasNext()) {
                    sequence = reader.nextInt();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            Log.d("Mark", "Mark konnte nicht geparst werden.");
            return null;
        }

        if (id != null && paperchaseId != null)
            return new Mark(id, paperchaseId, latitude, longitude, hint, sequence);
        else
            return null;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getSequence() {
        return sequence;
    }

    public UUID getPaperchaseId() {
        return paperchaseId;
    }

    public void setPaperchaseId(UUID paperchaseId) {
        this.paperchaseId = paperchaseId;
    }
}
