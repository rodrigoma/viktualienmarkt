package com.moip.hackday.slash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moip.hackday.domain.ProductExtractor;
import com.moip.hackday.domain.entity.Product;
import com.moip.hackday.domain.repository.ProductRepository;
import me.ramswaroop.jbot.core.slack.models.Attachment;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

        RichMessage richMessage = createRichMessage("Here is the product:", product.toAttachments(userId, userName));

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

        List<Product> productList = productRepository.findByNameLike(text);

        List<Attachment> attachmentList = new ArrayList();
        productList.forEach(product -> attachmentList.addAll(product.toAttachments(userId, userName)));

        String msg = "I found some interesting offers :)";
        if (attachmentList.size() < 1) {
            msg = "I did not find any interesting offers, sorry. :(";
        }

        RichMessage richMessage = createRichMessage(msg, attachmentList);

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

        RichMessage richMessage = createRichMessage("Cleaned up!", null);

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

        RichMessage richMessage = createRichMessage("Test Action", null);

        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Reply (RichMessage): {}", new ObjectMapper().writeValueAsString(richMessage));
            } catch (JsonProcessingException e) {
                logger.debug("Error parsing RichMessage: ", e);
            }
        }

        return richMessage.encodedMessage();
    }

    private RichMessage createRichMessage(String message, List<Attachment> attachmentList) {
        RichMessage richMessage = new RichMessage();
        richMessage.setResponseType("ephemeral");
        richMessage.setText(message);
        richMessage.setAttachments(attachmentList.stream().toArray(Attachment[]::new));
        return richMessage;
    }
}