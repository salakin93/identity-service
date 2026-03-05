package edu.usip.identity.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;
import java.util.Base64;

@Configuration
public class PemKeyLoader {

    @Bean
    public RSAPrivateKey rsaPrivateKey(
            @Value("${security.jwt.private-key-path:}") String privatePath,
            @Value("${security.jwt.private-key-pem:}") String privatePem
    ) {
        String pem = resolvePem(privatePath, privatePem, "private");
        return (RSAPrivateKey) parsePrivateKey(pem);
    }

    @Bean
    public RSAPublicKey rsaPublicKey(
            @Value("${security.jwt.public-key-path:}") String publicPath,
            @Value("${security.jwt.public-key-pem:}") String publicPem
    ) {
        String pem = resolvePem(publicPath, publicPem, "public");
        return (RSAPublicKey) parsePublicKey(pem);
    }

    private String resolvePem(String path, String pem, String kind) {
        // 1) si hay path, intenta leer archivo
        if (path != null && !path.isBlank()) {
            try {
                Path p = Path.of(path).toAbsolutePath().normalize();
                if (!Files.exists(p)) {
                    throw new IllegalStateException("No existe el archivo " + kind + " key: " + p);
                }
                return Files.readString(p);
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo leer el archivo " + kind + " key", e);
            }
        }

        // 2) fallback: pem inline
        if (pem != null && !pem.isBlank()) return pem;

        throw new IllegalStateException("Falta configurar la " + kind + " key. Usa *-key-path o *-key-pem.");
    }

    private PrivateKey parsePrivateKey(String pem) {
        try {
            String clean = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(clean);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo parsear private key PEM", e);
        }
    }

    private PublicKey parsePublicKey(String pem) {
        try {
            String clean = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(clean);
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo parsear public key PEM", e);
        }
    }
}