package com.developerdreamteam.jia.auth.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MongoUriPrinter implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MongoUriPrinter.class);

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Override
    public void run(String... args) throws Exception {
        logger.info("MongoDB URI: {}", mongoUri);
        logger.info("MongoDB Database: {}", databaseName);
    }
}
