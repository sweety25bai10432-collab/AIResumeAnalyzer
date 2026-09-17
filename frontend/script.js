function scrollToAnalyzer() {

    document
        .getElementById("analyzer")
        .scrollIntoView({
            behavior: "smooth"
        });
}


// =============================================
// SHOW SELECTED FILE NAME
// =============================================

function showFileName() {

    const fileInput =
        document.getElementById("resumeFile");

    const fileName =
        document.getElementById("fileName");

    if (fileInput.files.length > 0) {

        fileName.innerText =
            "Selected: " +
            fileInput.files[0].name;

    } else {

        fileName.innerText =
            "No file selected";
    }
}


// =============================================
// ANALYZE RESUME
// =============================================

async function analyzeResume() {

    const fileInput =
        document.getElementById("resumeFile");

    const jobDescription =
        document
            .getElementById("jobDescription")
            .value;

    const results =
        document.getElementById("results");

    const loading =
        document.getElementById("loading");

    const errorMessage =
        document.getElementById("errorMessage");


    // =========================================
    // HIDE OLD RESULTS
    // =========================================

    results.classList.add("hidden");

    errorMessage.classList.add("hidden");
    errorMessage.innerText = "";


    // =========================================
    // VALIDATION
    // =========================================

    if (fileInput.files.length === 0) {

        showError(
            "Please upload your resume PDF."
        );

        return;
    }


    if (!jobDescription.trim()) {

        showError(
            "Please enter the job description."
        );

        return;
    }


    const file =
        fileInput.files[0];


    // =========================================
    // CHECK FILE TYPE
    // =========================================

    if (
        !file.name
            .toLowerCase()
            .endsWith(".pdf")
    ) {

        showError(
            "Please upload a PDF file."
        );

        return;
    }


    // =========================================
    // SHOW LOADING
    // =========================================

    loading.classList.remove("hidden");


    try {

        // =====================================
        // READ PDF
        // =====================================

        const arrayBuffer =
            await file.arrayBuffer();


        // =====================================
        // CONVERT PDF TO BASE64
        // =====================================

        const bytes =
            new Uint8Array(arrayBuffer);

        let binary = "";

        const chunkSize = 8192;


        for (
            let i = 0;
            i < bytes.length;
            i += chunkSize
        ) {

            const chunk =
                bytes.subarray(
                    i,
                    Math.min(
                        i + chunkSize,
                        bytes.length
                    )
                );


            binary += String.fromCharCode(
                ...chunk
            );
        }


        const base64 =
            btoa(binary);


        // =====================================
        // CREATE REQUEST
        // =====================================

        const formData =
            new URLSearchParams();


        formData.append(
            "resume",
            base64
        );


        formData.append(
            "job",
            jobDescription
        );


        // =====================================
        // SEND REQUEST TO JAVA
        // =====================================

        const response =
            await fetch(
                "/analyze",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },

                    body:
                        formData.toString()
                }
            );


        // =====================================
        // CHECK SERVER RESPONSE
        // =====================================

        const data =
            await response.json();


        if (!response.ok) {

            throw new Error(
                data.error ||
                "Analysis failed."
            );
        }


        // =====================================
        // VALIDATE RESULT
        // =====================================

        if (
            typeof data.score === "undefined"
        ) {

            throw new Error(
                "Invalid response received from Java backend."
            );
        }


        // =====================================
        // DISPLAY REAL RESULTS
        // =====================================

        displayResults(data);


    } catch (error) {

        console.error(
            "Analysis error:",
            error
        );


        showError(
            "Analysis failed: " +
            error.message
        );


    } finally {

        loading.classList.add("hidden");
    }
}


// =============================================
// DISPLAY RESULTS
// =============================================

function displayResults(data) {

    const results =
        document.getElementById("results");


    // =========================================
    // SCORE
    // =========================================

    const score =
        Number(data.score).toFixed(2);


    document
        .getElementById("score")
        .innerText =
        score + "%";


    // =========================================
    // SCORE MESSAGE
    // =========================================

    const scoreMessage =
        document.getElementById(
            "scoreMessage"
        );


    if (Number(data.score) >= 80) {

        scoreMessage.innerText =
            "Your resume has a strong match with the job requirements.";

    } else if (Number(data.score) >= 50) {

        scoreMessage.innerText =
            "Your resume has a moderate match with the job requirements.";

    } else {

        scoreMessage.innerText =
            "Your resume has several skill gaps for this job.";
    }


    // =========================================
    // MATCHING SKILLS
    // =========================================

    displaySkills(
        "matchingSkills",
        data.matchingSkills
    );


    // =========================================
    // MISSING SKILLS
    // =========================================

    displaySkills(
        "missingSkills",
        data.missingSkills
    );


    // =========================================
    // RECOMMENDATIONS
    // =========================================

    const recommendations =
        document.getElementById(
            "recommendations"
        );


    recommendations.innerHTML = "";


    if (
        !data.missingSkills ||
        data.missingSkills.length === 0
    ) {

        const li =
            document.createElement("li");


        li.innerText =
            "Your resume covers all recognized required skills.";


        recommendations.appendChild(li);

    } else {

        data.missingSkills.forEach(
            skill => {

                const li =
                    document.createElement("li");


                li.innerText =
                    "Consider learning or highlighting " +
                    skill +
                    " in your resume.";


                recommendations.appendChild(li);
            }
        );
    }


    // =========================================
    // SHOW REAL RESULTS
    // =========================================

    results.classList.remove("hidden");


    // =========================================
    // SCROLL TO RESULTS
    // =========================================

    results.scrollIntoView({
        behavior: "smooth"
    });
}


// =============================================
// DISPLAY SKILLS
// =============================================

function displaySkills(
    elementId,
    skills
) {

    const container =
        document.getElementById(elementId);


    container.innerHTML = "";


    if (
        !skills ||
        skills.length === 0
    ) {

        const span =
            document.createElement("span");


        span.className = "skill";


        span.innerText =
            "None";


        container.appendChild(span);


        return;
    }


    skills.forEach(skill => {

        const span =
            document.createElement("span");


        span.className =
            "skill";


        span.innerText =
            skill;


        container.appendChild(span);
    });
}


// =============================================
// SHOW ERROR
// =============================================

function showError(message) {

    const errorMessage =
        document.getElementById(
            "errorMessage"
        );


    errorMessage.innerText =
        message;


    errorMessage.classList.remove(
        "hidden"
    );
}