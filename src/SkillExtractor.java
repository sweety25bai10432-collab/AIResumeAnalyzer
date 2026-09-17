import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillExtractor {

    private final List<String> skillDatabase = Arrays.asList(
            "java",
            "python",
            "c++",
            "c",
            "sql",
            "mysql",
            "mongodb",
            "spring boot",
            "javascript",
            "html",
            "css",
            "react",
            "angular",
            "git",
            "docker",
            "aws",
            "machine learning",
            "deep learning",
            "nlp",
            "data structures",
            "rest api"
    );

    public List<String> extractSkills(String text) {

        List<String> foundSkills = new ArrayList<>();

        String lowerText = text.toLowerCase();

        for (String skill : skillDatabase) {

            if (lowerText.contains(skill.toLowerCase())) {
                foundSkills.add(skill);
            }
        }

        return foundSkills;
    }
}