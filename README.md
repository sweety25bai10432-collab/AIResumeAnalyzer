# 🤖 AI Resume Analyzer

An intelligent web-based **Resume Analyzer** built using **Java, PDFBox, HTML, CSS, and JavaScript** that analyzes a candidate's resume against a given job description.

The system extracts text and skills from a PDF resume, compares them with the skills required for a job, calculates a compatibility score, identifies missing skills, and provides recommendations for improvement.

---

## 🚀 Features

* 📄 **PDF Resume Upload**

  * Upload a resume directly through the web interface.
  * Resume text is extracted automatically using Apache PDFBox.

* 🔍 **Skill Extraction**

  * Detects technical and programming skills from resume content.
  * Supports skills such as Java, Python, SQL, React, Docker, AWS, Machine Learning, Git, and more.

* 💼 **Job Description Analysis**

  * Accepts a job description as input.
  * Extracts required skills from the provided description.

* 📊 **Resume Compatibility Score**

  * Compares resume skills with job-required skills.
  * Generates a percentage-based compatibility score.

* ✅ **Matching Skills**

  * Displays skills found in both the resume and job description.

* ❌ **Missing Skills**

  * Identifies skills required by the job that are not detected in the resume.

* 💡 **Recommendations**

  * Provides suggestions based on identified skill gaps.

* 🌐 **Web Interface**

  * Clean and responsive frontend built using HTML, CSS, and JavaScript.

* ⚡ **Java Backend**

  * Lightweight Java HTTP server handles resume analysis requests.

* 🐳 **Docker Deployment**

  * Includes Docker configuration for easy cloud deployment.

---

## 🛠️ Technologies Used

### Backend

* Java
* Java HTTP Server
* Apache PDFBox
* Maven

### Frontend

* HTML5
* CSS3
* JavaScript

### Deployment

* Docker
* GitHub
* Render

---

## 🏗️ Project Structure

```text
AIResumeAnalyzer/
│
├── frontend/
│   ├── index.html
│   ├── script.js
│   └── style.css
│
├── src/
│   └── main/
│       └── java/
│           ├── Main.java
│           ├── ApiServer.java
│           ├── AnalysisResult.java
│           ├── JobDescription.java
│           ├── JobParser.java
│           ├── Resume.java
│           ├── ResumeAnalyzer.java
│           ├── ResumeParser.java
│           └── SkillExtractor.java
│
├── job.txt
├── skills.txt
├── pom.xml
├── Dockerfile
└── README.md
```

---

## 🔄 How It Works

```text
                 ┌──────────────────┐
                 │   Upload Resume  │
                 │      (PDF)       │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │  Extract Resume  │
                 │      Text        │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │ Extract Resume   │
                 │     Skills       │
                 └────────┬─────────┘
                          │
                          │
Job Description ──────────┤
                          ▼
                 ┌──────────────────┐
                 │ Extract Required │
                 │     Skills       │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │ Compare Skills   │
                 └────────┬─────────┘
                          │
                 ┌────────┴─────────┐
                 ▼                  ▼
        ┌─────────────────┐  ┌─────────────────┐
        │ Matching Skills │  │  Missing Skills │
        └────────┬────────┘  └────────┬────────┘
                 │                    │
                 └─────────┬──────────┘
                           ▼
                 ┌──────────────────┐
                 │ Compatibility    │
                 │      Score       │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │  Recommendations │
                 └──────────────────┘
```

---

## 🧠 Analysis Process

### 1. Resume Upload

The user uploads a PDF resume through the web interface.

### 2. Text Extraction

Apache PDFBox extracts readable text from the uploaded PDF.

### 3. Skill Extraction

The extracted resume text is processed and compared against a predefined skill database.

The system recognizes skills such as:

```text
Java
Python
C++
C#
SQL
MySQL
MongoDB
JavaScript
HTML
CSS
React
Angular
Node.js
Git
GitHub
Docker
AWS
Machine Learning
Deep Learning
NLP
TensorFlow
PyTorch
Data Structures
Algorithms
Pandas
NumPy
Matplotlib
REST API
Spring
Hibernate
OOPS
Operating Systems
Computer Networks
DBMS
```

### 4. Job Description Processing

The job description is analyzed using the same skill extraction mechanism.

### 5. Skill Comparison

The system compares:

```text
Resume Skills
       ↓
Required Job Skills
       ↓
Matching + Missing Skills
```

### 6. Compatibility Score

The score is calculated based on the percentage of required skills detected in the resume.

```text
Compatibility Score =
(Matching Required Skills / Total Required Skills) × 100
```

### 7. Recommendations

The missing skills are presented to the user so they can identify areas that may need improvement.

---

## 💻 Running the Project Locally

### Prerequisites

Make sure you have:

* Java 17 or later
* Maven
* Git

### Clone the Repository

```bash
git clone https://github.com/sweety25bai10432-collab/AIResumeAnalyzer.git
```

Navigate into the project:

```bash
cd AIResumeAnalyzer
```

### Build the Project

```bash
mvn clean package
```

The JAR will be generated inside:

```text
target/AIResumeAnalyzer-1.0.jar
```

### Run the Application

```bash
java -jar target/AIResumeAnalyzer-1.0.jar
```

The server runs on port `8080` by default.

Open your browser and visit:

```text
http://localhost:8080
```

---

## 🐳 Running with Docker

Build the Docker image:

```bash
docker build -t ai-resume-analyzer .
```

Run the container:

```bash
docker run -p 8080:8080 ai-resume-analyzer
```

Then open:

```text
http://localhost:8080
```

---

## ☁️ Deployment

The project includes a `Dockerfile`, making it suitable for deployment on platforms that support Docker containers.

The application is configured to use the environment-provided `PORT` when deployed to a cloud platform.

### Deployment Flow

```text
GitHub Repository
       ↓
Docker Build
       ↓
Maven Build
       ↓
Java JAR
       ↓
Java HTTP Server
       ↓
Web Application
```

---

## 📊 Example Output

After analyzing a resume, the application provides:

```text
Compatibility Score: 82%

Matching Skills:
✓ Java
✓ SQL
✓ Git
✓ Python

Missing Skills:
✗ Docker
✗ AWS
✗ React

Recommendations:
• Consider improving your knowledge of Docker.
• Add relevant AWS experience or projects.
• Consider building projects using React.
```

---

## 🎯 Use Cases

The project can be useful for:

* Students preparing resumes
* Freshers applying for internships
* Job seekers comparing resumes with job descriptions
* Identifying technical skill gaps
* Resume improvement
* Career preparation
* Educational demonstrations of text processing and web applications

---

## 🔮 Future Improvements

Possible future enhancements include:

* 🤖 Machine-learning-based resume classification
* 🧠 NLP-based semantic skill matching
* 📑 Support for DOCX resumes
* 📈 Detailed resume scoring
* 🎯 Job-role recommendations
* 🔗 LinkedIn/GitHub profile integration
* 🗂️ Multiple job description comparison
* 📊 Advanced analytics dashboard
* ☁️ Database integration
* 🔐 User authentication
* 📥 Downloadable analysis reports
* 🌍 Support for multiple languages

---

## 📌 Current Architecture

```text
Frontend
HTML + CSS + JavaScript
        │
        │ HTTP POST
        ▼
Java ApiServer
        │
        ├── ResumeParser
        │       ↓
        │   Apache PDFBox
        │
        ├── SkillExtractor
        │
        └── ResumeAnalyzer
                ↓
        AnalysisResult
                ↓
          JSON Response
                ↓
             Frontend
```

---

## 👩‍💻 Developer

**Sweety Kumari**

--
