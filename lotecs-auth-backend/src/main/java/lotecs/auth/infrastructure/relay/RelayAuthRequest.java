package lotecs.auth.infrastructure.relay;

public record RelayAuthRequest(
    String tenantId,
    String username,
    String password
) {
}
