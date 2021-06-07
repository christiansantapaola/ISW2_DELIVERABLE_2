package it.uniroma2.santapaola.christian.utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JsonHelper {

    /**
     * private constructor to hide the implicitly public one.
     */
    private JsonHelper() {}

    public static String readAll(Reader rd) throws IOException {
        var sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try (var rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
            String jsonText = readAll(rd);
            return new JSONArray(jsonText);
        } finally {
            is.close();
        }
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try (var rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } finally {
            is.close();
        }
    }

}
