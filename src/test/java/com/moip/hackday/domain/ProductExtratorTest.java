package com.moip.hackday.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Created by william on 04/08/17.
 */
public class ProductExtratorTest {

    @Test
    public void shouldExtract() {
        String text = "Controle Remoto por R$ 150 http://cafe.com";
        ProductExtrator extrator = new ProductExtrator(text);

        assertEquals("Controle Remoto", extrator.getName());
        assertEquals("R$ 150", extrator.getPrice());
        assertEquals("http://cafe.com", extrator.getUrl());
    }

    @Test
    public void shouldNotExtractInvalidURL() {
        String text = "Controle Remoto por R$ 150 invalid://@cafe";
        ProductExtrator extrator = new ProductExtrator(text);

        assertEquals("", extrator.getUrl());
    }

    @Test
    public void shouldNotExtractWithoutURL() {
        String text = "Controle Remoto por R$ 150";
        ProductExtrator extrator = new ProductExtrator(text);

        assertEquals("", extrator.getUrl());
    }
}
