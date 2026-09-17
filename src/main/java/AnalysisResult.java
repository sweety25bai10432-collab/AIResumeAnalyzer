import java.util.List;

public class AnalysisResult {

    private double compatibilityScore;
    private List<String> matchingSkills;
    private List<String> missingSkills;

    public AnalysisResult(
            double compatibilityScore,
            List<String> matchingSkills,
            List<String> missingSkills) {

        this.compatibilityScore = compatibilityScore;
        this.matchingSkills = matchingSkills;
        this.missingSkills = missingSkills;
    }

    public double getCompatibilityScore() {
        return compatibilityScore;
    }

    public List<String> getMatchingSkills() {
        return matchingSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }
}