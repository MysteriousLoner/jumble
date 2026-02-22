package asia.fourtitude.interviewq.jumble;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JumbleWebApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JumbleWebApplication.class);
        app.setAdditionalProfiles("web");
        app.run(args);
    }

}

