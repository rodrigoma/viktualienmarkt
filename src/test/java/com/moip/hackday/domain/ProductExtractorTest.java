package com.moip.hackday.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by william on 04/08/17.
 */
public class ProductExtractorTest {

    @Test
    public void shouldExtract() {
        String text = "Controle Remoto por R$ 150 http://cafe.com";
        ProductExtractor extrator = new ProductExtractor(text);

        assertEquals("Controle Remoto", extrator.getName());
        assertEquals("R$ 150", extrator.getPrice());
        assertEquals("http://cafe.com", extrator.getUrl());
    }

    @Test
    public void shouldNotExtractInvalidURL() {
        String text = "Controle Remoto por R$ 150 invalid://@cafe";
        ProductExtractor extrator = new ProductExtractor(text);

        assertEquals("", extrator.getUrl());
    }

    @Test
    public void shouldNotExtractWithoutURL() {
        String text = "Controle Remoto por R$ 150";
        ProductExtractor extrator = new ProductExtractor(text);

        assertEquals("", extrator.getUrl());
    }
}