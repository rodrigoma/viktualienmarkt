package com.moip.hackday.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class ProductExtractor {

    private static final Logger logger = LoggerFactory.getLogger(ProductExtractor.class);

    private Matcher matcher;

    public ProductExtractor(String source) {
        Pattern p = compile("(.+)\\s*por\\s*(R\\$\\s*[\\d,.]+)\\s*(.+)?");

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
            logger.error("MalformedURLException: " + url);
            return "";
        }
    }
}