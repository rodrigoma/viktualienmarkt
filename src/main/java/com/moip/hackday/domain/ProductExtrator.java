package com.moip.hackday.domain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductExtrator {

    private Matcher matcher;

    public ProductExtrator(String source) {
        Pattern p = Pattern.compile("(.+)\\s*por\\s*(R\\$\\s*[\\d,.]+)\\s*(.+)?");

        matcher = p.matcher(source);
        matcher.find();
    }

    public String getName() {
        return matcher.group(1).trim();
    }

    public String getPrice() {
        return matcher.group(2).trim();
    }

    public String getUrl() {
        String url = matcher.group(3);
        try {
            new URL(url);
            return url.trim();
        } catch (MalformedURLException ex) {
            return "";
        }
    }
}
