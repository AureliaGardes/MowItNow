package fr.xebia;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

// By design, SpringBootApplication doit être à un niveau de package supérieur aux classes qu'elle utilise.
@SpringBootApplication
@Slf4j
public class Start {
    public static void main(String... args) {
        if (args == null || args.length == 0) {
            log.error("fichier !!!!");
            return;
        }
        new MowItNow(args[0]).run();
    }
}
