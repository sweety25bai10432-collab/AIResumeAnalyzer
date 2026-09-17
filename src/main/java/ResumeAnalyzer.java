import java.util.ArrayList;
import java.util.List;

public class ResumeAnalyzer {

    public AnalysisResult analyze(
            List<String> resumeSkills,
            List<String> requiredSkills) {

        List<String> matchingSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String required : requiredSkills) {

            if (containsIgnoreCase(resumeSkills, required)) {
                matchingSkills.add(required);
            } else {
                missingSkills.add(required);
            }
        }

        double score = calculateScore(
                matchingSkills.size(),
                requiredSkills.size()
        );

        return new AnalysisResult(
                score,
                matchingSkills,
                missingSkills
        );
    }

    private boolean containsIgnoreCase(
            List<String> skills,
            String target) {

        for (String skill : skills) {

            if (skill.equalsIgnoreCase(target)) {
                return true;
            }
        }

        return false;
    }

    private double calculateScore(
            int matching,
            int totalRequired) {

        if (totalRequired == 0) {
            return 0;
        }

        return ((double) matching / totalRequired) * 100;
    }
}