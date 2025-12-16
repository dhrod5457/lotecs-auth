package lotecs.auth.domain.sso;

public interface SsoProvider {

    SsoAuthResult authenticate(SsoAuthRequest request);
}
