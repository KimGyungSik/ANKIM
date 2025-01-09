document.addEventListener("DOMContentLoaded", () => {
    const cartItemsContainer = document.getElementById("cartItemsContainer");
    const checkoutButton = document.querySelector(".checkout-button");

    // API 호출을 통해 장바구니 데이터 가져오기
    fetch("/api/cart")
        .then(response => response.json())
        .then(data => {
            if (data.code === 200 && data.data) {
                renderCartItems(data.data);
            } else {
                alert("장바구니 데이터를 불러오는데 실패했습니다.");
            }
        })
        .catch(error => {
            console.error("장바구니 불러오기 오류:", error);
        });

    // 장바구니 항목 렌더링
    function renderCartItems(items) {
        if (items.length === 0) {
            cartItemsContainer.innerHTML = "<tr><td colspan='5'>장바구니가 비어 있습니다.</td></tr>";
            checkoutButton.disabled = true;
            return;
        }

        items.forEach(item => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td><input type="checkbox" class="select-item"></td>
                <td>
                    <img src="${item.thumbNailImgUrl}" alt="${item.productName}" class="thumbnail">
                    <div>
                        <p>${item.productName}</p>
                        <p>${item.itemName}</p>
                    </div>
                </td>
                <td>
                    <button class="decrease-qty" data-id="${item.cartItemNo}">-</button>
                    <span>${item.qty}</span>
                    <button class="increase-qty" data-id="${item.cartItemNo}">+</button>
                </td>
                <td>${item.totalPrice.toLocaleString()}원</td>
                <td>${item.freeShip === "Y" ? "무료" : `${item.shipFee.toLocaleString()}원`}</td>
            `;
            cartItemsContainer.appendChild(row);
        });

        updateCheckoutButtonState();
    }

    // 구매 버튼 활성화 상태 업데이트
    function updateCheckoutButtonState() {
        const selectedItems = document.querySelectorAll(".select-item:checked");
        checkoutButton.disabled = selectedItems.length === 0;
    }

    // 이벤트 바인딩
    cartItemsContainer.addEventListener("change", updateCheckoutButtonState);
});