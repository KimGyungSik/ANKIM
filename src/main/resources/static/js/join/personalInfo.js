document.addEventListener("DOMContentLoaded", () => {
    initializePersonalInfoResources();
});

async function initializePersonalInfoResources() {
    var form = document.getElementById("personalInfoForm");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        var formData = new FormData(form);
        var formObject = Object.fromEntries(formData.entries());

        try {
            var response = await fetch("/api/member/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(formObject),
            });

            if (response.ok) {
                alert("회원가입이 완료되었습니다!");
                window.location.href = "/";
            } else {
                var data = await response.json();
                if (data.fieldErrors) {
                    displayFieldErrors(data.fieldErrors);
                }
                throw new Error(data.message || "회원가입 중 오류 발생");
            }
        } catch (error) {
            console.error("회원가입 중 오류:", error);
        }
    });

    async function displayFieldErrors(errors) {
        document.querySelectorAll(".error-message").forEach(element => {
            element.textContent = "";
        });

        errors.forEach(error => {
            var field = error.field;
            var reason = error.reason;

            var errorElement = document.getElementById(field + 'Error');
            if (errorElement) {
                errorElement.textContent = reason;
                errorElement.style.display = 'block';
            }
        });
    }
}