import java.util.List;

public class Resume {

    private String fileName;
    private String text;
    private List<String> skills;

    public Resume(String fileName, String text, List<String> skills) {
        this.fileName = fileName;
        this.text = text;
        this.skills = skills;
    }

    public String getFileName() {
        return fileName;
    }

    public String getText() {
        return text;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}