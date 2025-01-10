// export async function fetchWithAccessToken(url, options = {}) {
//     const accessToken = localStorage.getItem("access");
//     if (!accessToken) {
//         alert("로그인이 필요합니다.");
//         window.location.href = "/login/member";
//         return;
//     }
//
//     const headers = {
//         ...(options.headers || {}),
//         access: accessToken,
//     };
//
//     return fetch(url, {
//         ...options,
//         headers,
//     });
// }