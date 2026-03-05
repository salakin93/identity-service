package edu.usip.identity;

import edu.usip.identity.domain.Role;
import edu.usip.identity.repo.UserRepository;
import edu.usip.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository repo;
    private final UserService userService;

    @Override
    public void run(String @NonNull ... args) {
        if (repo.count() == 0) {
            userService.create("Admin Inicial", "70000001", Role.ROLE_ADMIN);
        }
    }
}