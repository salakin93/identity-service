package edu.usip.identity.api.dto.request;

import edu.usip.identity.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserRequest {
    @NotBlank private String name;
    @NotBlank private String phone;
    @NotNull private Role role;
}