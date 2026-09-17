import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SkillExtractor {

    private final List<String> skillDatabase = Arrays.asList(
            "java",
            "python",
            "c++",
            "c#",
            "sql",
            "mysql",
            "mongodb",
            "spring boot",
            "javascript",
            "typescript",
            "html",
            "css",
            "react",
            "angular",
            "node.js",
            "node",
            "express.js",
            "express",
            "git",
            "github",
            "docker",
            "aws",
            "azure",
            "machine learning",
            "deep learning",
            "artificial intelligence",
            "nlp",
            "natural language processing",
            "tensorflow",
            "pytorch",
            "scikit-learn",
            "data structures",
            "algorithms",
            "data analysis",
            "pandas",
            "numpy",
            "matplotlib",
            "rest api",
            "restful api",
            "spring",
            "hibernate",
            "oops",
            "object oriented programming",
            "operating systems",
            "computer networks",
            "dbms"
    );

    public List<String> extractSkills(String text) {

        List<String> foundSkills = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return foundSkills;
        }

        String lowerText = text
                .toLowerCase()
                .replaceAll("[\\r\\n]+", " ")
                .replaceAll("\\s+", " ")
                .trim();

        for (String skill : skillDatabase) {

            if (containsSkill(lowerText, skill)) {

                if (!foundSkills.contains(skill)) {
                    foundSkills.add(skill);
                }
            }
        }

        return foundSkills;
    }


    private boolean containsSkill(String text, String skill) {

        String lowerSkill = skill.toLowerCase();

        /*
         * Special cases containing symbols.
         */

        if (lowerSkill.equals("c++")) {
            return text.contains("c++");
        }

        if (lowerSkill.equals("c#")) {
            return text.contains("c#");
        }

        if (lowerSkill.equals("node.js")) {
            return text.contains("node.js") || text.contains("node js");
        }

        if (lowerSkill.equals("express.js")) {
            return text.contains("express.js") || text.contains("express js");
        }

        if (lowerSkill.equals("scikit-learn")) {
            return text.contains("scikit-learn")
                    || text.contains("scikit learn");
        }


        /*
         * Normal skills.
         *
         * \\b means word boundary.
         *
         * Example:
         * "java"  -> detected
         * "javascript" -> does NOT become Java
         */

        String pattern = "\\b"
                + Pattern.quote(lowerSkill)
                + "\\b";

        return Pattern.compile(pattern)
                .matcher(text)
                .find();
    }
}