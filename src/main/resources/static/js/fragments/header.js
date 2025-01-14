document.addEventListener("DOMContentLoaded", () => {
    var cartButton = document.getElementById("cartButton");

    cartButton.addEventListener("click", (event) => {
        event.preventDefault();

        // 장바구니 페이지로 바로 이동
        window.location.href = "/cart";
    });
});