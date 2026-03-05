package edu.usip.identity.api.dto.response;

import edu.usip.identity.domain.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private String name;
    private String phone;
    private Role role;
    private boolean active;
}