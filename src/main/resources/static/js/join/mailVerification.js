document.addEventListener("DOMContentLoaded", () => {
    const verificationButton = document.getElementById("verificationButton");
    const verificationCodeContainer = document.getElementById("verificationCodeContainer");
    const nextButton = document.getElementById("nextButton");
    const loginIdInput = document.getElementById("loginId");
    const verificationCodeInput = document.getElementById("verificationCode");
    const codeError = document.getElementById("codeError");

    // 이메일 입력 시 유효성 검사
    loginIdInput.addEventListener("input", validateEmail);

    function validateEmail() {
        const loginId = loginIdInput.value.trim();
        const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        if (emailPattern.test(loginId)) {
            verificationButton.disabled = false;
            verificationButton.classList.add("active");
        } else {
            verificationButton.disabled = true;
            verificationButton.classList.remove("active");
        }
    }

    // 이메일 인증 요청 버튼 클릭 시
    verificationButton.addEventListener("click", function () {
        const loginId = loginIdInput.value.trim();
        const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        if (!emailPattern.test(loginId)) {
            codeError.textContent = "유효한 이메일 주소를 입력해주세요.";
            codeError.style.display = "block";
            return;
        }

        fetch("/api/mail/send", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({ loginId }).toString()
        })
            .then((response) => handleResponse(response))
            .then((data) => {
                // 인증번호 입력 필드 및 다음 버튼 표시
                verificationCodeContainer.style.display = "block";
                nextButton.style.display = "block";

                verificationButton.disabled = true;
                verificationButton.innerText = "인증 요청 완료";
                codeError.style.display = "none"; // 에러 메시지 숨기기
            })
            .catch((error) => {
                codeError.textContent = error.message;
                codeError.style.display = "block";
            });
    });

    // 인증번호 검증 요청
    nextButton.addEventListener("click", function (event) {
        event.preventDefault(); // 기본 동작 방지

        const loginId = loginIdInput.value.trim();
        const verificationCode = verificationCodeInput.value.trim();

        if (!loginId || !verificationCode) {
            codeError.textContent = "이메일과 인증번호를 모두 입력해주세요.";
            codeError.style.display = "block";
            return;
        }

        fetch("/api/mail/verify", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ loginId, verificationCode })
        })
            .then((response) => handleResponse(response))
            .then((data) => {
                if (data.message === "SUCCESS") {
                    alert("인증 성공! 다음 단계로 진행합니다.");
                    codeError.style.display = "none"; // 에러 메시지 숨기기
                    // window.location.href = "/next-step"; // 다음 페이지 경로
                }
                else if (data.message === "RETRY") {
                    codeError.textContent = "인증번호를 3번 이상 틀리셨습니다. 다시 요청해주세요.";
                    codeError.style.display = "block";
                }
                else {
                    codeError.textContent = "인증번호가 잘못되었습니다. 다시 시도해주세요.";
                    codeError.style.display = "block";
                }
            })
            .catch((error) => {
                handleErrors(error);
            });
    });

    function handleResponse(response) {
        return response.json().then((data) => {
            if (!response.ok) {
                throw data; // 에러 데이터 객체 상태로 throw
            }
            return data;
        });
    }

    // 에러 메시지 표시 함수
    function handleErrors(errorData) {
        if (errorData && errorData.fieldErrors) {
            // 필드 에러 처리
            const fieldError = errorData.fieldErrors.find((e) => e.field === "verificationCode");
            if (fieldError) {
                codeError.textContent = fieldError.reason; // 서버에서 전달받은 reason 사용
                codeError.style.display = "block";
            }
        } else if (errorData && errorData.message) {
            // 일반 에러 메시지 처리
            codeError.textContent = errorData.message;
            codeError.style.display = "block";
        } else {
            // 알 수 없는 에러 메시지 처리
            codeError.textContent = "알 수 없는 오류가 발생했습니다. 다시 시도해주세요.";
            codeError.style.display = "block";
        }
    }
});