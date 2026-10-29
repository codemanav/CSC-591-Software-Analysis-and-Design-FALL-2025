import lombok.Data;

@Data
public class ChallengeAttemptDTO {
    private int factorA;
    private int factorB;
    private int guess;
    private String userAlias;
}
