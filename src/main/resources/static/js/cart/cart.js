import { fetchWithAccessToken } from '../utils/fetchUtils.js';

document.addEventListener("DOMContentLoaded", async () => {
    window.closeModal = function () {
        var modal = document.querySelector('.modal');
        modal.style.display = "none";
    };

    // ESC 키로도 모달 닫기 기능 추가
    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
            closeModal();
        }
    });

    var cartItemsContainer = document.getElementById("cartItemsContainer");
    var checkoutButton = document.querySelector(".checkout-button");

    try {
        var response = await fetchWithAccessToken("/api/cart", { method: "GET" });
        var data = await response.json();

        if (data.code === 200 && data.data) {
            renderCartItems(data.data);
        } else {
            showModal(data.message || "장바구니 데이터를 불러오는데 실패했습니다.");
        }
    } catch (error) {
        showModal(errer.message || "장바구니 데이터를 가져오는 중 오류가 발생했습니다.");
    }

    // 선택 상품 총 결제정보 업데이트 함수
    function renderCartSummary(items) {
        var selectedItems = document.querySelectorAll(".select-item:checked:not(:disabled)"); // 선택된 상품들 중 활성화된 항목만
        var cartSummaryContainer = document.getElementById("cartSummaryContainer");
        var totalOrderAmountElement = document.getElementById("totalOrderAmount");
        var totalShippingFeeElement = document.getElementById("totalShippingFee");
        var totalPaymentAmountElement = document.getElementById("totalPaymentAmount");

        // 초기화
        let totalOrderAmount = 0;
        let totalShippingFee = 0;
        let totalItemsCount = 0;

        if (selectedItems.length === 0) {
            // 선택된 상품이 없을 때 요약 정보 초기화
            totalOrderAmountElement.textContent = `0원`;
            totalShippingFeeElement.textContent = `0원`;
            totalPaymentAmountElement.textContent = `0원`;
        } else {
            // 선택된 항목에서 금액, 배송비, 개수 계산
            selectedItems.forEach((checkbox) => {
                var cartItem = checkbox.closest(".cart-item");
                var itemPrice = parseInt(cartItem.querySelector(".item-price").textContent.replace(/[^0-9]/g, ""), 10) || 0; // 주문 금액
                var itemShippingFee = parseInt(cartItem.querySelector(".item-shipping").textContent.replace(/[^0-9]/g, ""), 10) || 0; // 배송비
                var itemQty = parseInt(cartItem.querySelector(".qty-input").value, 10) || 0; // 수량

                totalOrderAmount += itemPrice; // 가격 * 수량
                totalShippingFee += itemShippingFee;
                totalItemsCount += itemQty;
            });

            var totalPaymentAmount = totalOrderAmount + totalShippingFee;

            // 값 업데이트
            totalOrderAmountElement.textContent = `${totalOrderAmount.toLocaleString()}원`;
            totalShippingFeeElement.textContent = `${totalShippingFee.toLocaleString()}원`;
            totalPaymentAmountElement.textContent = `${totalPaymentAmount.toLocaleString()}원`;
        }

        // 요약 컨테이너 항상 표시
        cartSummaryContainer.classList.remove("hidden");
    }

    // 장바구니 항목 렌더링 함수
    function renderCartItems(items) {
        cartItemsContainer.innerHTML = ""; // 기존 항목 초기화

        if (items.length === 0) {
            cartItemsContainer.innerHTML = `
        <div class="empty-cart-message">장바구니가 비어 있습니다.</div>
        `;
            checkoutButton.disabled = true;
            renderCartSummary([]); // 요약 정보 숨김
            return;
        }

        items.forEach((item) => {
            var unitPrice = parseInt(item.totalPrice || 0, 10); // 단가 가져오기
            var calculatedTotalPrice = unitPrice * parseInt(item.qty || 1, 10); // 수량에 따른 총 가격 계산

            var isSoldOut = item.itemQty === 0; // 품절 여부 확인

            var cartItem = document.createElement("div");
            cartItem.className = "cart-item";

            cartItem.innerHTML = `
        <div class="item-checkbox">
            <input type="checkbox" class="select-item" ${isSoldOut ? "disabled" : ""}>
        </div>
        <div class="item-info">
            <img src="${item.thumbNailImgUrl}" alt="${item.productName}" class="item-thumbnail">
            <div class="item-details">
                <p class="product-name">${item.productName}</p>
                <p class="item-option">옵션: ${item.itemName}</p>
            </div>
        </div>
        <div class="item-qty">
            <div class="quantity-control">
                <button class="decrease-qty" type="button" data-id="${item.cartItemNo}" ${isSoldOut ? "disabled" : ""}>-</button>
                <input class="qty-input" type="text" value="${item.qty}" inputmode="numeric" data-default="${item.qty}" ${isSoldOut ? "disabled" : ""}>
                <button class="increase-qty" type="button" data-id="${item.cartItemNo}" ${isSoldOut ? "disabled" : ""}>+</button>
            </div>
        </div>
        <div class="item-price" data-unit-price="${unitPrice}">
            ${isSoldOut ? "SOLD OUT" : `${calculatedTotalPrice.toLocaleString()}원`}
        </div>
        <div class="item-shipping">
            ${item.freeShip === "Y" ? "조건무료" : `${item.shipFee.toLocaleString()}원`}
        </div>
    `;
            if (isSoldOut) {
                cartItem.classList.add("sold-out");
            }
            cartItemsContainer.appendChild(cartItem);
        });

        addCartActionsContainer(); // 장바구니 상품 컨테이너 동적 추가
        updateCheckoutButtonState();
        bindQuantityChangeEvents();
        renderCartSummary(items); // 선택 상품 총 결제정보 업데이트
        bindSelectAllEvent(); // 전체상품 선택버튼 바인딩(selectAll)
    }

    function bindSelectAllEvent() {
        var selectAllCheckbox = document.getElementById("selectAll");
        var itemCheckboxes = document.querySelectorAll(".select-item:not(:disabled)"); // 비활성화된 체크박스 제외

        if (!selectAllCheckbox) return;

        // Select All 클릭 이벤트
        selectAllCheckbox.addEventListener("change", () => {
            var isChecked = selectAllCheckbox.checked;
            itemCheckboxes.forEach((checkbox) => {
                checkbox.checked = isChecked;
            });
            renderCartSummary(); // 선택 상품 총 결제정보 업데이트
            updateCheckoutButtonState(); // checkoutButton 활성화
        });

        // 개별 체크박스 변경 시 Select All 상태 변경
        itemCheckboxes.forEach((checkbox) => {
            checkbox.addEventListener("change", () => {
                var allChecked = Array.from(itemCheckboxes).every((cb) => cb.checked); // 모든 체크박스가 선택되었는지 확인
                selectAllCheckbox.checked = allChecked; // 모든 체크박스가 선택된 경우에만 전체 선택 체크
                renderCartSummary(); // 선택 상품 총 결제정보 업데이트
                updateCheckoutButtonState(); // checkoutButton 활성화
            });
        });
    }

    // 장바구니 액션 컨테이너 동적 추가 함수
    function addCartActionsContainer() {
        let cartActionsContainer = document.getElementById("cartActionsContainer");

        // 이미 추가되어 있다면 중복 추가 방지
        if (cartActionsContainer) return;

        cartActionsContainer = document.createElement("div");
        cartActionsContainer.id = "cartActionsContainer";
        cartActionsContainer.className = "cart-actions-container";

        cartActionsContainer.innerHTML = `
    <div class="actions-buttons">
        <button class="delete-selected-button">선택상품 삭제</button>
        <button class="delete-sold-out-button">품절상품 삭제</button>
    </div>
    <div class="cart-info">
        장바구니는 최대 100개의 상품을 담을 수 있습니다.
    </div>
    `;

        // `cart-table` 아래에 추가
        var cartTable = document.querySelector(".cart-table");
        cartTable.insertAdjacentElement("afterend", cartActionsContainer);

        // 버튼 이벤트 리스너 추가
        var deleteSelectedButton = cartActionsContainer.querySelector(".delete-selected-button");
        var deleteSoldOutButton = cartActionsContainer.querySelector(".delete-sold-out-button");

        // 선택 상품 삭제 요청
        deleteSelectedButton.addEventListener("click", async () => {
            var selectedItems = document.querySelectorAll(".select-item:checked");
            var cartItemNos = Array.from(selectedItems).map((checkbox) => {
                var cartItem = checkbox.closest(".cart-item");
                return cartItem.querySelector(".decrease-qty").dataset.id;
            });

            if (cartItemNos.length === 0) {
                showModal("선택된 상품이 없습니다.");
                return;
            }

            try {
                var response = await fetchWithAccessToken("/api/cart/items/selected", {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(cartItemNos),
                });

                var data = await response.json();
                showModal(data.data || "선택된 상품이 삭제되었습니다.");

                // 선택된 항목 제거
                selectedItems.forEach((checkbox) => {
                    var cartItem = checkbox.closest(".cart-item");
                    cartItem.remove();
                });

                renderCartSummary(); // 선택 항목 변경 시 선택 상품 총 결제정보 업데이트
            } catch (error) {
                showModal(error.message || "선택된 상품 삭제 중 오류가 발생했습니다.");
            }
        });

        deleteSoldOutButton.addEventListener("click", async () => {
            try {
                var response = await fetchWithAccessToken("/api/cart/items/sold-out", {
                    method: "DELETE",
                });

                var data = await response.json();
                showModal(data.data || "품절 상품이 삭제되었습니다.");

                // 품절 상품 제거
                var soldOutItems = document.querySelectorAll(".cart-item.sold-out");
                soldOutItems.forEach((item) => item.remove());

                renderCartSummary(); // 선택 항목 변경 시 선택 상품 총 결제정보 업데이트
            } catch (error) {
                showModal(error.message || "품절 상품 삭제 중 오류가 발생했습니다.");
            }
        });
    }

    function showModal(message) {
        var modal = document.querySelector('.modal');
        var modalBody = modal.querySelector('.modal-body');
        modalBody.textContent = message;
        modal.style.display = "flex";
    }

    // 체크박스 변경 이벤트
    cartItemsContainer.addEventListener("change", (event) => {
        if (event.target.classList.contains("select-item")) {
            renderCartSummary(); // 선택 항목 변경 시 선택 상품 총 결제정보 업데이트
        }
    });

    function updateCheckoutButtonState() {
        var selectedItems = document.querySelectorAll(".select-item:checked");
        checkoutButton.disabled = selectedItems.length === 0;
    }

    cartItemsContainer.addEventListener("change", updateCheckoutButtonState);

    function bindQuantityChangeEvents() {
        var decreaseButtons = document.querySelectorAll(".decrease-qty");
        var increaseButtons = document.querySelectorAll(".increase-qty");
        var qtyInputs = document.querySelectorAll(".qty-input");

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
        var cartItemNo = event.target.dataset.id;
        var qtyInput = event.target.nextElementSibling;
        var cartItem = qtyInput.closest(".cart-item");
        var itemPriceElement = cartItem.querySelector(".item-price");
        var unitPrice = parseInt(itemPriceElement.dataset.unitPrice, 10); // 개별 단가
        let currentQty = parseInt(qtyInput.value);

        if (currentQty > 1) {
            currentQty -= 1;
            qtyInput.value = currentQty;


            // 총 가격 업데이트
            var newTotalPrice = unitPrice * currentQty;
            itemPriceElement.textContent = `${newTotalPrice.toLocaleString()}원`;

            var isUpdated = await updateCartItemQuantity(cartItemNo, currentQty, qtyInput);

            if (isUpdated) {
                // 성공적으로 업데이트되었을 경우만 data-default를 변경
                qtyInput.dataset.default = currentQty;
            } else {
                // 실패 시 복구
                qtyInput.value = previousQty;
                itemPriceElement.textContent = `${(unitPrice * previousQty).toLocaleString()}원`;
            }
            renderCartSummary(); // 선택 상품 총 결제정보 업데이트
        }
    }

    async function handleQuantityIncrease(event) {
        var cartItemNo = event.target.dataset.id;
        var qtyInput = event.target.previousElementSibling;
        var cartItem = qtyInput.closest(".cart-item");
        var itemPriceElement = cartItem.querySelector(".item-price");
        var unitPrice = parseInt(itemPriceElement.dataset.unitPrice, 10); // 개별 단가
        let currentQty = parseInt(qtyInput.value);

        currentQty += 1;
        qtyInput.value = currentQty;

        // 총 가격 업데이트
        var newTotalPrice = unitPrice * currentQty;
        itemPriceElement.textContent = `${newTotalPrice.toLocaleString()}원`;

        var isUpdated = await updateCartItemQuantity(cartItemNo, currentQty, qtyInput);

        if (isUpdated) {
            // 성공적으로 업데이트되었을 경우만 data-default를 변경
            qtyInput.dataset.default = currentQty;
        } else {
            // 실패 시 복구
            qtyInput.value = previousQty;
            itemPriceElement.textContent = `${(unitPrice * previousQty).toLocaleString()}원`;
        }
        renderCartSummary(); // 선택 상품 총 결제정보 업데이트
    }

    async function handleQuantityInputChange(event) {
        var qtyInput = event.target; // 현재 입력 필드
        var cartItem = qtyInput.closest(".cart-item");
        var cartItemNo = cartItem.querySelector(".decrease-qty").dataset.id;
        var itemPriceElement = cartItem.querySelector(".item-price");
        var unitPrice = parseInt(itemPriceElement.dataset.unitPrice, 10); // 개별 단가
        var previousQty = parseInt(qtyInput.dataset.default); // 이전 수량 저장
        let currentQty = parseInt(qtyInput.value); // 현재 입력된 값

        // 입력값 검증: 잘못된 값 입력 시 경고 및 복구
        if (isNaN(currentQty) || currentQty < 1) {
            showModal("수량은 1개 이상이어야 합니다.");
            qtyInput.value = previousQty; // 이전 값으로 복구
            return;
        }

        // 총 가격 업데이트
        var newTotalPrice = unitPrice * currentQty;
        itemPriceElement.textContent = `${newTotalPrice.toLocaleString()}원`;

        var isUpdated = await updateCartItemQuantity(cartItemNo, currentQty, qtyInput);

        if (isUpdated) {
            // 성공적으로 업데이트되었을 경우만 data-default를 변경
            qtyInput.dataset.default = currentQty;
        } else {
            // 실패 시 복구
            qtyInput.value = previousQty;
            itemPriceElement.textContent = `${(unitPrice * previousQty).toLocaleString()}원`;
        }
        renderCartSummary(); // 선택 상품 총 결제정보 업데이트
    }


    function debounce(func, delay) {
        let timeout;
        return function (...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), delay);
        };
    }

    async function updateCartItemQuantity(cartItemNo, qty, qtyInput) {
        var previousQty = parseInt(qtyInput.dataset.default); // 이전 수량 저장
        try {
            var response = await fetchWithAccessToken(
                `/api/cart/items/${cartItemNo}?qty=${qty}`,
                { method: "PATCH" }
            );
            var data = await response.json();

            if (data.code === 200) {
                return true; // 성공
            } else {
                throw new Error(data.message);
            }

        } catch (error) {
            showModal(error.message);
            return false; // 실패
        }
    }

    checkoutButton.addEventListener("click", async () => {
        var selectedItems = document.querySelectorAll(".select-item:checked");

        if (selectedItems.length === 0) {
            showModal("주문할 상품을 선택해주세요.");
            return;
        }

        // 선택된 상품의 cartItemNo 리스트 생성
        var cartItemNoList = Array.from(selectedItems).map((checkbox) => {
            var cartItem = checkbox.closest(".cart-item");
            return parseInt(cartItem.querySelector(".decrease-qty").dataset.id, 10);
        });

        try {
            var response = await fetchWithAccessToken("/api/temp-order", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(cartItemNoList),
            });

            var data = await response.json();

            if (data.code === 200 && data.data) {
                alert("임시 주문 생성 성공"); // FIXME 주문페이지로 랜더링 해야됨!!!
                // 주문 생성 성공 -> /order 페이지로 이동
                // window.location.href = "/order";
            } else {
                showModal(data.message || "주문 요청 중 오류가 발생했습니다.");
            }
        } catch (error) {
            showModal(error.message || "주문 요청을 보내는 중 오류가 발생했습니다.");
        }
    });
});