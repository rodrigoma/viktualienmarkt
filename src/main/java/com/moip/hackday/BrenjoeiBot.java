package com.moip.hackday;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moip.hackday.domain.entity.Product;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class BrenjoeiBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(BrenjoeiBot.class);

    private Map<String, Product> products = new HashMap<String, Product>();

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

    @Controller(pattern = "(quero vender)", next = "productName", events = {EventType.DIRECT_MESSAGE})
    public void sellProduct(WebSocketSession session, Event event) {
        logger.info("Quero vender " + products.size());
        logger.info("User null: " + (event.getUser() == null));
        startConversation(event, "productName");
        Product product = getProduct(event);
        products.put(event.getUserId(), product);
        reply(session, event, new Message("O que você quer vender?"));
    }

    @Controller(next = "productPrice", events = {EventType.DIRECT_MESSAGE})
    public void productName(WebSocketSession session, Event event) {
        logger.info("Nome do produto " + products.size());
        logger.info("User null: " + (event.getUser() == null));
        getProduct(event).setName(event.getText());
        reply(session, event, new Message("Por quanto você quer vender?"));
        nextConversation(event);
    }

    @Controller(next = "productImage", events = {EventType.DIRECT_MESSAGE})
    public void productPrice(WebSocketSession session, Event event) {
        logger.info("Qual o preço " + products.size());
        logger.info("User null: " + (event.getUser() == null));
        getProduct(event).setPrice(event.getText());
        reply(session, event, new Message("Adicione uma url da imagem: (Digite NÃO para não adicionar)"));
        nextConversation(event);
    }

    @Controller(next = "confirm", events = {EventType.DIRECT_MESSAGE})
    public void productImage(WebSocketSession session, Event event) {
        logger.info("Adicionar imagem " + products.size());
        logger.info("User null: " + (event.getUser() == null));
        if (!event.getText().equalsIgnoreCase("não")) {
            getProduct(event).setUrl(event.getText());
        }
        reply(session, event, getProduct(event).toRichMessage());
        nextConversation(event);
    }

    @Controller(events = {EventType.DIRECT_MESSAGE})
    public void confirm(WebSocketSession session, Event event) {
        logger.info("Confirmar " + products.size());
        logger.info("User null: " + (event.getUser() == null));
        reply(session, event, getProduct(event).toRichMessage());
        stopConversation(event);
    }

    private Product getProduct(Event event) {
        logger.info("Event userid:" + event.getUserId());
        logger.info("Products size: " + products.size());
        logger.info("User null: " + (event.getUser() == null));
        return (products.containsKey(event.getUserId()) ? products.get(event.getUserId()) : new Product().setSellerName(event.getUserId()));
    }

    private final void reply(WebSocketSession session, Event event, RichMessage reply) {
        try {
            if(reply.getChannel() == null && event.getChannelId() != null) {
                reply.setChannel(event.getChannelId());
            }

            session.sendMessage(new TextMessage(toJSONString(reply)));
            if(logger.isDebugEnabled()) {
                logger.debug("Reply (RichMessage): {}", toJSONString(reply));
            }
        } catch (IOException var5) {
            logger.error("Error sending event: {}. Exception: {}", event.getText(), var5.getMessage());
        }
    }

    private String toJSONString(RichMessage richMessage) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(richMessage);
    }
}
