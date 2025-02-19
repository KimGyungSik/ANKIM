// 중분류 선택 시 소분류 로드
    async function loadSubcategories(middleCategoryId) {
    const smallCategorySelect = document.querySelector("[name='smallCategoryNo']");
    smallCategorySelect.innerHTML = '<option value="">선택</option>'; // 초기화

    if (!middleCategoryId) return; // 중분류 선택이 없을 경우

    try {
    const response = await fetch(`/category/subcategories?middleCategoryId=${middleCategoryId}`);
    if (!response.ok) {
    await handleApiError(response);
    return;
}

    const data = await response.json();

    // 소분류 데이터를 옵션으로 추가
    data.data.forEach(sub => {
    const option = document.createElement("option");
    option.value = sub.categoryNo;
    option.textContent = sub.name;
    smallCategorySelect.appendChild(option);
});
} catch (error) {
    alert(`요청 실패: ${error.message}`);
}
}

    // 초기 중분류 목록 로드
    async function loadMiddleCategories() {
    const middleCategorySelect = document.querySelector("[name='subcategoryNo']");
    middleCategorySelect.innerHTML = '<option value="">선택</option>'; // 초기화

    try {
    const response = await fetch("/category/middle");
    if (!response.ok) {
    await handleApiError(response);
    return;
}

    const data = await response.json();

    // 중분류 데이터를 옵션으로 추가
    data.data.forEach(middle => {
    const option = document.createElement("option");
    option.value = middle.categoryNo;
    option.textContent = middle.name;
    middleCategorySelect.appendChild(option);
});
} catch (error) {
    alert(`요청 실패: ${error.message}`);
}
}

    // 페이지 로드 시 중분류 목록 초기화
    document.addEventListener("DOMContentLoaded", loadMiddleCategories);

    // 공통 에러 처리 함수
    function handleApiError(response) {
    return response.json().then(data => {
    if (data.fieldErrors && data.fieldErrors.length > 0) {
    data.fieldErrors.forEach(error => alert(`에러: ${error.reason}`));
} else if (data.message) {
    alert(`에러: ${data.message}`);
} else {
    alert("알 수 없는 오류가 발생했습니다.");
}
});
}

    // 모달 열기/닫기
    function openCategorySettings() {
    document.getElementById("categorySettingsModal").style.display = "block";
    loadCategories(); // 카테고리 목록 로드
}

    function closeCategorySettings() {
    document.getElementById("categorySettingsModal").style.display = "none";
    // 페이지 재로딩
    location.reload();
}

    // 카테고리 목록 로드
    async function loadCategories() {
    const categoryList = document.getElementById("middleCategoryList");
    categoryList.innerHTML = ""; // 기존 내용 초기화

    try {
    const response = await fetch("/category/total");
    if (!response.ok) throw new Error("카테고리 로드 실패");
    const data = await response.json();

    data.data.forEach(category => {
    const middleCategoryDiv = document.createElement("div");
    middleCategoryDiv.className = "middle-category";

    // 소분류가 비어 있는 경우 처리
    const subCategoriesHTML =
    category.childCategories.length > 0
    ? category.childCategories.map(sub => `
                        <div class="sub-category">
                            <input type="text" value="${sub.name}" class="sub-category-name" />
                            <button onclick="updateSubCategory(${sub.categoryNo}, this)" class="category-btn" style="width: 64px;margin-bottom: 15px;">수정</button>
                            <button onclick="deleteCategory(${sub.categoryNo})" class="category-btn delete-btn" style="width: 64px;margin-bottom: 15px;">삭제</button>
                        </div>
                    `).join("")
    : `<div class="no-sub-category" style="width: 182px;
                                                                        font-size: 14px;
                                                                        color: #555;
                                                                        margin: 9px 0;
                                    text-align: left;">소분류 목록이 비어있습니다.</div>`;
    middleCategoryDiv.innerHTML = `
                <div>
                    <input type="text" value="${category.name}" class="middle-category-name" />
                    <button onclick="updateMiddleCategory(${category.categoryNo}, this)" class="category-btn">수정</button>
                    <button onclick="deleteCategory(${category.categoryNo})" class="category-btn delete-btn">삭제</button>
                </div>
                <button onclick="toggleSubCategoryList(${category.categoryNo})" class="btn-secondary toggle-btn">+</button>
                <div class="sub-category-list" id="sub-category-list-${category.categoryNo}" style="display: none;">
                    ${subCategoriesHTML}
                    <button onclick="addSubCategoryUI(${category.categoryNo})" class="btn-secondary">소분류 추가</button>
                </div>
            `;

    categoryList.appendChild(middleCategoryDiv);
});
} catch (error) {
    alert(error.message);
}
}

    // 소분류 목록 토글
    function toggleSubCategoryList(middleCategoryId) {
    const subCategoryList = document.getElementById(`sub-category-list-${middleCategoryId}`);
    const toggleButton = document.querySelector(`[onclick="toggleSubCategoryList(${middleCategoryId})"]`);

    if (subCategoryList.style.display === "none") {
    subCategoryList.style.display = "block";
    toggleButton.textContent = "-"; // 버튼 텍스트 변경
} else {
    subCategoryList.style.display = "none";
    toggleButton.textContent = "+"; // 버튼 텍스트 변경
}
}

    // 중분류 수정
    async function updateMiddleCategory(categoryId, element) {
    const newName = element.previousElementSibling.value;

    if (!newName) {
    alert("카테고리 이름을 입력하세요.");
    return;
}

    try {
    const response = await fetch(`/category/middle/${categoryId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: newName })
});

    if (!response.ok) {
    await handleApiError(response);
    return;
}

    alert("중분류가 수정되었습니다.");
    loadCategories(); // 목록 갱신
} catch (error) {
    alert(error.message);
}
}

    // 소분류 수정
    async function updateSubCategory(categoryId, element) {
    const newName = element.previousElementSibling.value;

    if (!newName) {
    alert("카테고리 이름을 입력하세요.");
    return;
}

    try {
    const response = await fetch(`/category/sub/${categoryId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: newName })
});

    if (!response.ok) {
    await handleApiError(response);
    return;
}

    alert("소분류가 수정되었습니다.");
    loadCategories(); // 목록 갱신
} catch (error) {
    alert(error.message);
}
}

    // 카테고리 삭제
    async function deleteCategory(categoryId) {
    const confirmDelete = confirm("이 카테고리를 삭제하시겠습니까?");
    if (!confirmDelete) return;

    try {
    const response = await fetch(`/category/${categoryId}`, {
    method: "DELETE",
});

    if (!response.ok) {
    await handleApiError(response);
    return;
}

    alert("카테고리가 삭제되었습니다.");
    loadCategories(); // 목록 갱신
} catch (error) {
    alert(error.message);
}
}

    // 중분류 추가 UI
    function addMiddleCategoryUI() {
    const categoryList = document.getElementById("middleCategoryList");
    const newCategoryDiv = document.createElement("div");
    newCategoryDiv.className = "middle-category";

    newCategoryDiv.innerHTML = `
        <div>
            <input type="text" placeholder="중분류 이름" class="middle-category-name" />
            <button onclick="createMiddleCategory(this)"style="
                             padding: 7px 5px;
                             border-radius: 88px;
                            background-color: #007bff;
                            color: white;
                            border: none;
                            padding: 5px 10px;
                            border-radius: 5px;
                            font-size: 14px;
                            cursor: pointer;
                            margin-right: 5px;
                        ">추가</button>
        </div>
        <div class="sub-category-list"></div>
    `;

    categoryList.appendChild(newCategoryDiv);
}

    // 중분류 추가
    async function createMiddleCategory(element) {
    const name = element.previousElementSibling.value;

    if (!name) {
    alert("카테고리 이름을 입력하세요.");
    return;
}

    try {
    const response = await fetch("/category/new", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name })
});

    if (!response.ok) {
    await handleApiError(response);
    return;
}

    alert("중분류가 추가되었습니다.");
    loadCategories(); // 목록 갱신
} catch (error) {
    alert(error.message);
}
}

    // 소분류 추가 UI
    function addSubCategoryUI(middleCategoryId) {
    const subCategoryList = document.getElementById(`sub-category-list-${middleCategoryId}`);

    // 소분류 목록이 비어있으면 메시지 제거
    const noSubCategoryMessage = subCategoryList.querySelector(".no-sub-category");
    if (noSubCategoryMessage) {
    noSubCategoryMessage.remove();
}

    const newSubCategoryDiv = document.createElement("div");
    newSubCategoryDiv.className = "sub-category";

    newSubCategoryDiv.innerHTML = `
        <input type="text" placeholder="소분류 이름" class="sub-category-name" />
        <button onclick="createSubCategory(this, ${middleCategoryId})" class="category-btn" style="width: 64px; margin-bottom: 13px;">추가</button>
    `;

    subCategoryList.appendChild(newSubCategoryDiv);
}


    // 소분류 추가
    async function createSubCategory(element, parentNo) {
    const name = element.previousElementSibling.value;

    if (!name) {
    alert("소분류 이름을 입력하세요.");
    return;
}

    try {
    const response = await fetch("/category/new", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, parentNo })
});

    if (!response.ok) {
    await handleApiError(response);
    return;
}

    alert("소분류가 추가되었습니다.");
    loadCategories(); // 목록 갱신
} catch (error) {
    alert(error.message);
}
}