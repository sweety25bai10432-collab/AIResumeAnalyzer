import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiServer {

    public static void main(String[] args) throws Exception {

        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8080")
        );

        HttpServer server = HttpServer.create(
                new InetSocketAddress("0.0.0.0", port),
                0
        );

        server.createContext("/analyze", ApiServer::analyzeResume);
        server.createContext("/", ApiServer::serveFrontend);

        server.setExecutor(null);

        System.out.println("======================================");
        System.out.println("      AI RESUME ANALYZER STARTED");
        System.out.println("======================================");
        System.out.println("Server running on port: " + port);
        System.out.println("======================================");

        server.start();
    }


    // =========================================================
    // SERVE FRONTEND
    // =========================================================

    private static void serveFrontend(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

            sendTextResponse(
                    exchange,
                    "Only GET requests are allowed.",
                    "text/plain",
                    405
            );

            return;
        }

        String requestPath =
                exchange.getRequestURI().getPath();

        if (requestPath.equals("/") ||
                requestPath.equals("/index.html")) {

            serveFile(
                    exchange,
                    "frontend/index.html",
                    "text/html"
            );

            return;
        }

        if (requestPath.equals("/style.css")) {

            serveFile(
                    exchange,
                    "frontend/style.css",
                    "text/css"
            );

            return;
        }

        if (requestPath.equals("/script.js")) {

            serveFile(
                    exchange,
                    "frontend/script.js",
                    "application/javascript"
            );

            return;
        }

        sendTextResponse(
                exchange,
                "404 - File Not Found",
                "text/plain",
                404
        );
    }


    // =========================================================
    // SERVE FILE
    // =========================================================

    private static void serveFile(
            HttpExchange exchange,
            String filePath,
            String contentType)
            throws IOException {

        Path path = Path.of(filePath);

        if (!Files.exists(path)) {

            sendTextResponse(
                    exchange,
                    "File not found: " + filePath,
                    "text/plain",
                    404
            );

            return;
        }

        byte[] fileBytes =
                Files.readAllBytes(path);

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        contentType + "; charset=UTF-8"
                );

        exchange.sendResponseHeaders(
                200,
                fileBytes.length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(fileBytes);
        output.close();
    }


    // =========================================================
    // ANALYZE RESUME
    // =========================================================

    private static void analyzeResume(
            HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin",
                "*"
        );

        exchange.getResponseHeaders().add(
                "Access-Control-Allow-Methods",
                "POST, OPTIONS"
        );

        exchange.getResponseHeaders().add(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );


        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(
                    204,
                    -1
            );

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

            // =================================================
            // READ REQUEST
            // =================================================

            InputStream inputStream =
                    exchange.getRequestBody();

            String requestBody =
                    new String(
                            inputStream.readAllBytes(),
                            StandardCharsets.UTF_8
                    );


            // =================================================
            // PARSE FORM DATA
            // =================================================

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


            // =================================================
            // DECODE PDF
            // =================================================

            byte[] pdfBytes =
                    Base64.getDecoder()
                            .decode(resumeBase64);


            // =================================================
            // CREATE TEMP PDF
            // =================================================

            Path tempFile =
                    Files.createTempFile(
                            "uploaded_resume_",
                            ".pdf"
                    );

            Files.write(
                    tempFile,
                    pdfBytes
            );


            // =================================================
            // EXTRACT RESUME TEXT
            // =================================================

            ResumeParser resumeParser =
                    new ResumeParser();

            String resumeText =
                    resumeParser.extractText(
                            tempFile.toString()
                    );


            Files.deleteIfExists(tempFile);


            // =================================================
            // DEBUG: RESUME TEXT
            // =================================================

            System.out.println();
            System.out.println("======================================");
            System.out.println("        RESUME ANALYSIS DEBUG");
            System.out.println("======================================");

            System.out.println(
                    "Resume text length: " +
                            resumeText.length()
            );

            System.out.println(
                    "Resume text preview:"
            );

            System.out.println(
                    resumeText.substring(
                            0,
                            Math.min(
                                    resumeText.length(),
                                    1000
                            )
                    )
            );


            // =================================================
            // EXTRACT SKILLS
            // =================================================

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


            // =================================================
            // DEBUG: SKILLS
            // =================================================

            System.out.println();
            System.out.println(
                    "Resume Skills: " +
                            resumeSkills
            );

            System.out.println(
                    "Required Skills: " +
                            requiredSkills
            );

            System.out.println(
                    "======================================"
            );


            // =================================================
            // ANALYZE
            // =================================================

            ResumeAnalyzer analyzer =
                    new ResumeAnalyzer();


            AnalysisResult result =
                    analyzer.analyze(
                            resumeSkills,
                            requiredSkills
                    );


            // =================================================
            // CREATE JSON
            // =================================================

            String json =
                    createJsonResponse(result);


            System.out.println(
                    "Final Result: " + json
            );

            System.out.println(
                    "======================================"
            );


            sendResponse(
                    exchange,
                    json,
                    200
            );


        } catch (Exception e) {

            e.printStackTrace();

            String errorMessage =
                    e.getMessage();

            if (errorMessage == null) {
                errorMessage =
                        "Unknown server error";
            }

            String error =
                    "{\"error\":\"" +
                            escapeJson(errorMessage) +
                            "\"}";


            sendResponse(
                    exchange,
                    error,
                    500
            );
        }
    }


    // =========================================================
    // FORM DATA PARSER
    // =========================================================

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

                data.put(
                        key,
                        value
                );
            }
        }

        return data;
    }


    // =========================================================
    // CREATE JSON RESPONSE
    // =========================================================

    private static String createJsonResponse(
            AnalysisResult result) {

        StringBuilder json =
                new StringBuilder();

        json.append("{");

        json.append("\"score\":")
                .append(
                        result.getCompatibilityScore()
                )
                .append(",");

        json.append("\"matchingSkills\":");

        json.append(
                listToJson(
                        result.getMatchingSkills()
                )
        );

        json.append(",");

        json.append("\"missingSkills\":");

        json.append(
                listToJson(
                        result.getMissingSkills()
                )
        );

        json.append("}");

        return json.toString();
    }


    // =========================================================
    // LIST TO JSON
    // =========================================================

    private static String listToJson(
            List<String> list) {

        StringBuilder json =
                new StringBuilder("[");

        for (int i = 0;
             i < list.size();
             i++) {

            json.append("\"")
                    .append(
                            escapeJson(
                                    list.get(i)
                            )
                    )
                    .append("\"");

            if (i < list.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        return json.toString();
    }


    // =========================================================
    // JSON ESCAPE
    // =========================================================

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


    // =========================================================
    // SEND JSON
    // =========================================================

    private static void sendResponse(
            HttpExchange exchange,
            String response,
            int statusCode)
            throws IOException {

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json; charset=UTF-8"
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


    // =========================================================
    // SEND TEXT
    // =========================================================

    private static void sendTextResponse(
            HttpExchange exchange,
            String response,
            String contentType,
            int statusCode)
            throws IOException {

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        contentType + "; charset=UTF-8"
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