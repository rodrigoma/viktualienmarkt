package com.moip.hackday.util;

import java.text.Normalizer;

public class StringUtil {
    public static String removeSpecialCharacters(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}
