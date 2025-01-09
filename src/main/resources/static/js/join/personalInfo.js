document.addEventListener("DOMContentLoaded", () => {
    initializePersonalInfoResources();
});

function initializePersonalInfoResources() {
    var form = document.getElementById("personalInfoForm");

    form.addEventListener("submit", (event) => {
        event.preventDefault();

        var formData = new FormData(form);
        var formObject = Object.fromEntries(formData.entries());

        fetch("/api/member/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(formObject),
        })
            .then(response => {
                if (response.ok) {
                    alert("회원가입이 완료되었습니다!");
                    window.location.href = "/";
                } else {
                    return response.json().then(data => {
                        if (data.fieldErrors) {
                            displayFieldErrors(data.fieldErrors);
                        }
                        throw new Error(data.message || "회원가입 중 오류 발생");
                    });
                }
            })
            .catch(error => {
                console.error("회원가입 중 오류:", error);
            });
    });

    function displayFieldErrors(errors) {
        // 기존 에러 메시지 초기화
        document.querySelectorAll(".error-message").forEach(element => {
            element.textContent = "";
        });

        errors.forEach(function (error) {
            alert("error" + error);
            alert("error.field" + error.field);
            var field = error.field;
            var reason = error.reason;

            // 해당 필드의 에러 메시지 요소 찾기
            var errorElement = document.getElementById(field + 'Error');
            if (errorElement) {
                errorElement.textContent = reason; // 에러 메시지 설정
                errorElement.style.display = 'block'; // 에러 메시지 표시
            }
        });
    }
}