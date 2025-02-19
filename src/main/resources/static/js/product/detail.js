import { fetchWithAccessToken } from '../utils/fetchUtils.js';


document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ Script Loaded!");

    let selectedOptions = {}; // 선택한 옵션값 저장
    let selectedItems = []; // 최종 선택된 아이템 리스트
    let filteredItems = []; // ✅ 첫 번째 옵션 선택 후, 가능한 아이템들 저장
    const dropdowns = document.querySelectorAll(".custom-dropdown");
    const selectedOptionsContainer = document.getElementById("selected-options");

    console.log("🟢 찾은 드롭다운 개수:", dropdowns.length);
    if (dropdowns.length === 0) {
    console.error("⚠️ 드롭다운 요소가 없음! HTML 확인 필요.");
    return;
}

    dropdowns.forEach((dropdown, index) => {
    const selected = dropdown.querySelector(".dropdown-selected");
    const optionsList = dropdown.querySelector(".dropdown-options");
    const options = optionsList.querySelectorAll(".dropdown-item");

    console.log(`📌 드롭다운 ${index} 설정 중...`, dropdown);

    if (index > 0) dropdown.classList.add("disabled"); // 첫 번째 옵션 그룹만 활성화

    selected.addEventListener("click", () => {
    if (!dropdown.classList.contains("disabled")) {
    dropdown.classList.toggle("active");
    optionsList.style.display = "block";
}
});

    options.forEach(option => {
    option.addEventListener("click", function () {
    const selectedValue = this.dataset.value;
    const selectedText = this.innerText.trim();
    console.log(`🟢 선택한 옵션 ${index}: ${selectedText}`);

    selectedOptions[index] = selectedValue; // ✅ 선택한 옵션을 저장
    console.log("🟢 현재 선택된 옵션들:", selectedOptions);

    selected.innerHTML = `<span>${selectedText}</span> <span class="dropdown-arrow">∨</span>`;
    dropdown.classList.remove("active");
    optionsList.style.display = "none";

    // ✅ 첫 번째 옵션 선택 후 가능한 아이템 리스트 필터링
    if (index === 0) {
    filterItemsByFirstOption(selectedValue);
}

    // ✅ 다음 드롭다운에서 가능한 옵션만 필터링
    filterNextDropdown(index);

    if (Object.keys(selectedOptions).length === dropdowns.length) {
    addSelectedItem();
    setTimeout(resetDropdowns, 50); // 🔥 DOM 업데이트 지연 적용
}
});
});

    document.addEventListener("click", (e) => {
    if (!dropdown.contains(e.target)) {
    dropdown.classList.remove("active");
    optionsList.style.display = "none";
}
});
});

    function filterItemsByFirstOption(selectedValue) {
    console.log(`🔍 [필터링] 첫 번째 옵션 선택: ${selectedValue}`);

    // ✅ 첫 번째 옵션(예: 사이즈)을 포함하는 아이템 필터링
    filteredItems = [];

    if (optionItemMap[selectedValue]) {
    filteredItems = optionItemMap[selectedValue];
}

    console.log("🟢 필터링된 아이템 리스트:", filteredItems);
}

    function filterNextDropdown(currentIndex) {
    const nextDropdown = dropdowns[currentIndex + 1];
    if (!nextDropdown) return;

    const selectedValues = Object.values(selectedOptions).map(val => val.toString());
    console.log("🔍 현재 선택된 옵션:", selectedValues);

    let possibleOptions = new Set();
    let priceMap = {}; // ✅ 추가금액을 저장할 객체

    // ✅ 필터링된 아이템 리스트에서 가능한 옵션만 추출하고 `addPrice` 저장
    filteredItems.forEach(item => {
    item.optionValues.forEach(opt => {
    if (!selectedValues.includes(opt.optionValueNo.toString())) {
    possibleOptions.add(opt.optionValueNo.toString()); // ✅ 선택한 옵션 제외하고 추가
    priceMap[opt.optionValueNo] = item.addPrice; // ✅ 해당 옵션의 추가금액 저장
}
});
});

    console.log("🟢 가능한 다음 옵션 값:", possibleOptions);
    console.log("🟢 옵션별 추가금액 맵:", priceMap);

    // ✅ 다음 옵션 목록에서 불가능한 옵션 숨김 처리 및 추가금액 표시
    const nextOptionsList = nextDropdown.querySelector(".dropdown-options");
    const nextOptions = nextOptionsList.querySelectorAll(".dropdown-item");

    nextOptions.forEach(option => {
    const optionValue = option.dataset.value;
    if (possibleOptions.has(optionValue)) {
    option.style.display = "block"; // 가능한 옵션만 표시

    // ✅ 추가금액을 표시 (해당 옵션의 `addPrice` 적용)
    let extraPriceSpan = option.querySelector(".extra-price");
    if (!extraPriceSpan) {
    extraPriceSpan = document.createElement("span");
    extraPriceSpan.classList.add("extra-price");
    option.appendChild(extraPriceSpan);
}
    extraPriceSpan.textContent = priceMap[optionValue] ? `(+${priceMap[optionValue].toLocaleString()}원)` : "";
} else {
    option.style.display = "none"; // 불가능한 옵션 숨김
}
});

    nextDropdown.classList.remove("disabled");
}

    function findMatchingItem() {
    let selectedValues = Object.values(selectedOptions).map(val => val.toString());
    console.log("🔍 매칭을 위한 선택된 옵션값:", selectedValues);

    let matchedItem = null;

    // ✅ 필터링된 아이템 중에서 정확한 옵션 조합을 가진 아이템 찾기
    filteredItems.forEach(item => {
    let itemOptions = item.optionValues.map(opt => opt.optionValueNo.toString());

    if (selectedValues.length === itemOptions.length && selectedValues.every(val => itemOptions.includes(val))) {
    matchedItem = item;
}
});

    if (!matchedItem) {
    console.warn("⚠️ 매칭되는 아이템이 없습니다.");
} else {
    console.log("✅ 정확하게 매칭된 아이템:", matchedItem);
}

    return matchedItem;
}


    function renderSelectedOptions() {
    selectedOptionsContainer.innerHTML = selectedItems.map((item, index, array) => `
        <div class="selected-option-item" data-item-id="${item.itemId}">
            <div class="item-info">
                <span style="font-size: 15px; font-weight: 550;">${item.text}</span>
                <div class="quantity-controls">
                    <button class="quantity-decrease" data-item-id="${item.itemId}">−</button>
                    <span class="quantity">${item.quantity}</span>
                    <button class="quantity-increase" data-item-id="${item.itemId}">+</button>
                </div>
                <span class="option-price" style="font-size: 15px; font-weight: 550;">
                    ${(item.price * item.quantity).toLocaleString()}원
                </span>
                <button class="remove-option" data-item-id="${item.itemId}">✖</button>
            </div>
        </div>
    `).join("");

    updateTotalPrice(); // ✅ 총 상품 금액 업데이트

    // ✅ 수량 조절 이벤트 추가
    document.querySelectorAll(".quantity-increase").forEach(button => {
    button.addEventListener("click", function () {
    let itemId = this.dataset.itemId;
    let item = selectedItems.find(item => item.itemId == itemId);
    if (item) {
    item.quantity++;
    renderSelectedOptions();
}
});
});

    document.querySelectorAll(".quantity-decrease").forEach(button => {
    button.addEventListener("click", function () {
    let itemId = this.dataset.itemId;
    let item = selectedItems.find(item => item.itemId == itemId);
    if (item && item.quantity > 1) {
    item.quantity--;
    renderSelectedOptions();
}
});
});

    document.querySelectorAll(".remove-option").forEach(button => {
    button.addEventListener("click", function () {
    let itemId = this.dataset.itemId;
    selectedItems = selectedItems.filter(item => item.itemId != itemId);
    renderSelectedOptions();
});
});
}



    function updateTotalPrice() {
    const totalPriceContainer = document.getElementById("total-price-container");
    let totalPrice = selectedItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);

    if (selectedItems.length > 0) {
    totalPriceContainer.innerHTML = `
            <div class="total-price">
                총 상품 금액 <span class="price">${totalPrice.toLocaleString()}</span><span class="price2">원</span>
            </div>
        `;
} else {
    totalPriceContainer.innerHTML = ''; // ✅ 선택된 아이템이 없으면 총 상품 금액 숨김
}
}


    function resetDropdowns() {
    console.log("🔄 [초기화] 옵션 선택 가능 상태 초기화");

    selectedOptions = {}; // 선택한 옵션 초기화
    filteredItems = []; // ✅ 필터링된 아이템 리스트도 초기화

    dropdowns.forEach((dropdown, index) => {
    dropdown.classList.add("disabled");

    const selected = dropdown.querySelector(".dropdown-selected");
    let originalLabel = selected.getAttribute("data-group-name") || "옵션 그룹"; // ✅ 저장된 그룹명 불러오기

    console.log(`🔄 옵션 ${index} 초기화 → ${originalLabel}`);

    selected.innerHTML = `
            <span class="group-label">${originalLabel}</span>
            <span class="dropdown-arrow">∨</span>
        `;

    const optionsList = dropdown.querySelector(".dropdown-options");
    optionsList.style.display = "none";
});

    dropdowns[0].classList.remove("disabled"); // ✅ 첫 번째 옵션 그룹만 다시 활성화
}

    // ✅ 모달 관련 요소
    const duplicateModal = document.getElementById("duplicate-option-modal");
    const closeModal = document.querySelector(".close-modal");
    const modalConfirm = document.getElementById("modal-confirm");

// ✅ 모달 닫기 이벤트
    closeModal.addEventListener("click", () => {
    duplicateModal.style.display = "none";
});
    modalConfirm.addEventListener("click", () => {
    duplicateModal.style.display = "none";
});

// ✅ 이미 선택된 아이템인지 확인하는 함수
    function isDuplicateItem(selectedTexts) {
    return selectedItems.some(item => item.text === selectedTexts);
}

// ✅ 선택한 아이템 추가 로직
    function addSelectedItem() {
    const selectedTexts = [];
    const selectedOptionValues = []; // ✅ 선택한 옵션값 번호 저장

    Object.keys(selectedOptions).forEach(index => {
    const dropdown = dropdowns[index];
    const selectedElement = dropdown.querySelector(".dropdown-selected span:first-child");
    if (selectedElement) {
    let optionText = selectedElement.innerText.trim();
    optionText = optionText.replace(/\(\+\s?[0-9,]+원\)/g, "").trim();
    selectedTexts.push(optionText);

    // ✅ 옵션 값 번호 저장
    selectedOptionValues.push(selectedOptions[index]);
}
});

    const formattedText = selectedTexts.join(" - ");
    console.log(`✅ 최종 선택된 옵션: ${formattedText}`);
    console.log(`✅ 최종 선택된 옵션 번호: ${selectedOptionValues}`);

    // ✅ 중복 확인 후 모달 띄우기
    if (isDuplicateItem(formattedText)) {
    duplicateModal.style.display = "block";
    return;
}

    let matchedItem = findMatchingItem();
    if (!matchedItem) {
    console.warn("⚠️ 매칭되는 아이템을 찾을 수 없습니다.");
    return;
}

    let totalPrice = productPrice + matchedItem.addPrice;

    selectedItems.push({
    text: formattedText,
    price: totalPrice,
    itemId: matchedItem.itemId,
    quantity: 1, // ✅ 기본 수량 1로 설정
    optionValueNoList: selectedOptionValues // ✅ 선택한 옵션값 번호 저장
});

    console.log("🛒 장바구니에 추가된 아이템:", selectedItems);
    renderSelectedOptions();
}


    const wishlistIcon = document.getElementById("wishlist");

    wishlistIcon.addEventListener("click", function () {
    this.classList.toggle("active");
});

    const cartButton = document.querySelector(".cart-button");

    cartButton.addEventListener("click", function () {
    if (selectedItems.length === 0) {
    alert("장바구니에 담을 상품을 선택해주세요.");
    return;
}

    // ✅ 장바구니에 담을 데이터 생성 (JSON 형식)
    const cartData = selectedItems.map(item => ({
    productNo: productNo, // ✅ 상품 번호
    optionValueNoList: item.optionValueNoList, // ✅ 선택한 옵션값들
    qty: item.quantity // ✅ 개별 수량
}));

    console.log("🛒 장바구니 담기 요청 데이터:", cartData);

    // ✅ API 요청 보내기
    fetchWithAccessToken("/api/cart/items", {
    method: "POST",
    headers: {
    "Content-Type": "application/json"
},
    body: JSON.stringify(cartData)
})
    .then(data => {
    if (data.code === 200 && data.status === "OK") {
    showCartModal(); // ✅ 장바구니 모달 표시
} else {
    console.error("장바구니 담기 실패:", data);
    alert(data.message || "장바구니 담기에 실패했습니다.");
}
})
    .catch(error => {
    console.error("장바구니 담기 오류:", error);
    alert(error);
    alert(error.data);
    alert(error.message);
});
});



    // ✅ 장바구니 담기 성공 시 모달창 띄우기
    function showCartModal() {
    const modalHtml = `
            <div class="modal" id="cart-modal">
                <span class="close-modal" onclick="closeCartModal()">✖</span>
                <div class="modal-content">장바구니에 상품이 담겼습니다.</div>
                <button id="modal-confirm">장바구니 바로가기</button>
            </div>
            <div class="modal-overlay" onclick="closeCartModal()"></div>
        `;
    document.body.insertAdjacentHTML("beforeend", modalHtml);
    document.getElementById("cart-modal").style.display = "block";

    document.getElementById("modal-confirm").addEventListener("click", () => {
    window.location.href = "/cart"; // ✅ 장바구니 페이지로 이동
});
}

    // ✅ 모달 닫기 함수
    window.closeCartModal = function () {
    document.getElementById("cart-modal")?.remove();
    document.querySelector(".modal-overlay")?.remove();
};
});