function initializeMailVerification() {
    var verificationButton = document.getElementById("verificationButton");
    var verificationCodeContainer = document.getElementById("verificationCodeContainer");
    var nextButton = document.getElementById("nextButton");
    var loginIdInput = document.getElementById("loginId");
    var verificationCodeInput = document.getElementById("verificationCode");
    var codeError = document.getElementById("codeError");

    // 이메일 입력 시 유효성 검사
    loginIdInput.addEventListener("input", validateEmail);

    function validateEmail() {
        var loginId = loginIdInput.value.trim();
        var emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

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
        var loginId = loginIdInput.value.trim();
        var emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

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
                // verificationButton.innerText = "인증 요청 완료";
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

        var loginId = loginIdInput.value.trim();
        var verificationCode = verificationCodeInput.value.trim();

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
                    codeError.style.display = "none"; // 에러 메시지 숨기기
                    loadPersonalInfoPage(loginId); // 개인정보 입력 페이지 로드
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
            var fieldError = errorData.fieldErrors.find((e) => e.field === "verificationCode");
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
}

function loadPersonalInfoPage(loginId) {
    fetch(`/member/email-next?loginId=${encodeURIComponent(loginId)}`, {
        method: "GET",
        headers: {
            "Content-Type": "text/html",
        },
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("서버 응답이 올바르지 않습니다.");
            }
            return response.text();
        })
        .then(html => {
            document.querySelector("main").innerHTML = html;
            loadPersonalInfoResources(); // 개인정보 입력 리소스 로드
        })
        .catch(error => {
            console.error("개인정보 입력 페이지 전환 중 오류 발생:", error);
        });
}

function loadPersonalInfoResources() {
    var cssLink = document.createElement("link");
    cssLink.rel = "stylesheet";
    cssLink.href = "/css/join/personalInfo.css";
    document.head.appendChild(cssLink);

    var script = document.createElement("script");
    script.src = "/js/join/personalInfo.js";
    script.defer = true;
    script.onload = () => {
        initializePersonalInfoResources(); // 개인정보 입력 초기화 함수 호출
    };
    document.body.appendChild(script);
}