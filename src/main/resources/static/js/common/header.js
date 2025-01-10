document.addEventListener("DOMContentLoaded", () => {
    const cartButton = document.getElementById("cartButton");

    cartButton.addEventListener("click", (event) => {
        event.preventDefault();

        // localStorage에서 accessToken 가져오기
        const accessToken = localStorage.getItem("access");

        if (!accessToken) {
            alert("로그인이 필요합니다."); // 토큰 없을 경우 처리
            return;
        }

        // access 토큰을 헤더에 포함하여 요청
        fetch("/cart", {
            method: "GET",
            headers: {
                access: `${accessToken}`,
            },
            credentials: "include"
        })
            .then(response => response.text())
            .then(html => {
                document.open();
                document.write(html);
                document.close();
            });
    });
});


// // 공통함수화 하려고 fetchWithAccessToken 함수를 만들었지만, 사용하지 않았습니다.
// import { fetchWithAccessToken } from '../utils/fetchUtils.js';
//
// document.addEventListener("DOMContentLoaded", () => {
//     const cartButton = document.getElementById("cartButton");
//
//     cartButton.addEventListener("click", (event) => {
//         event.preventDefault();
//
//         fetchWithAccessToken("/cart", { method: "GET" })
//             .then(response => response.text()) // 백엔드에서 HTML을 반환한다고 가정
//             .then(html => {
//                 document.open();
//                 document.write(html);
//                 document.close();
//             })
//             .catch(error => {
//                 console.error("장바구니 요청 중 오류 발생:", error);
//                 alert("장바구니 접근에 실패했습니다.");
//             });
//     });
// });