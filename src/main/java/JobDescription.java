import java.util.List;

public class JobDescription {

    private String description;
    private List<String> requiredSkills;

    public JobDescription(String description, List<String> requiredSkills) {
        this.description = description;
        this.requiredSkills = requiredSkills;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }
}