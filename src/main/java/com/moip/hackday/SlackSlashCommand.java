package com.moip.hackday;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moip.hackday.domain.ProductExtrator;
import com.moip.hackday.domain.entity.Product;
import com.moip.hackday.domain.repository.ProductRepository;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SlackSlashCommand {

    private static final Logger logger = LoggerFactory.getLogger(SlackSlashCommand.class);

    @Value("${slashCommandToken}")
    private String slackToken;

    @Autowired
    private ProductRepository productRepository;

    @RequestMapping(value = "/products/create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
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

        ProductExtrator extrator = new ProductExtrator(text);

        Product product = new Product()
                .setSellerName(userName)
                .setName(extrator.getName())
                .setPrice(extrator.getPrice())
                .setUrl(extrator.getUrl());

        product = productRepository.save(product);

        RichMessage richMessage = new RichMessage("Product " + product.getName() + " offered for sale!");
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

    @RequestMapping(value = "/products/list",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
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
        List<ButtonAttachment> attachments = products.stream().map(p -> p.toAttachment(userId, userName)).collect(Collectors.toList());
        ButtonAttachment[] att = new ButtonAttachment[attachments.size()];
        att = attachments.toArray(att);

        RichMessage richMessage = new RichMessage("Encontrei algumas ofertas interessantes :)");
        richMessage.setAttachments(att);
        richMessage.setResponseType("ephemeral");

        if (att.length  < 1) {
            richMessage.setText("Não encontrei nenhuma oferta interessante :(");
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

    @RequestMapping(value = "/products/clear",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
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

    @RequestMapping(value = "/products/action",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage action(@RequestParam("token") String token,
                              @RequestBody String body) {

        if (!token.equals(slackToken)) {
            return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
        }



        logger.info(body);

        RichMessage richMessage = new RichMessage("Qq coisa");
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
