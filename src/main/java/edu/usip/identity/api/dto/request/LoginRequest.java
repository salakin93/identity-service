package edu.usip.identity.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest {
    @NotBlank
    @Pattern(regexp = "^[0-9]{8}$", message = "El teléfono debe tener 8 dígitos")
    private String phone;
}