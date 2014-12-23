package mobi.fhdo.geoschnitzeljagd.Model;

import java.util.UUID;

public class Mark {

    private UUID id;
    private UUID paperchaseId;
    private double latitude;
    private double longitude;


    private String hint;
    private int sequence;

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
