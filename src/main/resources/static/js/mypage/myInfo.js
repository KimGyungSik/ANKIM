import { fetchWithAccessToken } from '../utils/fetchUtils.js';
import { execDaumPostcode } from '../utils/map.js';
import { selectedAddress } from '../utils/addressStore.js'; // 저장된 주소

// 전역 변수 (파일 상단)
let allTerms = [];
let termsTree = [];
let marketingStatus = {
};

document.addEventListener("DOMContentLoaded", async () => {
    var passwordCheckSection = document.getElementById("passwordCheckSection");
    var infoEditSection = document.getElementById("infoEditSection");
    var termsSection = document.getElementById("termsSection"); // 약관
    var leaveSection = document.getElementById("leaveSection"); // 약관
    var verifyPasswordBtn = document.getElementById("verifyPasswordBtn");

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
    var zipCodeInput = document.getElementById("zipCodeInput");
    var addressMainInput = document.getElementById("addressMainInput");
    var addressDetailInput= document.getElementById("addressDetailInput");

    // 기타 버튼(연락처/이메일 수정) - 예시
    var phoneEditBtn   = document.getElementById("phoneEditBtn");
    var emailEditBtn   = document.getElementById("emailEditBtn");

    // 회원 탈퇴
    var leaveBtn = document.getElementById("leaveBtn");

    try {
        // 기존 마이페이지 회원정보(이름, 좋아요수, 등급 등) + 상세(주소,약관)
        var response = await fetchWithAccessToken("/api/edit", { method: "GET" });

        if (response.code === 200 && response.data) {
            renderMyPage(response.data);         // 사이드바, 상단 등 공통 정보
            showMemberInfoSection(response.data); // 회원정보 수정 섹션(이름, 주소, 약관 등)

            // 약관 트리 렌더링 (agreedTerms가 있을 경우)
            if(response.data.agreedTerms) {
                // 1) 전역 변수에 먼저 할당
                allTerms = response.data.agreedTerms;

                // 2) 트리 빌드 & 렌더링
                termsTree = buildTermsTree(allTerms);
                const termsContainer = termsSection;
                // termsContainer가 존재할 때만 렌더링
                if (termsContainer) {
                    renderTermsTree(termsTree, termsContainer);
                }
            }
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
            handleErrors("비밀번호를 입력해주세요.");
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
                termsSection.style.display = "block";
                leaveSection.style.display = "block";
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
        // input 필드에서 가져오는 대신, 저장된 데이터를 사용
        var zipCode = selectedAddress.zipCode;
        var addressMain = selectedAddress.addressMain;
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

    // 주소 에러 비우는 이벤트
    zipCodeInput.addEventListener("input", function() {
        const errorElement = document.getElementById("zipCodeError");
        if (errorElement) {
            errorElement.style.display = "none";
        }
    });

    addressMainInput.addEventListener("input", function() {
        const errorElement = document.getElementById("addressMainError");
        if (errorElement) {
            errorElement.style.display = "none";
        }
    });

    addressDetailInput.addEventListener("input", function() {
        const errorElement = document.getElementById("addressDetailError");
        if (errorElement) {
            errorElement.style.display = "none";
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

// === 약관 트리구조 만들기 함수 ===
function buildTermsTree(termsList) {
    const map = {};
    termsList.forEach(t => {
        // 각 항목에 children 배열 추가
        map[t.termsNo] = { ...t, children: [] };
    });

    const roots = [];
    // 부모-자식 연결
    Object.values(map).forEach(node => {
        if (map[node.parentsNo]) {
            map[node.parentsNo].children.push(node);
        } else {
            roots.push(node);
        }
    });

    // 부모 노드(자식이 있는 노드)의 동의 상태를 marketingStatus에 저장
    Object.values(map).forEach(node => {
        if (node.children && node.children.length > 0) {
            // 자식이 있으면 광고성 동의 그룹이므로, 동의 상태 저장
            marketingStatus[node.termsNo] = node.agreeYn; // 서버에서 받은 값 ("Y" 또는 "N")
        }
    });
    return roots;
}

// [마케팅/광고 알림 설정] -> termsSection에 랜더링
/*
약관 트리를 렌더링할 때
자식 노드가 있는 경우(광고성 동의 그룹)는 체크박스 없이 제목(라벨)과 설명을 표시하고,
자식 노드가 없는 항목은 체크박스 + 라벨로 렌더링
자식 항목을 렌더링할 때 부모의 termsNo 값을 dataset.marketingParent에 저장
 */
function renderTermsTree(roots, container) {
    roots.forEach(node => {
        if (node.children && node.children.length > 0) {
            // 부모 노드: 체크박스 없이 그룹 제목과 설명 표시
            const groupDiv = document.createElement("div");
            groupDiv.className = "terms-parent";

            const titleSpan = document.createElement("h4");
            titleSpan.className = "terms-parent-title";
            titleSpan.textContent = `[선택] ${node.name}`;

            const descDiv = document.createElement("div");
            descDiv.className = "terms-parent-desc";
            descDiv.innerText = "서비스의 중요 안내사항 및 주문/배송에 대한 정보는 위 수신 여부와 관계없이 발송됩니다.\n하위 항목 중 하나라도 동의하면 광고성 동의로 처리됩니다.";
            descDiv.style.fontSize = "13px";  // 원하는 크기로 설정
            descDiv.style.marginBottom = "1.3%";

            groupDiv.appendChild(titleSpan);
            container.appendChild(groupDiv);
            groupDiv.appendChild(descDiv);

            // 자식 항목은 별도의 컨테이너에 렌더링
            const childContainer = document.createElement("div");
            childContainer.className = "term-children";

            node.children.forEach(child => {
                renderLeafTerm(child, childContainer, node); // 부모 정보(node) 전달
            });
            container.appendChild(childContainer);
        } else {
            // 자식이 없는 항목: 단순 체크박스+라벨 렌더링
            renderLeafTerm(node, container, null);
            // 자식 항목을 렌더링하기 전에 구분선 추가
            const separator = document.createElement("div");
            separator.className = "info-line"; // 구분선 추가
            container.appendChild(separator);
        }
    });
}

function renderLeafTerm(node, container, parentNode) {
    const itemDiv = document.createElement("div");
    itemDiv.className = "term-leaf-item";

    const checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.id = `termsCheck_${node.termsNo}`;
    checkbox.checked = (node.agreeYn === "Y");
    checkbox.dataset.termsNo = node.termsNo;

    // 만약 부모 노드가 있다면, 해당 parent's termsNo를 저장 (즉, 마케팅 동의를 위한 부모)
    if (parentNode) {
        checkbox.dataset.marketingParent = parentNode.termsNo;
    }

    const label = document.createElement("label");
    label.htmlFor = checkbox.id;
    label.textContent = "[선택] " + node.name;

    // 리프 항목에 대해서만 체크 이벤트를 등록 (부모는 체크박스가 없으므로)
    checkbox.addEventListener("change", e => onLeafCheckboxChange(e, node));

    itemDiv.appendChild(checkbox);
    itemDiv.appendChild(label);
    container.appendChild(itemDiv);
}

/*
 * buildTermsTree()로 만든 트리 구조(루트 배열)에서
 * 부모 노드로 사용되지 않고(자식 노드가 없는) 마케팅 약관을 찾아 반환
 * 여기서는 예시로 level이 2인 항목을 마케팅 약관으로 가정
 *
 * nodes - buildTermsTree()의 결과(루트 노드 배열)
 * returns {Object|null} - 마케팅 약관 노드 또는 null
 */
function findMarketingTermFromTree(nodes) {
    for (const node of nodes) {
        // 자식 노드가 없으면 leaf node
        if (!node.children || node.children.length === 0) {
            // 여기서 "마케팅 약관"은 2 level
            if (node.level === 2) {
                return node;
            }
        } else {
            // 자식 노드가 있으면, 재귀적으로 검색
            const found = findMarketingTermFromTree(node.children);
            if (found) return found;
        }
    }
    return null;
}

async function onLeafCheckboxChange(e, node) {
    const isChecked = e.target.checked;
    const termsNo = node.termsNo;

    if (isChecked) {
        if (needMarketingAgreement()) {
            openConfirmModal(
                `${node.name} 동의 시 마케팅 약관도 함께 동의해야 합니다. 진행하시겠습니까?`,
                async () => {
                    const marketingTerm = findMarketingTermFromTree(termsTree);
                    const payload = [{ terms_no: termsNo, terms_hist_agreeYn: "Y" }];
                    if (marketingTerm) {
                        payload.push({ terms_no: marketingTerm.termsNo, terms_hist_agreeYn: "Y" });
                    }
                    const res = await postTermsUpdate(payload);
                    if (res.code === 200) {
                        // 마케팅 노드도 체크 표시
                        if (marketingTerm) {
                            const mkCb = document.getElementById(`termsCheck_${marketingTerm.termsNo}`);
                            if (mkCb) mkCb.checked = true;
                        }
                        showAlertModal(res.data);
                    } else {
                        // 실패 시 다시 해제
                        e.target.checked = false;
                        showAlertModal(res.message || "동의 처리 실패");
                    }
                },
                () => {
                    e.target.checked = false;
                }
            );
        } else {
            // 마케팅 약관이 이미 체크되어 있다면, 자식 항목만 전송
            try {
                const payload = [{ terms_no: termsNo, terms_hist_agreeYn: "Y" }];
                const res = await postTermsUpdate(payload);
                if (res.code === 200) {
                    showAlertModal(res.data);
                } else {
                    e.target.checked = false;
                    showAlertModal("동의 처리 실패");
                }
            } catch (err) {
                e.target.checked = false;
            }
        }
    } else {
        // [체크 해제 시 로직]
        // (1) 만약 "마케팅 노드"를 해제하는 경우 => 광고성도 함께 해제 모달
        if (isMarketingNode(node)) {
            openConfirmModalForMarketingCancel(
                "마케팅 목적의 개인정보 수집 및 이용 동의를 철회할 경우,\n광고성 정보 수신 동의도 함께 철회됩니다.\n모두 동의 철회하기를 선택하면 광고성 동의(푸시, 문자, 이메일)도 해제됩니다.",
                async () => {
                    // "모두 동의 철회하기"
                    // 마케팅 노드 + 이미 체크된 광고성 노드들
                    // const roots = buildTermsTree(allTerms);
                    const advChecked = findCheckedAdvertisementNodes(termsTree);

                    // payload: 마케팅 본인 + 광고성 체크된 것들 -> N
                    const payload = [{ terms_no: termsNo, terms_hist_agreeYn: "N" }];
                    advChecked.forEach(advNode => {
                        payload.push({
                            terms_no: advNode.termsNo,
                            terms_hist_agreeYn: "N"
                        });
                    });

                    const res = await postTermsUpdate(payload);
                    if (res.code === 200) {
                        // 모두 해제
                        const mkCb = document.getElementById(`termsCheck_${termsNo}`);
                        if (mkCb) mkCb.checked = false;
                        advChecked.forEach(n => {
                            const c = document.getElementById(`termsCheck_${n.termsNo}`);
                            if (c) c.checked = false;
                        });
                        showAlertModal(res.data);
                    } else {
                        // 실패하면 다시 체크
                        const mkCb = document.getElementById(`termsCheck_${termsNo}`);
                        if (mkCb) mkCb.checked = true;
                        showAlertModal("해제 실패");
                    }
                },
                () => {
                    // "모두 동의 유지하기" => 다시 체크
                    e.target.checked = true;
                }
            );
        } else {
            // (2) 일반 노드 해제
            try {
                const payload = [{ terms_no: termsNo, terms_hist_agreeYn: "N" }];
                const res = await postTermsUpdate(payload);
                if (res.code === 200) {
                    showAlertModal(res.data);
                } else {
                    e.target.checked = true;
                    showAlertModal("해제 실패");
                }
            } catch (err) {
                e.target.checked = true;
            }
        }
    }
}

async function postTermsUpdate(termsArray) {
    // termsArray 예시:
    // [ { terms_no:4, terms_hist_agreeYn:"Y" }, { terms_no:7, terms_hist_agreeYn:"Y" } ]
    const res = await fetchWithAccessToken("/api/terms/update", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(termsArray),
    });
    return await res;
}

// 마케팅 노드 식별
function isMarketingNode(node) {
    if (!node.children || node.children.length === 0) {
        // 여기서 "마케팅 약관"은 2 level
        if (node.level === 2) {
            return true;
        }
    } else {
        return false;
    }
}

function needMarketingAgreement() {
    const marketingTerm = findMarketingTermFromTree(termsTree);
    if (!marketingTerm) return false;
    const marketingCheckbox = document.getElementById(`termsCheck_${marketingTerm.termsNo}`);
    return marketingCheckbox && !marketingCheckbox.checked;
}

// 광고성(푸시/문자/이메일 등) 노드 중 체크된 것들을 찾는 함수
function findCheckedAdvertisementNodes(roots) {
    const result = [];

    function dfs(node) {
        // (예) 광고성 노드는 level=2 + 자식있음(광고성 부모) or level=3(문자/이메일)
        const cb = document.getElementById(`termsCheck_${node.termsNo}`);
        if (cb && cb.checked) {
            // 여기에 "node.level === 3" or "부모가 광고성" 등 추가 조건을 넣을 수도 있음
            result.push(node);
        }
        if (node.children) {
            node.children.forEach(dfs);
        }
    }

    roots.forEach(dfs);
    return result;
}

// 마케팅 동의 해제시 모달 함수
function openConfirmModalForMarketingCancel(message, onConfirm, onCancel) {
    // 버튼 영역이 보이도록 모달을 엽니다.
    showModal(message, true);

    const modal = document.getElementById("termsModal");
    const titleEl = document.getElementById("termsModalTitle");
    const msgEl = document.getElementById("termsModalMessage");
    const confirmBtn = document.getElementById("termsModalConfirmBtn");
    const cancelBtn = document.getElementById("termsModalCancelBtn");
    const closeBtn  = modal.querySelector(".close-button"); // X 버튼

    // 모달 내용
    titleEl.textContent = "마케팅 동의 해제 안내";
    msgEl.textContent = message;

    // 버튼 텍스트
    confirmBtn.textContent = "모두 동의 철회하기";
    cancelBtn.textContent = "모두 동의 유지하기";

    // 기존 리스너를 제거
    confirmBtn.onclick = null;
    cancelBtn.onclick = null;
    closeBtn.onclick   = null;

    // 모달창 열기
    modal.style.display = "flex";

    // 동의, 철회 확인 버튼
    confirmBtn.onclick = () => {
        closeModal();
        // ESC 키 이벤트도 제거
        document.removeEventListener("keydown", escHandler);
        // 콜백 실행
        if (onConfirm) onConfirm();
    };

    // 동의, 철회 취소 버튼
    cancelBtn.onclick = () => {
        closeModal();
        document.removeEventListener("keydown", escHandler);
        if (onCancel) onCancel();
    };

    // x 버튼(취소 버튼이랑 동일하게 동작해야됨)
    closeBtn.onclick = () => {
        closeModal();
        document.removeEventListener("keydown", escHandler);
        if (onCancel) onCancel();
    };

    // ESC 키 → 취소와 동일 로직
    function escHandler(e) {
        if (e.key === "Escape") {
            e.preventDefault();
            closeModal();
            document.removeEventListener("keydown", escHandler);
            if (onCancel) onCancel();
        }
    }
    document.addEventListener("keydown", escHandler);
}

function openConfirmModal(message, onConfirm, onCancel) {
    // 버튼 영역이 보이도록 모달을 엽니다.
    showModal(message, true);

    // 모달 요소들 가져오기
    const modal = document.getElementById("termsModal");
    // ↑ 기존에 termsModal인지 marketingModal인지 확인 후 수정
    const titleEl = document.getElementById("termsModalTitle");
    const msgEl = document.getElementById("termsModalMessage");
    const confirmBtn = document.getElementById("termsModalConfirmBtn");
    const cancelBtn = document.getElementById("termsModalCancelBtn");
    const closeBtn  = modal.querySelector(".close-button"); // X 버튼

    // 모달 제목/메시지 설정
    titleEl.textContent = "광고성 정보 수신 동의";
    // 예시: "광고성 정보 수신 동의" 라고 표시
    // 멀티라인 문구 (white-space: pre-wrap 적용)
    msgEl.textContent = message;
    // 예: "광고성 정보 알림을 받으시려면\n마케팅 목적의 개인정보 수집 및 이용 동의가 필요해요."

    // 모달 열기
    modal.style.display = "flex";

    // 버튼 텍스트 강제 지정(필요하면)
    cancelBtn.textContent = "다음에 하기";
    confirmBtn.textContent = "함께 동의하기";

    // 기존 이벤트 리스너 제거
    confirmBtn.onclick = null;
    cancelBtn.onclick = null;

    // [함께 동의하기] 버튼
    confirmBtn.onclick = () => {
        closeModal();
        document.removeEventListener("keydown", escHandler);
        if (onConfirm) onConfirm();
    };
    // [다음에 하기] 버튼
    cancelBtn.onclick = () => {
        closeModal();
        document.removeEventListener("keydown", escHandler);
        if (onCancel) onCancel();
    };
    // x 버튼(취소 버튼이랑 동일하게 동작해야됨)
    closeBtn.onclick = () => {
        closeModal();
        document.removeEventListener("keydown", escHandler);
        if (onCancel) onCancel();
    };

    // ESC 키 → 취소와 동일 로직
    function escHandler(e) {
        if (e.key === "Escape") {
            e.preventDefault();
            closeModal();
            document.removeEventListener("keydown", escHandler);
            if (onCancel) onCancel();
        }
    }
    document.addEventListener("keydown", escHandler);
}

function showAlertModal(serverData, showButtons = false) {
    const titleEl = document.getElementById("termsModalTitle");

    // 모달 제목/메시지 설정
    titleEl.textContent = "약관 동의 변경";

    if (serverData && serverData.message) {
        let lines = serverData.message;
        // 만약 lines가 배열이 아니라면 배열로 처리
        if (!Array.isArray(lines)) {
            lines = [lines];
        }
        const joined = lines.join("\n");
        const finalMsg = joined + "\n\n" + (serverData.date || "") + "\n" + (serverData.sender || "");
        showModal(finalMsg.trim(), showButtons); // trim()으로 앞뒤 공백 정리
    } else {
        // 그냥 문자열인 경우
        showModal(serverData, showButtons);
    }
}

// 모달 표시 함수
function showModal(message, showButtons = false) {
    var modal = document.getElementById("termsModal");
    var modalMessage = document.getElementById("termsModalMessage");
    var modalFooter = modal.querySelector(".modal-footer");
    modalMessage.textContent = message;
    modalMessage.style.whiteSpace = "pre-wrap"; // 줄바꿈을 유지
    // 버튼 영역 보이기/숨기기 설정
    modalFooter.style.display = showButtons ? "flex" : "none";
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