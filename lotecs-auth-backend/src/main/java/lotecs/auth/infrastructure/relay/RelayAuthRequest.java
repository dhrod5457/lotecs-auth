package lotecs.auth.infrastructure.relay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelayAuthRequest {

    private String tenantId;
    private String username;
    private String password;
}
