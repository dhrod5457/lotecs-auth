package lotecs.auth.application.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank
    private String tenantId;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    private String email;

    private String fullName;

    private List<String> roles;
}
