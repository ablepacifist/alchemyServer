package alchemy.api;

import alchemy.logic.CallerIdentityService.Verdict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Turns a {@link Verdict} into the response a refused caller gets; null when allowed. */
final class CallerCheck {

    private CallerCheck() {
    }

    static ResponseEntity<String> refusal(Verdict verdict) {
        return switch (verdict) {
            case ALLOWED -> null;
            case SIGN_IN_REQUIRED -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Your session has expired. Please sign in again.");
            case NOT_YOU -> ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only act as yourself.");
        };
    }
}
