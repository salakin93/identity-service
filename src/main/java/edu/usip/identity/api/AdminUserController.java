package edu.usip.identity.api;

import edu.usip.identity.api.dto.request.UserRequest;
import edu.usip.identity.api.dto.response.UserResponse;
import edu.usip.identity.domain.AppUser;
import edu.usip.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest req) {
        AppUser u = userService.create(req.getName(), req.getPhone(), req.getRole());
        return ResponseEntity.ok(toResponse(u));
    }

    @PutMapping
    public ResponseEntity<UserResponse> update(@Valid @RequestBody UserRequest req) {
        AppUser u = userService.update(req.getPhone(), req.getName(), req.getRole());
        return ResponseEntity.ok(toResponse(u));
    }

    @DeleteMapping("/{phone}")
    public ResponseEntity<Void> disable(@PathVariable String phone) {
        userService.disable(phone);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> list() {
        return ResponseEntity.ok(
                userService.list().stream().map(this::toResponse).toList()
        );
    }

    private UserResponse toResponse(AppUser u) {
        return UserResponse.builder()
                .name(u.getName())
                .phone(u.getPhone())
                .role(u.getRole())
                .active(u.isActive())
                .build();
    }
}