package alchemy.logic;

import alchemy.data.ILexiconIdentityClient;
import alchemy.data.ILexiconIdentityClient.Identity;
import alchemy.logic.CallerIdentityService.Verdict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Who may act as a player in Alchemy: the gap was that anyone could, by naming an id. */
class CallerIdentityManagerTest {

    private static final Identity ALICE = new Identity(7, "alice", "USER", Set.of());

    private ILexiconIdentityClient lexicon;

    @BeforeEach
    void setUp() {
        lexicon = mock(ILexiconIdentityClient.class);
        when(lexicon.validate("alice-token")).thenReturn(ALICE);
        when(lexicon.validate("expired")).thenReturn(null);
    }

    @Test
    void aTokenForTheNamedPlayerIsAllowed() {
        assertEquals(Verdict.ALLOWED, new CallerIdentityManager(lexicon, true).check("alice-token", 7));
    }

    @Test
    void aValidTokenNamingSomeoneElseIsRefused() {
        assertEquals(Verdict.NOT_YOU, new CallerIdentityManager(lexicon, false).check("alice-token", 8));
    }

    @Test
    void anInvalidTokenIsRefusedEvenBeforeTokensAreRequired() {
        // Sending a bad token is never better than sending none: it is always checked.
        assertEquals(Verdict.SIGN_IN_REQUIRED, new CallerIdentityManager(lexicon, false).check("expired", 7));
    }

    @Test
    void noTokenIsStillServedUntilTheSwitchIsOn() {
        CallerIdentityManager lenient = new CallerIdentityManager(lexicon, false);
        assertEquals(Verdict.ALLOWED, lenient.check(null, 7));
        assertEquals(Verdict.ALLOWED, lenient.check("  ", 7));
        verify(lexicon, never()).validate(anyString());
    }

    @Test
    void noTokenIsRefusedOnceTheSwitchIsOn() {
        CallerIdentityManager strict = new CallerIdentityManager(lexicon, true);
        assertEquals(Verdict.SIGN_IN_REQUIRED, strict.check(null, 7));
        assertEquals(Verdict.SIGN_IN_REQUIRED, strict.check("", 7));
    }
}
