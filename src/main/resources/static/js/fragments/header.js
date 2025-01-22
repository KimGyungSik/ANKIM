document.addEventListener("DOMContentLoaded", async () => {
    var mypageButton = document.getElementById("mypageButton");
    var cartButton = document.getElementById("cartButton");
    var loginButton = document.getElementById("loginButton");
    var logoutButton = document.getElementById("logoutButton");

    // 로그인 상태 확인 API 호출
    var isLoggedIn = await checkLoginStatus();

    if (isLoggedIn) {
        loginButton.style.display = "none";
        logoutButton.style.display = "inline-block";
    } else {
        loginButton.style.display = "inline-block";
        logoutButton.style.display = "none";
    }

    // 마이페이지 버튼 클릭 이벤트
    mypageButton.addEventListener("click", (event) => {
        event.preventDefault();
        window.location.href = "/mypage";
    });

    // 장바구니 버튼 클릭 이벤트
    cartButton.addEventListener("click", (event) => {
        event.preventDefault();
        window.location.href = "/cart";
    });

    // 로그아웃 버튼 클릭 이벤트
    logoutButton.addEventListener("click", async (event) => {
        event.preventDefault();
        await handleLogout();
    });
});

// 로그인 상태 확인 함수
async function checkLoginStatus() {
    try {
        var response = await fetch("/api/auth/status", { credentials: "include" });
        var responseData = await response.json();
        return responseData.data === true; // API 응답의 `data` 값이 true인지 확인
    } catch (error) {
        console.error("로그인 상태 확인 실패:", error);
        return false;
    }
}

// 로그아웃 처리 함수
async function handleLogout() {
    try {
        var accessToken = localStorage.getItem("access");

        const response = await fetch("/logout", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "access": accessToken // access 토큰 추가
            },
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error("로그아웃 요청 실패: " + response.status);
        }

        // 응답 데이터를 JSON으로 변환
        const responseData = await response.json();
        console.log("로그아웃 응답:", responseData);

        // 로컬스토리지에서 access 토큰 제거
        localStorage.removeItem("access");

        // 쿠키에서 refresh 토큰이 제거되었는지 확인
        setTimeout(() => {
            var cookies = document.cookie.split(";");
            var hasRefreshToken = cookies.some(cookie => cookie.trim().startsWith("refresh="));

            if (!hasRefreshToken) {
                console.log("로그아웃 성공, 페이지 새로고침");
                location.reload(); // 로그아웃이 정상 처리되면 페이지 새로고침
            } else {
                console.warn("로그아웃 실패: refresh 쿠키가 아직 남아 있음");
            }
        }, 500);
    } catch (error) {
        console.error("로그아웃 실패:", error);
    }
}