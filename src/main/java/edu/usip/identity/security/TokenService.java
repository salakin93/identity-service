package edu.usip.identity.security;

import edu.usip.identity.domain.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final String issuer;
    private final long ttlSeconds;

    public TokenService(JwtEncoder encoder,
                        @Value("${security.jwt.issuer}") String issuer,
                        @Value("${security.jwt.ttl-seconds}") long ttlSeconds) {
        this.encoder = encoder;
        this.issuer = issuer;
        this.ttlSeconds = ttlSeconds;
    }

    public String issue(String phone, List<Role> roles) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .subject(phone)
                .claim("roles", roles.stream().map(Enum::name).toList())
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}