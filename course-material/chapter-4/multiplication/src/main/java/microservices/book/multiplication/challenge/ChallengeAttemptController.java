package microservices.book.multiplication.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * This class provides a REST API to POST the attempts from users.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/attempts")
class ChallengeAttemptController {

    private final ChallengeService challengeService;

    @PostMapping
    ResponseEntity<ChallengeAttempt> postResult(
            @RequestBody @Valid ChallengeAttemptDTO challengeAttemptDTO) {
        return ResponseEntity.ok(challengeService.verifyAttempt(challengeAttemptDTO));
    }
}