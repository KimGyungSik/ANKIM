import { fetchWithAccessToken } from '../utils/fetchUtils.js';

document.addEventListener("DOMContentLoaded", async () => {
    try {
        const response = await fetchWithAccessToken("/api/mypage", { method: "GET" });

        if (!response || response.error) {
            alert("서버문제");
            throw new Error(response.message || "서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }

        if (response.code === 200 && response.data) {
            alert("정상");
            renderMyPage(response.data);
        } else {
            alert("데이터 전송실패");
            showModal(response.message || "마이페이지 데이터를 불러오는데 실패했습니다.");
        }
    } catch (error) {
        alert("대실패");
        showModal(error.message || "마이페이지 데이터를 가져오는 중 오류가 발생했습니다.");
        setTimeout(() => window.location.href = "/login/member", 2000); // 2초 후 로그인 페이지로 이동
    }
});

// 마이페이지 데이터 렌더링
function renderMyPage(data) {
    document.getElementById("userName").textContent = data.userName || "알 수 없음";
    document.getElementById("likeCount").textContent = data.likeCount || 0;
    document.getElementById("userGrade").textContent = data.userGrade || "미정";
    document.getElementById("couponCount").textContent = data.couponCount || 0;
    document.getElementById("mileage").textContent = data.mileage || 0;

    const contentContainer = document.querySelector(".mypage-content");
    contentContainer.innerHTML = `<p>마이페이지 정보가 정상적으로 불러와졌습니다.</p>`;
}

// 모달 표시 함수
function showModal(message) {
    alert(message);
}