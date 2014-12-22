package mobi.fhdo.geoschnitzeljagd.Contexts;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mobi.fhdo.geoschnitzeljagd.Model.User;

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
}
