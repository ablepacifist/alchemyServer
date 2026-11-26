package alchemy.data;

import java.util.Set;

/**
 * Asks Lexicon who a caller is and what they may do.
 *
 * <p>Lexicon is the permission authority for the whole stack: the role lives on the shared
 * {@code PLAYERS} row, and Lexicon's logic layer decides which permissions a role grants.
 * Alchemy never keeps its own list; it trades the caller's single-use SSO token for an
 * identity and checks the permission names that come back.
 */
public interface ILexiconIdentityClient {

    /** Who a validated token belongs to. {@code permissions} holds Lexicon's permission names. */
    record Identity(int userId, String username, String role, Set<String> permissions) {
        public boolean has(String permission) {
            return permissions != null && permissions.contains(permission);
        }
    }

    /**
     * Validate and consume a Lexicon SSO token.
     *
     * @return the identity, or {@code null} when the token is missing, expired, already used,
     *         or Lexicon could not be reached
     */
    Identity validate(String ssoToken);
}
