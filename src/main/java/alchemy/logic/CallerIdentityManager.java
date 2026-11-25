package alchemy.logic;

import alchemy.data.ILexiconIdentityClient;
import alchemy.data.ILexiconIdentityClient.Identity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * See {@link CallerIdentityService}.
 *
 * <p><b>Why a token is not required yet.</b> Every Android build already installed calls
 * these endpoints without one, and the app's "critical update" flag does not stop anyone
 * using an old build — so requiring it now would break Alchemy for those players until
 * they happened to update. Until {@code ALCHEMY_REQUIRE_SSO=true}:
 * <ul>
 *   <li>a request <em>with</em> a token is fully checked, and refused if it names
 *       someone else or the token is not valid;</li>
 *   <li>a request <em>without</em> one is still served, as before.</li>
 * </ul>
 * The website and current app always send a token, so turning the switch on once older
 * installs have updated closes the gap completely.
 */
@Service
public class CallerIdentityManager implements CallerIdentityService {

    private final ILexiconIdentityClient identity;
    private final boolean requireToken;

    public CallerIdentityManager(ILexiconIdentityClient identity,
                                 @Value("${ALCHEMY_REQUIRE_SSO:false}") boolean requireToken) {
        this.identity = identity;
        this.requireToken = requireToken;
    }

    @Override
    public Verdict check(String ssoToken, int claimedPlayerId) {
        if (ssoToken == null || ssoToken.isBlank()) {
            return requireToken ? Verdict.SIGN_IN_REQUIRED : Verdict.ALLOWED;
        }
        Identity caller = identity.validate(ssoToken);
        if (caller == null) {
            return Verdict.SIGN_IN_REQUIRED;
        }
        return caller.userId() == claimedPlayerId ? Verdict.ALLOWED : Verdict.NOT_YOU;
    }
}
