import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64;

public class ApiServer {

    public static void main(String[] args) throws Exception {

        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8080")
        );

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress("0.0.0.0", port),
                        0
                );

        server.createContext("/analyze", ApiServer::analyzeResume);

        server.setExecutor(null);

        System.out.println("======================================");
        System.out.println("   AI RESUME ANALYZER API STARTED");
        System.out.println("======================================");
        System.out.println("Server running at:");
        System.out.println("http://localhost:8080");
        System.out.println("Waiting for resume...");
        System.out.println("======================================");

        server.start();
    }


    private static void analyzeResume(HttpExchange exchange)
            throws IOException {

        // Allow frontend to communicate with backend
        exchange.getResponseHeaders()
                .add("Access-Control-Allow-Origin", "*");

        exchange.getResponseHeaders()
                .add("Access-Control-Allow-Methods",
                        "POST, OPTIONS");

        exchange.getResponseHeaders()
                .add("Access-Control-Allow-Headers",
                        "Content-Type");


        // Handle browser CORS request
        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }


        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "{\"error\":\"Only POST requests are allowed.\"}",
                    405
            );

            return;
        }


        try {

            // =========================================
            // READ REQUEST
            // =========================================

            InputStream inputStream =
                    exchange.getRequestBody();

            String requestBody =
                    new String(
                            inputStream.readAllBytes(),
                            StandardCharsets.UTF_8
                    );


            // =========================================
            // PARSE FORM DATA
            // =========================================

            Map<String, String> data =
                    parseFormData(requestBody);


            String resumeBase64 =
                    data.get("resume");

            String jobDescription =
                    data.get("job");


            if (resumeBase64 == null ||
                    resumeBase64.isEmpty()) {

                sendResponse(
                        exchange,
                        "{\"error\":\"Resume was not provided.\"}",
                        400
                );

                return;
            }


            if (jobDescription == null ||
                    jobDescription.trim().isEmpty()) {

                sendResponse(
                        exchange,
                        "{\"error\":\"Job description was not provided.\"}",
                        400
                );

                return;
            }


            // =========================================
            // DECODE PDF
            // =========================================

            byte[] pdfBytes =
                    Base64.getDecoder()
                            .decode(resumeBase64);


            // =========================================
            // CREATE TEMP PDF
            // =========================================

            Path tempFile =
                    Files.createTempFile(
                            "uploaded_resume_",
                            ".pdf"
                    );

            Files.write(
                    tempFile,
                    pdfBytes
            );


            // =========================================
            // EXTRACT RESUME TEXT
            // =========================================

            ResumeParser resumeParser =
                    new ResumeParser();

            String resumeText =
                    resumeParser.extractText(
                            tempFile.toString()
                    );


            // Delete temporary PDF
            Files.deleteIfExists(tempFile);


            // =========================================
            // EXTRACT SKILLS
            // =========================================

            SkillExtractor extractor =
                    new SkillExtractor();


            List<String> resumeSkills =
                    extractor.extractSkills(
                            resumeText
                    );


            List<String> requiredSkills =
                    extractor.extractSkills(
                            jobDescription
                    );


            // =========================================
            // ANALYZE
            // =========================================

            ResumeAnalyzer analyzer =
                    new ResumeAnalyzer();


            AnalysisResult result =
                    analyzer.analyze(
                            resumeSkills,
                            requiredSkills
                    );


            // =========================================
            // CREATE JSON RESPONSE
            // =========================================

            String json =
                    createJsonResponse(result);


            sendResponse(
                    exchange,
                    json,
                    200
            );


        } catch (Exception e) {

            e.printStackTrace();

            String error =
                    "{\"error\":\"" +
                            escapeJson(e.getMessage()) +
                            "\"}";

            sendResponse(
                    exchange,
                    error,
                    500
            );
        }
    }


    // =============================================
    // FORM DATA PARSER
    // =============================================

    private static Map<String, String> parseFormData(
            String body) {

        Map<String, String> data =
                new HashMap<>();


        String[] pairs =
                body.split("&");


        for (String pair : pairs) {

            String[] parts =
                    pair.split("=", 2);


            if (parts.length == 2) {

                String key =
                        URLDecoder.decode(
                                parts[0],
                                StandardCharsets.UTF_8
                        );

                String value =
                        URLDecoder.decode(
                                parts[1],
                                StandardCharsets.UTF_8
                        );

                data.put(key, value);
            }
        }

        return data;
    }


    // =============================================
    // CREATE JSON
    // =============================================

    private static String createJsonResponse(
            AnalysisResult result) {

        StringBuilder json =
                new StringBuilder();


        json.append("{");


        json.append("\"score\":")
                .append(result.getCompatibilityScore())
                .append(",");


        json.append("\"matchingSkills\":");

        json.append(listToJson(
                result.getMatchingSkills()
        ));

        json.append(",");


        json.append("\"missingSkills\":");

        json.append(listToJson(
                result.getMissingSkills()
        ));


        json.append("}");


        return json.toString();
    }


    // =============================================
    // LIST → JSON ARRAY
    // =============================================

    private static String listToJson(
            List<String> list) {

        StringBuilder json =
                new StringBuilder("[");


        for (int i = 0; i < list.size(); i++) {

            json.append("\"")
                    .append(
                            escapeJson(list.get(i))
                    )
                    .append("\"");


            if (i < list.size() - 1) {

                json.append(",");
            }
        }


        json.append("]");


        return json.toString();
    }


    // =============================================
    // JSON ESCAPE
    // =============================================

    private static String escapeJson(
            String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }


    // =============================================
    // SEND RESPONSE
    // =============================================

    private static void sendResponse(
            HttpExchange exchange,
            String response,
            int statusCode)
            throws IOException {

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json"
                );


        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );


        OutputStream output =
                exchange.getResponseBody();


        output.write(bytes);

        output.close();
    }
}