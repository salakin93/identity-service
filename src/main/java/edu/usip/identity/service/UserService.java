package edu.usip.identity.service;

import edu.usip.identity.domain.AppUser;
import edu.usip.identity.domain.Role;
import edu.usip.identity.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    @Transactional
    public AppUser create(String name, String phone, Role role) {
        repo.findByPhone(phone).ifPresent(u -> { throw new RuntimeException("Usuario ya existe"); });

        return repo.save(AppUser.builder()
                .name(name)
                .phone(phone)
                .role(role)
                .active(true)
                .build());
    }

    @Transactional
    public AppUser update(String phone, String name, Role role) {
        AppUser u = repo.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        u.setName(name);
        u.setRole(role);
        return repo.save(u);
    }

    @Transactional
    public void disable(String phone) {
        AppUser u = repo.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setActive(false);
        repo.save(u);
    }

    @Transactional(readOnly = true)
    public List<AppUser> list() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public AppUser getActiveOrThrow(String phone) {
        AppUser u = repo.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!u.isActive()) throw new RuntimeException("Usuario desactivado");
        return u;
    }
}