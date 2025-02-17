import { fetchWithAccessToken } from '../utils/fetchUtils.js';
import { execDaumPostcode } from '../utils/map.js';

document.addEventListener("DOMContentLoaded", async () => {
    var passwordCheckSection = document.getElementById("passwordCheckSection");
    var infoEditSection = document.getElementById("infoEditSection");
    var marketingSection = document.getElementById("marketingSection"); // 마케팅 섹션
    var verifyPasswordBtn = document.getElementById("verifyPasswordBtn");
    var passwordToggleButton = document.querySelector(".password-toggle-button");

    // 비밀번호 변경 관련 요소
    var pwChangeBtn    = document.getElementById("pwChangeBtn");
    var pwChangeForm   = document.getElementById("pwChangeForm");
    var pwSubmitBtn    = document.getElementById("pwSubmitBtn");
    var currentPwInput = document.getElementById("currentPwInput");
    var newPwInput     = document.getElementById("newPwInput");
    var confirmPwInput = document.getElementById("confirmPwInput");
    var newPwCheckLen  = document.getElementById("newPwCheckLen");   // 길이 체크
    var newPwCheckChar = document.getElementById("newPwCheckChar");  // 문자 조합 체크
    var confirmPwCheckMsg = document.getElementById("confirmPwCheckMsg"); // 일치여부 체크

    // 주소 관련
    var addrSearchBtn  = document.getElementById("addrSearchBtn");
    var addrChangeBtn  = document.getElementById("addrChangeBtn");
    var addressDetailInput= document.getElementById("addressDetailInput");

    // 기타 버튼(연락처/이메일 수정) - 예시
    var phoneEditBtn   = document.getElementById("phoneEditBtn");
    var emailEditBtn   = document.getElementById("emailEditBtn");

    try {
        // 기존 마이페이지 회원정보(이름, 좋아요수, 등급 등) + 상세(주소,약관)
        var response = await fetchWithAccessToken("/api/edit", { method: "GET" });

        if (response.code === 200 && response.data) {
            renderMyPage(response.data);         // 사이드바, 상단 등 공통 정보
            showMemberInfoSection(response.data); // 회원정보 수정 섹션(이름, 주소, 약관 등)
            // renderTermsCheckboxes(response.data.agreedTerms); // 약관 정보
        } else {
            alert("데이터 전송실패");
            showModal(response.message || "마이페이지 데이터를 불러오는데 실패했습니다.");
        }
    } catch (error) {
        showModal(error.message || "마이페이지 데이터를 가져오는 중 오류가 발생했습니다.");
        setTimeout(() => window.location.href = "/login/member", 2000);
    }

    // 비밀번호 재확인 -> 다음 버튼
    verifyPasswordBtn.addEventListener("click", async () => {
        var password = document.getElementById("password").value;

        if (!password) {
            showModal("비밀번호를 입력해주세요.");
            return;
        }

        try {
            var res = await fetchWithAccessToken("/api/mypage/confirm-password", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ password }),
            });

            if (res.code === 200) {
                // 비밀번호 검증 성공 -> UI 변경
                passwordCheckSection.style.display = "none";
                infoEditSection.style.display = "block";
                marketingSection.style.display = "block"; // 마케팅 섹션도 함께 표시
            } else {
                handleErrors(res.message || "비밀번호가 일치하지 않습니다.");
            }
        } catch (error) {
            alert("서버 오류 발생: " + error);
        }
    });

    // 모든 toggle 버튼에 이벤트 리스너 붙이기
    document.querySelectorAll(".toggle-button").forEach(button => {
        button.addEventListener("click", function() {
            var targetInputId = this.getAttribute("data-target");
            console.log(targetInputId);
            toggleInputVisibility(targetInputId, this);
        });
    });

    // ============== [비밀번호 변경 로직] ==============
    pwChangeBtn?.addEventListener("click", () => {
        // '******' 인풋 비활성화 상태 그대로 두거나 숨겨도 됨
        // 아래는 폼을 보이도록
        pwChangeForm.style.display = "block";
    });

    // 새 비밀번호/확인 인풋 실시간 검사
    newPwInput?.addEventListener("input", checkNewPassword);
    confirmPwInput?.addEventListener("input", checkNewPassword);

    function checkNewPassword() {
        var newVal = newPwInput.value;
        var confirmVal = confirmPwInput.value;

        // 길이 체크 (8~20자)
        var lengthOk = (newVal.length >= 8 && newVal.length <= 20);
        if (lengthOk) {
            newPwCheckLen.style.color = "green";
        } else {
            newPwCheckLen.style.color = "#999";
        }

        // 문자 조합 체크 (대문자, 소문자, 숫자, 특수문자 각 1개 이상)
        var charRegex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*(),.?":{}|<>])/;
        var charOk = charRegex.test(newVal);
        if (charOk) {
            newPwCheckChar.style.color = "green";
        } else {
            newPwCheckChar.style.color = "#999";
        }

        // 비밀번호 재확인 (일치 여부)
        if (newVal && newVal === confirmVal) {
            confirmPwCheckMsg.style.color = "green";
        } else {
            confirmPwCheckMsg.style.color = "#999";
        }

        // 4) 최종: 길이/문자조합/재확인 모두 OK면 버튼 활성화
        if (lengthOk && charOk && (newVal === confirmVal && newVal !== "")) {
            pwSubmitBtn.disabled = false;
        } else {
            pwSubmitBtn.disabled = true;
        }
    }

    // 비밀번호 변경 버튼
    pwSubmitBtn?.addEventListener("click", async () => {
        if (pwSubmitBtn.disabled) return;

        var oldPw = currentPwInput.value;
        var newPw = newPwInput.value;
        var confirmPw = confirmPwInput.value;

        try {
            var res = await fetchWithAccessToken("/api/edit/change-password", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    oldPassword: oldPw,
                    newPassword: newPw,
                    confirmPassword: confirmPw
                }),
            });

            if (res.code === 200) {
                alert(res.data);
                localStorage.removeItem("access");
                window.location.href = "/";
            } else {
                alert("비밀번호 변경 실패: " + res.message);
            }
        } catch (error) {
            alert("서버 오류 발생: " + error);
        }
    });

    // ============== [연락처/이메일 수정 버튼] ==============
    phoneEditBtn?.addEventListener("click", () => {
        alert("본인인증 로직 구현 필요 (인증코드 입력 등) 예: 모달 열기");
    });
    emailEditBtn?.addEventListener("click", () => {
        alert("이메일 변경 로직 구현 필요 (인증코드 입력 등) 예: 모달 열기");
    });

    // ============== [주소 검색 로직] ==============
    addrSearchBtn?.addEventListener("click", () => {
        // 카카오 주소검색 API 연동 -> 주소 선택
        execDaumPostcode();
        addressDetailInput.disabled = false;
        addrChangeBtn.disabled = false;
    });

    addrChangeBtn?.addEventListener("click", async () => {
        if (addrChangeBtn.disabled) return;
        var zipCode = document.getElementById("zipCodeInput").value;
        var addressMain = document.getElementById("addressMainInput").value;
        var addressDetail = document.getElementById("addressDetailInput").value;

        try {
            var res = await fetchWithAccessToken("/api/address/edit", {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    zipCode: zipCode,
                    addressMain: addressMain,
                    addressDetail: addressDetail
                }),
            });

            if (res.code === 200) {
                alert(res.data);
            } else {
                // 만약 필드 에러가 있는 경우 각 에러 메시지 표시
                if (res.fieldErrors) {
                    res.fieldErrors.forEach(error => {
                        var errorElement = document.getElementById(error.field + "Error");
                        if (errorElement) {
                            errorElement.textContent = error.reason;
                            errorElement.style.display = "block";
                        }
                    });
                } else {
                    alert("주소 변경 실패: " + res.message);
                }
            }
        } catch (error) {
            alert("서버 오류 발생: " + error);
        }
    });
});

// 기존 renderMyPage, showMemberInfoSection, togglePasswordVisibility, showModal, etc...
function renderMyPage(data) {
    // (기존) 사이드바, 상단 멤버십 등
    document.getElementById("userName").textContent = data.name || "알 수 없음";
    document.getElementById("likeCount").textContent = data.likeCount || 6;
    document.getElementById("userGrade").textContent = data.userGrade || "ORANGE";
    document.getElementById("couponCount").textContent = data.couponCount || 8;
    document.getElementById("mileage").textContent = data.mileage || 966;

    // 비밀번호 재확인 섹션의 아이디 표시
    var loginIdConfirm = document.getElementById("loginIdConfirm");
    if (loginIdConfirm) {
        loginIdConfirm.textContent = data.loginId || "알 수 없음";
    }

    // 회원정보 수정 섹션 아이디 표시
    var editLoginId = document.getElementById("editLoginId");
    if (editLoginId) {
        editLoginId.textContent = (data.loginId || "알 수 없음");
    }
}

function showMemberInfoSection(memberData) {
    // [회원 정보] 이름, 연락처, 생년월일
    document.getElementById("editName").textContent     = memberData.name     || "-";
    document.getElementById("editBirth").textContent    = memberData.birth    || "-";
    document.getElementById("phoneNumInput").value = memberData.phoneNum || "-";
    document.getElementById("userEmailInput").value = memberData.loginId || "-";

    // [주소] - zipCodeInput, addressMainInput, addressDetailInput
    if (memberData.address) {
        document.getElementById("zipCodeInput").value    = memberData.address.zipCode       || "-";
        document.getElementById("addressMainInput").value   = memberData.address.addressMain   || "-";
        document.getElementById("addressDetailInput").value = memberData.address.addressDetail || "-";
    }

    // [마케팅/광고 알림 설정] -> marketingSection
    // 기존에는 ul#termsList에 li로 표시했지만,
    // 요구사항에 맞춰 체크박스 등으로 확장 가능
    var termsList = document.getElementById("termsList");
    termsList.innerHTML = "";
    if (memberData.agreedTerms && Array.isArray(memberData.agreedTerms)) {
        memberData.agreedTerms.forEach(term => {
            // 간단히 li로 표시
            var li = document.createElement("li");
            li.textContent = `${term.name} (${term.agreeYn})`;
            termsList.appendChild(li);
        });
    }
}

// 비밀번호 표시/숨기기 (기존 로직)
// 공통 함수
function toggleInputVisibility(targetInputId, buttonElement) {
    var passwordField = document.getElementById(targetInputId);
    if (!passwordField) return;

    // 현재 타입을 토글: password <-> text
    var isPasswordVisible = passwordField.type === "text";
    passwordField.type = isPasswordVisible ? "password" : "text";

    buttonElement.setAttribute(
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

    buttonElement.innerHTML = isPasswordVisible ? eyeIcon : crossedEyeIcon;
}

// 모달 표시 함수 (기존)
function showModal(message) {
    var modal = document.querySelector('.modal');
    var modalBody = modal.querySelector('.modal-body');
    modalBody.textContent = message;
    modal.style.display = "flex";
}
function closeModal() {
    var modal = document.querySelector('.modal');
    modal.style.display = 'none';
}
function handleErrors(errorData) {
    document.querySelectorAll('.error-message').forEach(e => e.textContent = '');
    var confirmError = document.getElementById('passwordError');
    confirmError.textContent = errorData || '알 수 없는 오류가 발생했습니다.';
    confirmError.style.display = 'block';
}