package com.moip.hackday.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class SlackUtil {

    public static String getUsername(String userId, String token) {
        String url = "https://slack.com/api/users.info?token=" + token + "&user=" + userId;
        String json = null;
        try {
            json = readUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(json);
        JSONObject userObject = jsonObject.getJSONObject("user");
        String username = userObject.getString("name");
        return username;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}