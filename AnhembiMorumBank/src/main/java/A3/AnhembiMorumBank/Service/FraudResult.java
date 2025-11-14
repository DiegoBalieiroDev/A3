package A3.AnhembiMorumBank.Service;

import java.util.List;

public class FraudResult {
    public enum Action { APPROVE, DENY, PENDING_REVIEW }

    private final int score;
    private final List<String> reasons;
    private final Action action;

    public FraudResult(int score, List<String> reasons, Action action) {
        this.score = score;
        this.reasons = reasons;
        this.action = action;
    }

    public int getScore() { return score; }
    public List<String> getReasons() { return reasons; }
    public Action getAction() { return action; }


}