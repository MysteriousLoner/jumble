package asia.fourtitude.interviewq.jumble;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;

import asia.fourtitude.interviewq.jumble.console.ConsoleApp;

@SpringBootApplication(exclude = {
        WebMvcAutoConfiguration.class,
        ThymeleafAutoConfiguration.class,
        ErrorMvcAutoConfiguration.class
})
public class JumbleConsoleApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JumbleConsoleApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.setAdditionalProfiles("console");
        app.run(args);
    }

    @Bean
    public CommandLineRunner run(ConsoleApp consoleApp) {
        return args -> consoleApp.run();
    }

}
