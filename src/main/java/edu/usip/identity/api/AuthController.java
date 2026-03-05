package edu.usip.identity.api;

import edu.usip.identity.api.dto.request.LoginRequest;
import edu.usip.identity.api.dto.response.LoginResponse;
import edu.usip.identity.security.TokenService;
import edu.usip.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        var user = userService.getActiveOrThrow(req.getPhone());
        String token = tokenService.issue(user.getPhone(), List.of(user.getRole()));
        return ResponseEntity.ok(LoginResponse.builder().token(token).build());
    }
}