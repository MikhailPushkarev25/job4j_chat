package ru.job4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.job4j.model.User;
import ru.job4j.repository.UserRepository;

@SpringBootApplication
class Job4jChatApplicationTests {

    public static void main(String[] args) {
        SpringApplication.run(Job4jChatApplicationTests.class, args);
    }

    @Bean
    public CommandLineRunner run(UserRepository userRepository) throws Exception {
        return (String[] args) -> {
            User user1 = new User("Mikle", "mikhail");
            User user2 = new User("Jenny", "jenny@domain.com");
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.findAll().forEach(System.out::println);
        };

    }

}
