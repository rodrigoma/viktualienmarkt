package com.moip.hackday;

import com.moip.hackday.domain.entity.Product;
import com.moip.hackday.domain.repository.ProductRepository;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
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

    private final static Map<String, Product> PRODUCTS = new HashMap();

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
        logger.info("Quero vender " + PRODUCTS.size());
        logger.info("Event type: " + event.getType());
        logger.info("User null: " + (event.getUser() == null));
        startConversation(event, "productName");
        Product product = getProduct(event);
        PRODUCTS.put(event.getUserId(), product);
        reply(session, event, new Message("O que você quer vender?"));
    }

    @Controller(next = "productPrice", events = {DIRECT_MESSAGE})
    public void productName(WebSocketSession session, Event event) {
        logger.info("Nome do produto " + PRODUCTS.size());
        logger.info("Event type: " + event.getType());
        logger.info("User null: " + (event.getUser() == null));

        String productName = event.getText();

        if (isVava(productName)) {
            reply(session, event, new Message("Valor de fofura inestimável! S2"));
            stopConversation(event);
            return;
        }

        getProduct(event).setName(productName);
        reply(session, event, new Message("Por quanto você quer vender?"));
        nextConversation(event);
    }

    private boolean isVava(String productName) {
        return "vava".equals(productName.toLowerCase().replace(" ", ""));
    }

    @Controller(next = "productImage", events = {DIRECT_MESSAGE})
    public void productPrice(WebSocketSession session, Event event) {
        logger.info("Qual o preço " + PRODUCTS.size());
        logger.info("Event type: " + event.getType());
        logger.info("User null: " + (event.getUser() == null));
        getProduct(event).setPrice(event.getText());
        reply(session, event, new Message("Quer mandar uma imagem? Me envie a URL, ou responda NÃO/NOPE/NEM."));
        nextConversation(event);
    }

    @Controller(events = {DIRECT_MESSAGE})
    public void productImage(WebSocketSession session, Event event) {
        logger.info("Adicionar imagem " + PRODUCTS.size());
        logger.info("Event type: " + event.getType());
        logger.info("User null: " + (event.getUser() == null));
        Product product = getProduct(event);

        if (isNegativeAnswer(event.getText())) {
            product.setUrl(event.getText().replace("<","").replace(">",""));
        }
        productRepository.save(product);
        PRODUCTS.remove(event.getUserId());
        reply(session, event, new Message("Anuncio criado com sucesso"));
        stopConversation(event);
    }

    private boolean isNegativeAnswer(String answer){
        List<String> negatives = new ArrayList();
        negatives.add("nao");
        negatives.add("nope");
        negatives.add("nem");

        String normalizedAnswer = removeSpecialCharacters(answer.toLowerCase());

        for (String negative : negatives){
            if (normalizedAnswer.contains(negative)) return true;
        }

        return false;
    }

    public static String removeSpecialCharacters(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    private Product getProduct(Event event) {
        logger.info("Event userid:" + event.getUserId());
        logger.info("Products size: " + PRODUCTS.size());
        logger.info("User null: " + (event.getUser() == null));
        String username = BrenjoeiUtil.getUsername(event.getUserId(), slackToken);
        return (PRODUCTS.containsKey(username) ? PRODUCTS.get(username) : new Product().setSellerName(username));
    }
}
