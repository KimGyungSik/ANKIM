async function sendLoginRequest(event) {
    // 기본 폼 동작 중지
    event.preventDefault();

    // 폼 데이터 가져오기
    var loginId = document.getElementById('loginId').value;
    var password = document.getElementById('password').value;

    // JSON 데이터 생성
    var requestData = {
        loginId: loginId,
        password: password
    };

    try {
        // Fetch API로 POST 요청 보내기
        var response = await fetch('/api/login/member', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        });

        // 서버 응답 처리
        if (response.ok) {
            var accessToken = response.headers.get("access"); // Access Token 가져오기
            if (accessToken) {
                localStorage.setItem("access", accessToken); // localStorage에 저장
                window.location.href = "/"; // 성공 시 루트 경로로 이동
            }
        } else {
            var errorData = await response.json();
            handleErrors(errorData);
        }
    } catch (error) {
        alert('서버와의 연결에 실패했습니다.');
    }
}

function handleErrors(errorData) {
    // 기존 에러 메시지 초기화
    document.querySelectorAll('.error-message').forEach(function (element) {
        element.textContent = '';
    });

    // 에러 데이터가 필드 에러일 경우 처리
    if (errorData && errorData.fieldErrors) {
        errorData.fieldErrors.forEach(function (error) {
            var field = error.field;
            var reason = error.reason;

            // 해당 필드의 에러 메시지 요소 찾기
            var errorElement = document.getElementById(field + 'Error');
            if (errorElement) {
                errorElement.textContent = reason; // 에러 메시지 설정
                errorElement.style.display = 'block'; // 에러 메시지 표시
            }
        });
    } else if (errorData && errorData.message) {
        // 일반 에러 메시지 처리
        var loginError = document.getElementById('loginError');
        loginError.textContent = errorData.message;
        loginError.style.display = 'block';
    } else {
        // 기본 오류 메시지 처리
        var loginError = document.getElementById('loginError');
        loginError.textContent = '알 수 없는 오류가 발생했습니다. 다시 시도해주세요.';
        loginError.style.display = 'block';
    }
}

function togglePasswordVisibility() {
    var passwordField = document.getElementById("password");
    var passwordToggleButton = document.querySelector(".password-toggle-button");

    if (!passwordToggleButton) {
        console.error("passwordToggleButton 요소를 찾을 수 없습니다.");
        return;
    }

    var isPasswordVisible = passwordField.type === "text";

    // 비밀번호 표시 상태 변경
    passwordField.type = isPasswordVisible ? "password" : "text";

    // aria-label 업데이트
    passwordToggleButton.setAttribute(
        "aria-label",
        isPasswordVisible
            ? "비밀번호가 화면에서 보여지지 않습니다."
            : "비밀번호가 화면에 문자로 보여집니다."
    );

    // SVG 아이콘 교체
    var eyeIcon = `
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M6.52612 7.64835C8.15485 6.54396 9.98507 6 12 6C14.0149 6 15.8451 6.54396 17.4739 7.64835C19.1026 8.75275 20.278 10.2033 21 12C20.2948 13.7967 19.1194 15.2637 17.4739 16.3516C15.8451 17.456 14.0149 18 12 18C9.98507 18 8.15485 17.456 6.52612 16.3516C4.89739 15.2473 3.72202 13.7967 3 12C3.72202 10.2033 4.89739 8.73626 6.52612 7.64835ZM9.11194 14.8352C9.91791 15.6264 10.875 16.022 12 16.022C13.125 16.022 14.0821 15.6264 14.8881 14.8352C15.694 14.044 16.097 13.1044 16.097 12C16.097 10.8956 15.694 9.95604 14.8881 9.16483C14.0821 8.37363 13.125 7.97802 12 7.97802C10.875 7.97802 9.91791 8.37363 9.11194 9.16483C8.30597 9.95604 7.90299 10.8956 7.90299 12C7.90299 13.1044 8.30597 14.044 9.11194 14.8352ZM10.2705 10.3022C10.7575 9.84066 11.3284 9.59341 12 9.59341C12.6549 9.59341 13.2425 9.82418 13.7295 10.3022C14.1996 10.7802 14.4515 11.3407 14.4515 12C14.4515 12.6593 14.2164 13.2198 13.7295 13.6978C13.2425 14.1593 12.6549 14.4066 12 14.4066C11.3284 14.4066 10.7575 14.1758 10.2705 13.6978C9.80037 13.2198 9.54851 12.6593 9.54851 12C9.54851 11.3407 9.80037 10.7802 10.2705 10.3022Z" fill="#A0A0A0"></path>
        </svg>
    `;

    var crossedEyeIcon = `
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
            <rect x="19.7642" y="4.11853" width="1.70061" height="22.7708" transform="rotate(47 19.7642 4.11853)" fill="#A0A0A0"></rect>
            <path fill-rule="evenodd" clip-rule="evenodd" d="M12 6C9.98507 6 8.15485 6.54396 6.52612 7.64835C4.89739 8.73626 3.72202 10.2033 3 12C3.72202 13.7967 4.89739 15.2473 6.52612 16.3516C6.88607 16.5957 7.25587 16.8124 7.63532 17.0019L9.57051 15.2304C9.4125 15.1119 9.25968 14.9802 9.11194 14.8352C8.30597 14.044 7.90299 13.1044 7.90299 12C7.90299 10.8956 8.30597 9.95604 9.11194 9.16483C9.91791 8.37363 10.875 7.97802 12 7.97802C13.125 7.97802 14.0821 8.37363 14.8881 9.16483C15.107 9.37979 15.2962 9.60568 15.4557 9.84286L17.6892 7.79825C17.6183 7.7476 17.5466 7.69763 17.4739 7.64835C15.8451 6.54396 14.0149 6 12 6ZM19.1243 9.04673L16.0934 11.8213C16.0958 11.8804 16.097 11.9399 16.097 12C16.097 13.1044 15.694 14.044 14.8881 14.8352C14.0821 15.6264 13.125 16.022 12 16.022C11.8402 16.022 11.6839 16.014 11.5308 15.998L9.63884 17.73C10.3947 17.9104 11.1821 18 12 18C14.0149 18 15.8451 17.456 17.4739 16.3516C19.1194 15.2637 20.2948 13.7967 21 12C20.5505 10.8814 19.9252 9.89698 19.1243 9.04673ZM14.2175 10.9764C14.0982 10.7351 13.9346 10.5108 13.7295 10.3022C13.2425 9.82418 12.6549 9.59341 12 9.59341C11.3284 9.59341 10.7575 9.84066 10.2705 10.3022C9.80037 10.7802 9.54851 11.3407 9.54851 12C9.54851 12.6593 9.80037 13.2198 10.2705 13.6978C10.4376 13.8618 10.6145 13.9967 10.8019 14.1031L14.2175 10.9764Z" fill="#A0A0A0"></path>
        </svg>
    `;

    passwordToggleButton.innerHTML = isPasswordVisible ? eyeIcon : crossedEyeIcon;
}