package com.friendfinder;

import com.friendfinder.dto.RegisterRequest;
import com.friendfinder.model.User;
import com.friendfinder.repository.UserRepository;
import com.friendfinder.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FriendFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendFinderApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(UserService userService, UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                // Seed Dummy Account 1
                RegisterRequest req1 = new RegisterRequest();
                req1.setName("tejuuuu");
                req1.setEmail("tejuuuu@test.com");
                req1.setPassword("123");
                userService.registerUser(req1);
                User tejuuuu = userRepository.findByEmail("tejuuuu@test.com").get();
                userService.addInterestToUser(tejuuuu.getId(), "dance");
                userService.addInterestToUser(tejuuuu.getId(), "music");
                userService.addInterestToUser(tejuuuu.getId(), "traveling");

                // Seed Dummy Account 2
                RegisterRequest req2 = new RegisterRequest();
                req2.setName("usha");
                req2.setEmail("usha@test.com");
                req2.setPassword("123");
                userService.registerUser(req2);
                User usha = userRepository.findByEmail("usha@test.com").get();
                userService.addInterestToUser(usha.getId(), "painting");
                userService.addInterestToUser(usha.getId(), "coding");
                userService.addInterestToUser(usha.getId(), "reading");

                // Seed Dummy Account 3
                RegisterRequest req3 = new RegisterRequest();
                req3.setName("nehaa");
                req3.setEmail("nehaa@test.com");
                req3.setPassword("123");
                userService.registerUser(req3);
                User nehaa = userRepository.findByEmail("nehaa@test.com").get();
                userService.addInterestToUser(nehaa.getId(), "music");
                userService.addInterestToUser(nehaa.getId(), "movies");
                userService.addInterestToUser(nehaa.getId(), "cooking");

                // Seed Dummy Account 4 (NEW)
                RegisterRequest req4 = new RegisterRequest();
                req4.setName("Alex");
                req4.setEmail("alex@test.com");
                req4.setPassword("123");
                userService.registerUser(req4);
                User alex = userRepository.findByEmail("alex@test.com").get();
                userService.addInterestToUser(alex.getId(), "sports");
                userService.addInterestToUser(alex.getId(), "gaming");
                userService.addInterestToUser(alex.getId(), "traveling");

                // Seed Dummy Account 5 (NEW)
                RegisterRequest req5 = new RegisterRequest();
                req5.setName("Jordan");
                req5.setEmail("jordan@test.com");
                req5.setPassword("123");
                userService.registerUser(req5);
                User jordan = userRepository.findByEmail("jordan@test.com").get();
                userService.addInterestToUser(jordan.getId(), "photography");
                userService.addInterestToUser(jordan.getId(), "coding");
                userService.addInterestToUser(jordan.getId(), "tech");

                // Seed Dummy Account 6 (NEW)
                RegisterRequest req6 = new RegisterRequest();
                req6.setName("Sam");
                req6.setEmail("sam@test.com");
                req6.setPassword("123");
                userService.registerUser(req6);
                User sam = userRepository.findByEmail("sam@test.com").get();
                userService.addInterestToUser(sam.getId(), "sports");
                userService.addInterestToUser(sam.getId(), "fitness");

                // Seed Dummy Account 7 (NEW)
                RegisterRequest req7 = new RegisterRequest();
                req7.setName("Taylor");
                req7.setEmail("taylor@test.com");
                req7.setPassword("123");
                userService.registerUser(req7);
                User taylor = userRepository.findByEmail("taylor@test.com").get();
                userService.addInterestToUser(taylor.getId(), "music");
                userService.addInterestToUser(taylor.getId(), "fashion");
                userService.addInterestToUser(taylor.getId(), "reading");

                // Seed Dummy Account 8 (NEW)
                RegisterRequest req8 = new RegisterRequest();
                req8.setName("Casey");
                req8.setEmail("casey@test.com");
                req8.setPassword("123");
                userService.registerUser(req8);
                User casey = userRepository.findByEmail("casey@test.com").get();
                userService.addInterestToUser(casey.getId(), "gaming");
                userService.addInterestToUser(casey.getId(), "tech");

                // Seed Dummy Account 9
                RegisterRequest req9 = new RegisterRequest();
                req9.setName("krishnasree");
                req9.setEmail("krishnasree@test.com");
                req9.setPassword("123");
                userService.registerUser(req9);
                User krishnasree = userRepository.findByEmail("krishnasree@test.com").get();
                userService.addInterestToUser(krishnasree.getId(), "dance");
                userService.addInterestToUser(krishnasree.getId(), "music");
                userService.addInterestToUser(krishnasree.getId(), "traveling");

                // Seed Dummy Account 10 (NEW)
                RegisterRequest req10 = new RegisterRequest();
                req10.setName("bindu");
                req10.setEmail("bindu@test.com");
                req10.setPassword("123");
                userService.registerUser(req10);
                User bindu = userRepository.findByEmail("bindu@test.com").get();
                userService.addInterestToUser(bindu.getId(), "adventure");
                userService.addInterestToUser(bindu.getId(), "movies");
                userService.addInterestToUser(bindu.getId(), "gym");

                // Seed Dummy Account 11 (NEW)
                RegisterRequest req11 = new RegisterRequest();
                req11.setName("pooja");
                req11.setEmail("pooja@test.com");
                req11.setPassword("123");
                userService.registerUser(req11);
                User pooja = userRepository.findByEmail("pooja@test.com").get();
                userService.addInterestToUser(pooja.getId(), "blogging");
                userService.addInterestToUser(pooja.getId(), "technology");
                userService.addInterestToUser(pooja.getId(), "socialmedia");

                // Seed Dummy Account 12 (NEW)
                RegisterRequest req12 = new RegisterRequest();
                req12.setName("priyanka");
                req12.setEmail("priyanka@test.com");
                req12.setPassword("123");
                userService.registerUser(req12);
                User priyanka = userRepository.findByEmail("priyanka@test.com").get();
                userService.addInterestToUser(priyanka.getId(), "baking");
                userService.addInterestToUser(priyanka.getId(), "gaming");
                userService.addInterestToUser(priyanka.getId(), "singing");

                System.out.println(
                        "✅ Dummy Users (tejuuuu, usha, nehaa, Alex, Jordan, Sam, Taylor, Casey, krishnasree, bindu, pooja, priyanka) have been auto-created!");
            }
        };
    }
}
