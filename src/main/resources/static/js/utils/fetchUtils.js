// 공통 fetch 함수
export async function fetchWithAccessToken(url, options = {}, loginType = "member") {
    var accessToken = localStorage.getItem("access");

    if (!accessToken) {
        alert("로그인이 필요합니다.");
        redirectToLogin(loginType);
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

    var responseData;
    try {
        responseData = await response.json();
    } catch (error) {
        return response;
    }

    // JWT 형식 및 refresh 토큰 만료 에러 상태 코드 처리
    if (responseData.jwtError && [403, 415, 500].includes(response.status)) {
        await handleLogout(loginType);
        return;
    }

    // Access Token이 만료되었을 경우 -> 재발행 요청
    if (responseData.jwtError && response.status === 401) {
        console.warn("로그인이 만료되었습니다.");

        // 사용자에게 로그인 연장 여부를 묻는 다이얼로그 표시
        var extendSession = confirm("세션이 만료되었습니다. 로그인 세션을 연장하시겠습니까?");

        if (extendSession) {
            var newTokenResponse = await refreshAccessToken(accessToken);
            if (newTokenResponse.success) {
                localStorage.setItem("access", newTokenResponse.accessToken); // 새 토큰 저장
                return fetchWithAccessToken(url, options, loginType); // 기존 요청 다시 실행
            } else {
                handleLogout(loginType); // Refresh Token도 만료된 경우 로그아웃 처리
            }
        } else {
            handleLogout(loginType); // 사용자가 로그인 연장을 원하지 않음 → 로그아웃
        }
    }

    return response;
}

// JWT 관련 에러 처리 함수
async function handleJwtError(status, loginType) {
    alert("잘못된 로그인 정보입니다. 다시 로그인해주세요.");

    await handleLogout(loginType);
}

// Refresh Token을 이용한 Access Token 재발급 요청
async function refreshAccessToken(accessToken) {
    try {
        var response = await fetch("/reissue", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "access": accessToken
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

// 로그아웃 처리 및 리디렉트
async function handleLogout(loginType) {
    alert("로그인이 필요합니다.");
    localStorage.removeItem("access");

    await fetch("/logout", { method: "POST", credentials: "include" });

    redirectToLogin(loginType);
}

// 로그인 페이지로 이동
function redirectToLogin(loginType) {
    var loginUrl = loginType === "admin" ? "/login/admin" : "/login/member";
    window.location.href = loginUrl;
}