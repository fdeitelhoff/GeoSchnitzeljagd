package mobi.fhdo.geoschnitzeljagd.Model;

/**
 * Created by Fabian Deitelhoff on 25.11.2014.
 */
public class Mark {

    private int id;
    private int paperchaseId;
    private double latitude;
    private double longitude;



    private String hint;
    private int sequence;

    public Mark(int id, double latitude, double longitude, String hint, int sequence) {
        this(latitude, longitude, hint, sequence);
    }

    public Mark(double latitude, double longitude, String hint, int sequence) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.hint = hint;
        this.sequence = sequence;
    }

    public Mark(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
   }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getPaperchaseId() {
        return paperchaseId;
    }

    public void setPaperchaseId(int paperchaseId) {
        this.paperchaseId = paperchaseId;
    }
}
