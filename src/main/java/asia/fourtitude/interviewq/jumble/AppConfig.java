package asia.fourtitude.interviewq.jumble;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.PrintStream;
import java.util.Scanner;

@Configuration
public class AppConfig {


    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    public PrintStream printStream() {
        return new PrintStream(System.out);
    }

}
