package alchemy.logic;

/**
 * Decides whether the caller of an Alchemy endpoint really is the player it names.
 *
 * <p>Alchemy's endpoints take a {@code playerId} in the path or body and used to trust it,
 * so anyone could forage, brew or eat as anyone. The caller proves who they are the same
 * way Holdfast already does: a single-use Lexicon SSO token in the {@code X-Lexicon-Sso}
 * header, traded with Lexicon for the signed-in player's id.
 */
public interface CallerIdentityService {

    /** Header carrying the caller's single-use Lexicon SSO token. */
    String SSO_HEADER = "X-Lexicon-Sso";

    enum Verdict {
        /** The token belongs to the named player, or no token was sent and none is required yet. */
        ALLOWED,
        /** A token was required or sent, and Lexicon did not accept it. */
        SIGN_IN_REQUIRED,
        /** A valid token for a different player than the one named. */
        NOT_YOU
    }

    Verdict check(String ssoToken, int claimedPlayerId);
}
