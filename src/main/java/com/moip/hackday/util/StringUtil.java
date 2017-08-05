package com.moip.hackday.util;

        import com.google.gson.Gson;

        import static java.text.Normalizer.Form.NFD;
        import static java.text.Normalizer.normalize;

public class StringUtil {
    public static String removeSpecialCharacters(String str) {
        return normalize(str, NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String toStr(Object object) {
        return new Gson().toJson(object);
    }

    public static String cleanUrl(String url) {
        return url.replace("<", "").replace(">", "");
    }
}