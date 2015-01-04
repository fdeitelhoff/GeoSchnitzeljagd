package mobi.fhdo.geoschnitzeljagd.Contexts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mobi.fhdo.geoschnitzeljagd.Model.User;

// Für den Systemübergreifenden User zugriff
// Beinhaltet immer den aktuell eingeloggten Benutzer
public class UserContext {

    private static UserContext instance = null;
    private User user;

    private UserContext() {
    }

    public static UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }

        return instance;
    }

    public User getLoggedInUser() {
        return user;
    }

    public void userLoggedIn(User user) {
        this.user = user;
    }

    // Erstellt einen MD5 Hash
    public static final String getMd5(final String s)
    {
        try
        {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
            {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    // Reads an InputStream and converts it to a String.
    public static String readIt(InputStream stream) throws IOException, UnsupportedEncodingException
    {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try
        {

            br = new BufferedReader(new InputStreamReader(stream));
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }
}
