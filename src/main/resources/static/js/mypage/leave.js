import { fetchWithAccessToken } from '../utils/fetchUtils.js';

document.addEventListener("DOMContentLoaded", async () => {
    // 회원 탈퇴 버튼
    var leaveSection = document.getElementById("leaveSection"); // 탈퇴 섹션
    var leaveReasonList = document.getElementById("leaveReasonList"); // 탈퇴 사유 리스트

    // 회원 탈퇴 이유 API 호출 및 렌더링
    try {
        const res = await fetchWithAccessToken('/api/leaveReason', {
            method: 'GET',
            headers: { "Content-Type": "application/json" },
        });

        if (res.code === 200 && res.data) {
            // API로 받은 탈퇴 사유 리스트를 렌더링
            const leaveReasons = res.data;
            console.log(leaveReasons);
        } else {
            alert('탈퇴 사유를 불러오는데 실패했습니다.');
            window.location.href("/mypage/edit/info");
        }
    } catch (error) {
        alert("탈퇴 사유를 불러오는데 오류가 발생했습니다.");
        window.location.href("/mypage/edit/info");
    }
});