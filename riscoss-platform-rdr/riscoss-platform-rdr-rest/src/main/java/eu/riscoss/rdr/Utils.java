package eu.riscoss.rdr;

import com.google.gson.Gson;

public class Utils
{
    private static Gson gson;

    public static Gson getGson()
    {
        if (gson == null) {
            gson = new Gson();
        }

        return gson;
    }
}
