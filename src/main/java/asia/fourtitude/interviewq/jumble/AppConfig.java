package asia.fourtitude.interviewq.jumble;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.PrintStream;
import java.util.Scanner;

@Configuration
public class AppConfig {


    @Profile("console")
    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

    @Profile("console")
    @Bean
    public PrintStream printStream() {
        return new PrintStream(System.out);
    }

}
