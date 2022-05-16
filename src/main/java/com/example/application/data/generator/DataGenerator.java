package com.example.application.data.generator;

import com.example.application.database.DBManager;
import com.vaadin.flow.spring.annotation.SpringComponent;

import com.example.application.data.service.UserRepository;
import com.example.application.data.entity.User;

import java.util.Collections;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.application.data.Role;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.info("Generating users");
            String logoUrl = "https://habrastorage.org/getpro/moikrug/uploads/education_platform/000/000/036/logo/54ab1ce8c393eb3df1474846ce0a0e2c.png";

            if (!DBManager.getConnectionState())
                DBManager.connectDB();
            List<List<String>> users = DBManager.getRowsFromUsersTable();
            for (List<String> u : users) {
                User user = new User();
                user.setName(u.get(0));
                user.setUsername(u.get(0));
                user.setHashedPassword(passwordEncoder.encode(u.get(1)));
                user.setProfilePictureUrl(logoUrl);
                if (u.get(2).equals("user"))
                    user.setRoles(Collections.singleton(Role.USER));
                else user.setRoles(Collections.singleton(Role.ADMIN));
                userRepository.save(user);
            }

            logger.info("Generated demo data");
        };
    }
}
