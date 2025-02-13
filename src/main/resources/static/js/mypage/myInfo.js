import { fetchWithAccessToken } from '../utils/fetchUtils.js';

document.addEventListener("DOMContentLoaded", async () => {
    var passwordCheckSection = document.getElementById("passwordCheckSection");
    var infoEditSection = document.getElementById("infoEditSection");
    var verifyPasswordBtn = document.getElementById("verifyPasswordBtn");
    var passwordToggleButton = document.querySelector(".password-toggle-button");

    try {
        var response = await fetchWithAccessToken("/api/edit", { method: "GET" });

        if (response.code === 200 && response.data) {
            // 화면에 데이터 세팅
            renderMyPage(response.data);
            showMemberInfoSection(response.data);
        } else {
            alert("데이터 전송실패");
            showModal(response.message || "마이페이지 데이터를 불러오는데 실패했습니다.");
        }
    } catch (error) {
        showModal(error.message || "마이페이지 데이터를 가져오는 중 오류가 발생했습니다.");
        setTimeout(() => window.location.href = "/login/member", 2000); // 2초 후 로그인 페이지로 이동
    }

    verifyPasswordBtn.addEventListener("click", async () => {
        const password = document.getElementById("password").value;

        if (!password) {
            showModal("비밀번호를 입력해주세요.");
            return;
        }

        try {
            const response = await fetchWithAccessToken("/api/mypage/confirm-password", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ password }),
            });

            if (response.code === 200) {
                // 비밀번호 검증 성공 -> UI 변경
                passwordCheckSection.style.display = "none"; // 비밀번호 확인 화면 숨기기
                infoEditSection.style.display = "block"; // 회원정보 수정 화면 표시

            } else {
                handleErrors(response.message || "비밀번호가 일치하지 않습니다.");
            }
        } catch (error) {
            alert(error);
            alert("서버 오류 발생");
        }
    });

    // 비밀번호 표시/숨기기 이벤트 리스너 추가
    passwordToggleButton.addEventListener("click", togglePasswordVisibility);
});

// 마이페이지 데이터 렌더링
function renderMyPage(data) {
    document.getElementById("userName").textContent = data.name || "알 수 없음";
    document.getElementById("likeCount").textContent = data.likeCount || 6;
    document.getElementById("userGrade").textContent = data.userGrade || "ORANGE";
    document.getElementById("couponCount").textContent = data.couponCount || 8;
    document.getElementById("mileage").textContent = data.mileage || 966;

    // 비밀번호 재확인 섹션 아이디 표시
    var loginIdConfirm = document.getElementById("loginIdConfirm");
    if (loginIdConfirm) {
        loginIdConfirm.textContent = data.loginId || "알 수 없음";
    }

    // 회원정보 수정 섹션 아이디 표시
    var editLoginId = document.getElementById("editLoginId");
    if (editLoginId) {
        editLoginId.textContent = "아이디(이메일): " + (data.loginId || "알 수 없음");
    }

    var contentContainer = document.querySelector(".mypage-content");
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

// 모달 표시 함수
function showModal(message) {
    var modal = document.querySelector('.modal');
    var modalBody = modal.querySelector('.modal-body');
    modalBody.textContent = message;
    modal.style.display = "flex";
}

function closeModal() {
    var modal = document.querySelector('.modal');
    modal.style.display = 'none'; // 모달 숨김
}

function handleErrors(errorData) {
    // 기존 에러 메시지 초기화
    document.querySelectorAll('.error-message').forEach(function (element) {
        element.textContent = '';
    });
    var confirmError = document.getElementById('passwordError');

    // 일반 에러 메시지 처리
    if (errorData != null) {
        confirmError.textContent = errorData;
        confirmError.style.display = 'block';
    } else {
        // 기본 오류 메시지 처리
        confirmError.textContent = '알 수 없는 오류가 발생했습니다. 다시 시도해주세요.';
        confirmError.style.display = 'block';
    }
}

// 회원정보 표시 함수
function showMemberInfoSection(memberData) {
    console.log("memberData:", memberData);
    console.log("address:", memberData.address);

    // [2] 회원 정보
    document.getElementById("editName").textContent     = memberData.name     || "-";
    document.getElementById("editPhoneNum").textContent = memberData.phoneNum || "-";
    document.getElementById("editBirth").textContent    = memberData.birth    || "-";

    // 주소 정보가 있는지 확인
    if (memberData.address) {
        // memberData.address가 null이 아니면 개별 필드 할당
        document.getElementById("editZipCode").textContent       = memberData.address.zipCode         || "-";
        document.getElementById("editAddressMain").textContent   = memberData.address.addressMain     || "-";
        document.getElementById("editAddressDetail").textContent = memberData.address.addressDetail   || "-";
    } else {
        // address가 null인 경우 기본값("-")만 표시
        document.getElementById("editZipCode").textContent       = "-";
        document.getElementById("editAddressMain").textContent   = "-";
        document.getElementById("editAddressDetail").textContent = "-";
    }

    // [3] 마케팅/광고 알림 설정(약관 동의 목록)
    const termsList = document.getElementById("termsList");
    termsList.innerHTML = ""; // 초기화

    if (memberData.agreedTerms && Array.isArray(memberData.agreedTerms)) {
        memberData.agreedTerms.forEach(term => {
            // li에 표시
            const li = document.createElement("li");
            // 예) "이메일 수신 동의 (N)"
            li.textContent = `${term.name} (${term.agreeYn})`;
            termsList.appendChild(li);

            // 체크박스로 표시하고 싶다면:
            // const checkbox = document.createElement("input");
            // checkbox.type = "checkbox";
            // checkbox.checked = (term.agreeYn === "Y");
            // li.appendChild(checkbox);
            // li.appendChild(document.createTextNode(` ${term.name}`));
        });
    }
}