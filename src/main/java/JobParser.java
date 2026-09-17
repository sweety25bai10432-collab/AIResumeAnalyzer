import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JobParser {

    public String extractJobDescription(
            String filePath) throws IOException {

        StringBuilder description =
                new StringBuilder();

        BufferedReader reader =
                new BufferedReader(
                        new FileReader(filePath)
                );

        String line;

        while ((line = reader.readLine()) != null) {

            description
                    .append(line)
                    .append(" ");
        }

        reader.close();

        return description.toString();
    }
}