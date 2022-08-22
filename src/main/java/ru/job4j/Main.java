package ru.job4j;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Properties loadDBProperties() {
        Properties cfg = new Properties();
        try (InputStream in = Main.class.getClassLoader().getResourceAsStream(
                "db.properties")) {
            cfg.load(in);
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return cfg;
    }

    @Bean
    public BasicDataSource loadPool() {
        Properties cfg = loadDBProperties();
        BasicDataSource pool = new BasicDataSource();
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxTotal(100);
        pool.setMaxOpenPreparedStatements(100);
        return pool;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("Go to: http://localhost:8080/index");
    }
}
