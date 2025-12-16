package lotecs.auth.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long userId;
    private String tenantId;
    private String username;
    private String email;
    private String fullName;
    private String status;
    private List<String> roles;
    private String externalUserId;
    private String externalSystem;
}
