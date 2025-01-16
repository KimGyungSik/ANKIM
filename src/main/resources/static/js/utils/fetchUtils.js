// 공통 fetch 함수
export async function fetchWithAccessToken(url, options = {}, loginType = "member") {
    var accessToken = localStorage.getItem("access");

    if (!accessToken) {
        alert("로그인이 필요합니다.");
        var loginUrl = loginType === "admin" ? "/login/admin" : "/login/member"; // 동적 로그인 페이지 설정
        window.location.href = loginUrl;
        return;
    }

    var headers = {
        ...(options.headers || {}), // 기존 옵션에 설정된 헤더 유지
        access: accessToken, // Access Token 추가
    };

    var response = await fetch(url, {
        ...options,
        headers,
        credentials: "include", // 쿠키 정보 포함 (CORS 설정 필요)
    });

    // Access Token이 만료되었을 경우
    if (response.status === 401) {
        console.warn("Access Token이 만료되었습니다. Refresh Token으로 재발급 시도");

        var newTokenResponse = await refreshAccessToken(accessToken);

        if (newTokenResponse.success) {
            localStorage.setItem("access", newTokenResponse.accessToken); // 새 토큰 저장
            return fetchWithAccessToken(url, options, loginType); // 기존 요청 다시 실행
        } else {
            handleLogout(loginType); // Refresh Token도 만료된 경우 로그아웃 처리
        }
    }

    return response;
}

// Refresh Token을 이용한 Access Token 재발급 요청
async function refreshAccessToken(accessToken) {
    try {
        var response = await fetch("/reissue", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "access" : accessToken
            },
            credentials: "include", // 쿠키에 저장된 Refresh Token 포함
        });

        if (!response.ok) throw new Error("Refresh Token 만료");

        var data = await response.json();
        return { success: true, accessToken: response.headers.get("access") }; // 새 Access Token 반환
    } catch (error) {
        console.error("Refresh Token 만료, 로그아웃 필요:", error);
        return { success: false };
    }
}

// Refresh Token까지 만료되면 로그아웃 처리
async function handleLogout(loginType) {
    alert("세션이 만료되었습니다. 다시 로그인해주세요.");
    localStorage.removeItem("access");

    await fetch("/logout", { method: "POST", credentials: "include" });

    var loginUrl = loginType === "admin" ? "/login/admin" : "/login/member";
    window.location.href = loginUrl;
}