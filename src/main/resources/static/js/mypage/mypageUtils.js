import { fetchWithAccessToken } from '../utils/fetchUtils.js';

document.addEventListener("DOMContentLoaded", async () => {
    try {
        const response = await fetchWithAccessToken("/api/mypage", { method: "GET" });

        if (response.code === 200 && response.data) {
            renderMyPage(response.data);
        } else {
            showModal(response.message || "마이페이지 데이터를 불러오는데 실패했습니다.");
        }
    } catch (error) {
        setTimeout(() => window.location.href = "/login/member", 2000); // 2초 후 로그인 페이지로 이동
    }

    window.closeModal = function () {
        var modal = document.querySelector('.modal');
        modal.style.display = "none";
    };

    // ESC 키로도 모달 닫기 기능 추가
    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
            closeModal();
        }
    });
});

// 마이페이지 데이터 렌더링
function renderMyPage(data) {
    document.getElementById("userName").textContent = data.name || "알 수 없음";
    document.getElementById("likeCount").textContent = data.likeCount || 6;
    document.getElementById("userGrade").textContent = data.userGrade || "ORANGE";
    document.getElementById("couponCount").textContent = data.couponCount || 8;
    document.getElementById("mileage").textContent = data.mileage || 966;

    const contentContainer = document.querySelector(".mypage-content");
    // contentContainer.innerHTML = `<p>마이페이지 정보가 정상적으로 불러와졌습니다.</p>`;
}

