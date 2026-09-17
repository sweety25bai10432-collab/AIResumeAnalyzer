function scrollToAnalyzer() {

    document
        .getElementById("analyzer")
        .scrollIntoView({
            behavior: "smooth"
        });
}


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


async function analyzeResume() {

    const fileInput =
        document.getElementById("resumeFile");


    const jobDescription =
        document
            .getElementById("jobDescription")
            .value;


    // =========================================
    // VALIDATION
    // =========================================

    if (fileInput.files.length === 0) {

        alert(
            "Please upload your resume PDF."
        );

        return;
    }


    if (!jobDescription.trim()) {

        alert(
            "Please enter the job description."
        );

        return;
    }


    const file =
        fileInput.files[0];


    // =========================================
    // SHOW LOADING
    // =========================================

    document
        .getElementById("loading")
        .classList
        .remove("hidden");


    document
        .getElementById("results")
        .classList
        .add("hidden");


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
        // SEND TO JAVA BACKEND
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

                    body: formData.toString()
                }
            );


        const data =
            await response.json();


        // =====================================
        // ERROR FROM JAVA
        // =====================================

        if (!response.ok) {

            throw new Error(
                data.error ||
                "Analysis failed."
            );
        }


        // =====================================
        // DISPLAY REAL RESULTS
        // =====================================

        displayResults(data);


    } catch (error) {

        console.error(error);


        alert(
            "Could not connect to the Java backend.\n\n" +
            error.message
        );


    } finally {

        document
            .getElementById("loading")
            .classList
            .add("hidden");
    }
}


// =============================================
// DISPLAY RESULTS
// =============================================

function displayResults(data) {

    // Score

    const score =
        Number(data.score).toFixed(2);


    document
        .getElementById("score")
        .innerText =
        score + "%";


    // Matching skills

    displaySkills(
        "matchingSkills",
        data.matchingSkills
    );


    // Missing skills

    displaySkills(
        "missingSkills",
        data.missingSkills
    );


    // Recommendations

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


    // Show result section

    document
        .getElementById("results")
        .classList
        .remove("hidden");


    // Scroll to result

    document
        .getElementById("results")
        .scrollIntoView({
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