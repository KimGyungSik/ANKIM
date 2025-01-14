import { fetchWithAccessToken } from '../utils/fetchUtils.js';

document.addEventListener("DOMContentLoaded", async () => {
    const cartItemsContainer = document.getElementById("cartItemsContainer");
    const checkoutButton = document.querySelector(".checkout-button");

    try {
        const response = await fetchWithAccessToken("/api/cart", { method: "GET" });
        const data = await response.json();

        if (data.code === 200 && data.data) {
            renderCartItems(data.data);
        } else {
            alert("장바구니 데이터를 불러오는데 실패했습니다.");
        }
    } catch (error) {
        console.error("장바구니 불러오기 오류:", error);
        alert("장바구니 데이터를 가져오는 중 오류가 발생했습니다.");
    }

    function renderCartItems(items) {
        cartItemsContainer.innerHTML = ""; // 기존 항목 초기화

        if (items.length === 0) {
            cartItemsContainer.innerHTML = `
            <div class="empty-cart-message">장바구니가 비어 있습니다.</div>
        `;
            checkoutButton.disabled = true;
            return;
        }

        items.forEach((item) => {
            const cartItem = document.createElement("div");
            cartItem.className = "cart-item";

            cartItem.innerHTML = `
            <div class="item-checkbox">
                <input type="checkbox" class="select-item">
            </div>
            <div class="item-info">
                <img src="${item.thumbNailImgUrl}" alt="${item.productName}" class="item-thumbnail">
                <div class="item-details">
                    <p class="product-name">${item.productName}</p>
                    <p class="item-option">옵션: 색상: ${item.color}, 사이즈: ${item.size}</p>
                </div>
            </div>
            <div class="item-qty">
                <div class="quantity-control">
                    <button class="decrease-qty" type="button" data-id="${item.cartItemNo}">-</button>
                    <input class="qty-input" type="text" value="${item.qty}" inputmode="numeric" data-default="${item.qty}">
                    <button class="increase-qty" type="button" data-id="${item.cartItemNo}">+</button>
                </div>
            </div>
            <div class="item-price">${item.totalPrice.toLocaleString()}원</div>
            <div class="item-shipping">${item.freeShip === "Y" ? "조건무료" : `${item.shipFee.toLocaleString()}원`}</div>
        `;
            cartItemsContainer.appendChild(cartItem);
        });

        updateCheckoutButtonState();
        bindQuantityChangeEvents();
    }

    function updateCheckoutButtonState() {
        const selectedItems = document.querySelectorAll(".select-item:checked");
        checkoutButton.disabled = selectedItems.length === 0;
    }

    cartItemsContainer.addEventListener("change", updateCheckoutButtonState);

    function bindQuantityChangeEvents() {
        const decreaseButtons = document.querySelectorAll(".decrease-qty");
        const increaseButtons = document.querySelectorAll(".increase-qty");
        const qtyInputs = document.querySelectorAll(".qty-input");

        decreaseButtons.forEach((button) =>
            button.addEventListener("click", handleQuantityDecrease)
        );
        increaseButtons.forEach((button) =>
            button.addEventListener("click", handleQuantityIncrease)
        );
        qtyInputs.forEach((input) =>
            input.addEventListener("input", debounce(handleQuantityInputChange, 500)) // 디바운스 적용
        );
    }

    async function handleQuantityDecrease(event) {
        const cartItemNo = event.target.dataset.id;
        const qtyInput = event.target.nextElementSibling;
        let currentQty = parseInt(qtyInput.value);

        if (currentQty > 1) {
            currentQty -= 1;
            qtyInput.value = currentQty;
            await updateCartItemQuantity(cartItemNo, currentQty, qtyInput);
        }
    }

    async function handleQuantityIncrease(event) {
        const cartItemNo = event.target.dataset.id;
        const qtyInput = event.target.previousElementSibling;
        let currentQty = parseInt(qtyInput.value);

        currentQty += 1;
        qtyInput.value = currentQty;
        await updateCartItemQuantity(cartItemNo, currentQty, qtyInput);
    }

    async function handleQuantityInputChange(event) {
        const qtyInput = event.target; // 현재 입력 필드
        const cartItemNo = qtyInput.closest(".quantity-control").querySelector(".decrease-qty").dataset.id;
        const previousQty = parseInt(qtyInput.dataset.default); // 이전 수량 저장
        let currentQty = parseInt(qtyInput.value); // 현재 입력된 값

        // 입력값 검증: 잘못된 값 입력 시 경고 및 복구
        if (isNaN(currentQty) || currentQty < 1) {
            alert("수량은 1 이상이어야 합니다.");
            qtyInput.value = previousQty; // 이전 값으로 복구
            return;
        }

        await updateCartItemQuantity(cartItemNo, currentQty, qtyInput);

    }


    function debounce(func, delay) {
        let timeout;
        return function (...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), delay);
        };
    }

    async function updateCartItemQuantity(cartItemNo, qty, qtyInput) {
        const previousQty = parseInt(qtyInput.dataset.default); // 이전 수량 저장
        try {
            const response = await fetchWithAccessToken(
                `/api/cart/items/${cartItemNo}?qty=${qty}`,
                { method: "PATCH" }
            );
            const data = await response.json();

        } catch (error) {
            alert(error.message);
            qtyInput.value = previousQty; // 이전 값으로 복구
        }
    }
});