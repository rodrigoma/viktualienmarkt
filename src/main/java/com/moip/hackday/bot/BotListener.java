package com.moip.hackday.bot;

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

import java.util.HashMap;
import java.util.Map;

import static com.moip.hackday.util.SlackUtil.getUsername;
import static com.moip.hackday.util.StringUtil.cleanUrl;
import static com.moip.hackday.util.StringUtil.toStr;
import static me.ramswaroop.jbot.core.slack.EventType.DIRECT_MESSAGE;

@Component
public class BotListener extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(BotListener.class);

    public final static Map<String, Product> PRODUCTS = new HashMap();

    @Autowired
    private ProductRepository productRepository;

    @Value("${slack.token.bot}")
    private String slackToken;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    @Controller(pattern = "(quero vender)|(wanna sell)", next = "productName", events = {DIRECT_MESSAGE})
    public void sellProduct(WebSocketSession session, Event event) {
        logger.info("Products in queue: {} ", PRODUCTS.size());
        logger.info("[1] Sell Product: {} ", toStr(event));

        startConversation(event, "productName");

        getProduct(event).setSellerName(getUsername(event.getUserId(), slackToken));

        reply(session, event, new Message("What do you want to sell?"));
    }

    @Controller(next = "productPrice", events = {DIRECT_MESSAGE})
    public void productName(WebSocketSession session, Event event) {
        logger.info("Products in queue: {} ", PRODUCTS.size());
        logger.info("[2] productName(): {} ", toStr(event));

        getProduct(event).setName(event.getText());

        reply(session, event, new Message("How much do you want to sell?"));

        nextConversation(event);
    }

    @Controller(next = "productImage", events = {DIRECT_MESSAGE})
    public void productPrice(WebSocketSession session, Event event) {
        logger.info("Products in queue: {} ", PRODUCTS.size());
        logger.info("[3] productPrice(): {} ", toStr(event));

        getProduct(event).setPrice(event.getText());

        reply(session, event, new Message("Give me the image URL, or just type NO to skip."));

        nextConversation(event);
    }

    @Controller(events = {DIRECT_MESSAGE})
    public void productImage(WebSocketSession session, Event event) {
        logger.info("Products in queue: {} ", PRODUCTS.size());
        logger.info("[4] productImage(): {} ", toStr(event));

        Product product = getProduct(event);

        if (!event.getText().equalsIgnoreCase("no")) {
            product.setUrl(cleanUrl(event.getText()));
        }

        productRepository.save(product);

        reply(session, event, new Message("Thanks! Offer was created successfully. " +
                "You can use the slash commands to list all offers. Have a nice sell."));

        finnishConversation(event);
    }

    @Controller(pattern = "(valew)|(thanks)|(tks)|(thank you)", events = {DIRECT_MESSAGE})
    public void endConversation(WebSocketSession session, Event event) {
        logger.info("Products in queue: {} ", PRODUCTS.size());
        logger.info("[5] End Conversation: {} ", toStr(event));

        reply(session, event, new Message("Welcome! It's a pleasure help you."));

        finnishConversation(event);
    }

    private Product getProduct(Event event) {
        if (!PRODUCTS.containsKey(event.getUserId())) {
            PRODUCTS.put(event.getUserId(), new Product());
        }

        return PRODUCTS.get(event.getUserId());
    }

    private void finnishConversation(Event event) {
        PRODUCTS.remove(event.getUserId());

        stopConversation(event);
    }
}