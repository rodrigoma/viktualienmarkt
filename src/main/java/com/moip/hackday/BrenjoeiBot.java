package com.moip.hackday;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moip.hackday.domain.entity.Product;
import com.moip.hackday.domain.repository.ProductRepository;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.ramswaroop.jbot.core.slack.EventType.DIRECT_MESSAGE;

@Component
public class BrenjoeiBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(BrenjoeiBot.class);

    private Map<String, Product> products = new HashMap<String, Product>();

    @Autowired
    private ProductRepository productRepository;

    @Value("${slackBotToken}")
    private String slackToken;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    @Controller(pattern = "(quero vender)", next = "productName", events = {DIRECT_MESSAGE})
    public void sellProduct(WebSocketSession session, Event event) {
        logger.info("Quero vender " + products.size());
        logger.info("User null: " + (event.getUser() == null));
        startConversation(event, "productName");
        Product product = getProduct(event);
        products.put(event.getUserId(), product);
        reply(session, event, new Message("O que você quer vender?"));
    }

    @Controller(next = "productPrice", events = {DIRECT_MESSAGE})
    public void productName(WebSocketSession session, Event event) {
        logger.info("Nome do produto " + products.size());
        logger.info("User null: " + (event.getUser() == null));
        getProduct(event).setName(event.getText());
        reply(session, event, new Message("Por quanto você quer vender?"));
        nextConversation(event);
    }

    @Controller(next = "productImage", events = {DIRECT_MESSAGE})
    public void productPrice(WebSocketSession session, Event event) {
        logger.info("Qual o preço " + products.size());
        logger.info("User null: " + (event.getUser() == null));
        getProduct(event).setPrice(event.getText());
        reply(session, event, new Message("Quer mandar uma imagem? Se sim, me manda a URL aqui"));
        nextConversation(event);
    }

    @Controller(events = {DIRECT_MESSAGE})
    public void productImage(WebSocketSession session, Event event) {
        logger.info("Adicionar imagem " + products.size());
        logger.info("User null: " + (event.getUser() == null));
        Product product = getProduct(event);
        if (!isPositiveAnswer(event.getText())) {
            product.setUrl(event.getText());
        }
        productRepository.save(product);
        reply(session, event, new Message("Anuncio criado com sucesso"));
        stopConversation(event);
    }

    private boolean isPositiveAnswer(String answer){
        List<String> positives = new ArrayList<>();
        positives.add("sim");
        positives.add("aham");
        positives.add("bora");
        positives.add("vamo");
        positives.add("fechou");

        String normalizedAnswer = removeSpecialCharacters(answer.toLowerCase());

        for (String positive : positives){
            if (normalizedAnswer.contains(positive)) return true;
        }
    }

    public static String removeSpecialCharacters(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

//    @Controller(events = {DIRECT_MESSAGE})
//    public void confirm(WebSocketSession session, Event event) {
//        logger.info("Confirmar " + products.size());
//        logger.info("User null: " + (event.getUser() == null));
//        reply(session, event, getProduct(event).toRichMessage());
//        stopConversation(event);
//    }

    private Product getProduct(Event event) {
        logger.info("Event userid:" + event.getUserId());
        logger.info("Products size: " + products.size());
        logger.info("User null: " + (event.getUser() == null));
        return (products.containsKey(event.getUserId()) ? products.get(event.getUserId()) : new Product().setSellerName(event.getUserId()));
    }

    private String toJSONString(RichMessage richMessage) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(richMessage);
    }
}
