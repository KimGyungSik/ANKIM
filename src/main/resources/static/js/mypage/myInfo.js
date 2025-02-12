import { fetchWithAccessToken } from '../utils/fetchUtils.js';

document.addEventListener("DOMContentLoaded", async () => {
    var passwordCheckSection = document.getElementById("passwordCheckSection");
    var infoEditSection = document.getElementById("infoEditSection");
    var verifyPasswordBtn = document.getElementById("verifyPasswordBtn");
    
    try {
        var response = await fetchWithAccessToken("/api/edit", { method: "GET" });

        if (!response || response.error) {
            alert("서버문제");
            throw new Error(response.message || "서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }

        if (response.code === 200 && response.data) {
            renderMyPage(response.data);
        } else {
            alert("데이터 전송실패");
            showModal(response.message || "마이페이지 데이터를 불러오는데 실패했습니다.");
        }
    } catch (error) {
        alert("대실패");
        alert(error.message);
        showModal(error.message || "마이페이지 데이터를 가져오는 중 오류가 발생했습니다.");
        setTimeout(() => window.location.href = "/login/member", 2000); // 2초 후 로그인 페이지로 이동
    }

    verifyPasswordBtn.addEventListener("click", async () => {
        const password = document.getElementById("password").value;

        if (!password) {
            alert("비밀번호를 입력해주세요.");
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
                alert(response.message || "비밀번호가 일치하지 않습니다.");
            }
        } catch (error) {
            alert("서버 오류 발생");
        }
    });
});

// 마이페이지 데이터 렌더링
function renderMyPage(data) {
    document.getElementById("userName").textContent = data.name || "알 수 없음";
    document.getElementById("loginId").textContent = data.loginId || "알 수 없음";
    document.getElementById("likeCount").textContent = data.likeCount || 6;
    document.getElementById("userGrade").textContent = data.userGrade || "ORANGE";
    document.getElementById("couponCount").textContent = data.couponCount || 8;
    document.getElementById("mileage").textContent = data.mileage || 966;

    var contentContainer = document.querySelector(".mypage-content");
}

// 모달 표시 함수
function showModal(message) {
    var modal = document.querySelector('.modal');
    var modalBody = modal.querySelector('.modal-body');
    modalBody.textContent = message;
    modal.style.display = "flex";
}