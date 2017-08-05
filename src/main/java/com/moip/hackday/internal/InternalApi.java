package com.moip.hackday.internal;

import com.moip.hackday.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.moip.hackday.bot.BotListener.PRODUCTS;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class InternalApi {

    private static final Logger logger = LoggerFactory.getLogger(InternalApi.class);

    @Autowired
    private ProductRepository productRepository;

    @RequestMapping(value = "/internal/mongo/clean", method = GET)
    public ResponseEntity cleanMongo() {
        logger.info("Cleaning Mongo...");
        productRepository.deleteAll();
        logger.info("Mongo clean");
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/internal/map/clean", method = GET)
    public ResponseEntity cleanMap() {
        logger.info("Cleaning Map...");
        PRODUCTS.clear();
        logger.info("Map clean");
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/internal/map/show", method = GET)
    public ResponseEntity showMap() {
        logger.info("Showing Map...");
        return ResponseEntity.ok().body(PRODUCTS.values());
    }
}