package com.moip.hackday.slash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moip.hackday.domain.ProductExtractor;
import com.moip.hackday.domain.entity.Product;
import com.moip.hackday.domain.repository.ProductRepository;
import com.moip.hackday.jbot.model.ButtonAttachment;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class SlashCommandApi {

    private static final Logger logger = LoggerFactory.getLogger(SlashCommandApi.class);

    @Value("${slack.token.slash-command}")
    private String slackToken;

    @Autowired
    private ProductRepository productRepository;

    @RequestMapping(value = "/products/create", method = POST, consumes = APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage create(@RequestParam("token") String token,
                              @RequestParam("team_id") String teamId,
                              @RequestParam("team_domain") String teamDomain,
                              @RequestParam("channel_id") String channelId,
                              @RequestParam("channel_name") String channelName,
                              @RequestParam("user_id") String userId,
                              @RequestParam("user_name") String userName,
                              @RequestParam("command") String command,
                              @RequestParam("text") String text,
                              @RequestParam("response_url") String responseUrl) {
        if (!token.equals(slackToken)) {
            return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
        }

        ProductExtractor extractor = new ProductExtractor(text);

        Product product = new Product()
                .setSellerName(userName)
                .setName(extractor.getName())
                .setPrice(extractor.getPrice())
                .setUrl(extractor.getUrl());

        product = productRepository.save(product);

        RichMessage richMessage = new RichMessage("Product " + product.getName() + " offered for sale!");
        richMessage.setResponseType("ephemeral");

        //TODO AQUI retornar um preview do anuncio

        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Reply (RichMessage): {}", new ObjectMapper().writeValueAsString(richMessage));
            } catch (JsonProcessingException e) {
                logger.debug("Error parsing RichMessage: ", e);
            }
        }

        return richMessage.encodedMessage();
    }

    @RequestMapping(value = "/products/list", method = POST, consumes = APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage list(@RequestParam("token") String token,
                            @RequestParam("team_id") String teamId,
                            @RequestParam("team_domain") String teamDomain,
                            @RequestParam("channel_id") String channelId,
                            @RequestParam("channel_name") String channelName,
                            @RequestParam("user_id") String userId,
                            @RequestParam("user_name") String userName,
                            @RequestParam("command") String command,
                            @RequestParam("text") String text,
                            @RequestParam("response_url") String responseUrl) {
        if (!token.equals(slackToken)) {
            return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
        }

        List<Product> products = productRepository.findByNameLike(text);
        List<ButtonAttachment> attachments = products.stream().map(p -> p.toAttachment(userId, userName)).collect(toList());
        ButtonAttachment[] att = new ButtonAttachment[attachments.size()];
        att = attachments.toArray(att);

        RichMessage richMessage = new RichMessage("I found some interesting offers :)");
        richMessage.setAttachments(att);
        richMessage.setResponseType("ephemeral");

        if (att.length < 1) {
            richMessage.setText("I did not find any interesting offers, sorry. :(");
        }

        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Reply (RichMessage): {}", new ObjectMapper().writeValueAsString(richMessage));
            } catch (JsonProcessingException e) {
                logger.debug("Error parsing RichMessage: ", e);
            }
        }

        return richMessage.encodedMessage();
    }

    @RequestMapping(value = "/products/clear", method = POST, consumes = APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage clear(@RequestParam("token") String token,
                             @RequestParam("team_id") String teamId,
                             @RequestParam("team_domain") String teamDomain,
                             @RequestParam("channel_id") String channelId,
                             @RequestParam("channel_name") String channelName,
                             @RequestParam("user_id") String userId,
                             @RequestParam("user_name") String userName,
                             @RequestParam("command") String command,
                             @RequestParam("text") String text,
                             @RequestParam("response_url") String responseUrl) {

        if (!token.equals(slackToken)) {
            return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
        }

        productRepository.deleteAll();

        RichMessage richMessage = new RichMessage("Cleaned up!");
        richMessage.setResponseType("ephemeral");

        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Reply (RichMessage): {}", new ObjectMapper().writeValueAsString(richMessage));
            } catch (JsonProcessingException e) {
                logger.debug("Error parsing RichMessage: ", e);
            }
        }

        return richMessage.encodedMessage();
    }

    @RequestMapping(value = "/products/action", method = POST, consumes = APPLICATION_JSON_VALUE)
    public RichMessage action(@RequestBody String body) {
        logger.info(body);

        RichMessage richMessage = new RichMessage("Test Action");
        richMessage.setResponseType("ephemeral");

        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Reply (RichMessage): {}", new ObjectMapper().writeValueAsString(richMessage));
            } catch (JsonProcessingException e) {
                logger.debug("Error parsing RichMessage: ", e);
            }
        }

        return richMessage.encodedMessage();
    }
}