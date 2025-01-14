// 공통 fetch 함수
export async function fetchWithAccessToken(url, options = {}) {
    var accessToken = localStorage.getItem("access");

    if (!accessToken) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login/member"; // 로그인 페이지로 리다이렉트
        return;
    }

    var headers = {
        ...(options.headers || {}),
        access: accessToken,
    };

    var response = await fetch(url, {
        ...options,
        headers,
        credentials: "include",
    });

    // 응답 상태 확인 및 에러 처리
    if (!response.ok) {
        var errorData = await response.json();
        throw new Error(errorData.message || "요청 실패");
    }

    return response;
}