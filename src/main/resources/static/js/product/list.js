// ✅ 공통 AJAX 요청 함수
function sendAjaxRequest(params) {
    const newUrl = `/api/v1/product/catalog/list?${params.toString()}`;

    // ✅ 브라우저의 주소창 URL 변경 (뒤로 가기 가능)
    history.pushState(null, "", newUrl);

    fetch(newUrl, {
        method: "GET",
        headers: { "X-Requested-With": "XMLHttpRequest" }
    })
        .then(response => response.json())
        .then(data => {
            updateProductList(data.data.products);
            updatePagination(data.data.pageInfo);
            updateCategoryActiveState(params.get("category"));
            window.scrollTo({ top: 0, behavior: "smooth" });
        })
        .catch(error => console.error("Error:", error));
}

// ✅ 상품 리스트 업데이트
function updateProductList(products) {
    const productListContainer = document.querySelector(".product-list");
    productListContainer.innerHTML = "";

    // 현재 URL에서 keyword 파라미터 확인 (검색 여부 판단)
    const urlParams = new URLSearchParams(window.location.search);
    const keyword = urlParams.get("keyword");

    if (products.length === 0) {
        if (keyword) {
            // 🔹 검색어가 있을 때 검색 결과 없음 문구 표시
            productListContainer.innerHTML = '<div class="no-products">해당 카테고리에는 검색결과가 없습니다.</div>';
        } else {
            // 🔹 일반적인 경우 상품 없음 문구 표시
            productListContainer.innerHTML = '<div class="no-products">상품이 0개입니다.</div>';
        }
        return;
    }

    products.forEach(product => {
        const productCard = `
        <div class="product-card" data-product-id="${product.no}">
            <img src="${product.thumbNailImgUrl}" alt="상품 이미지" />
            <div class="product-info">
                <h3 class="product-name">${product.name}</h3>
                <div class="price-discount">
                    ${product.discRate > 0 ? `<span class="discount-rate">${product.discRate}%</span>` : ""}
                    <span class="price">${product.sellPrice.toLocaleString()}</span>
                </div>
                <div class="product-badges">
                    ${product.freeShip === "Y" ? '<span class="badge">무료배송</span>' : ""}
                    ${product.handMadeYn === "Y" ? '<span class="badge">핸드메이드</span>' : ""}
                </div>
                <div class="extra-info">
                    <!-- 찜 수 (하트 아이콘) -->
                    <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-heart-fill" viewBox="0 0 16 16">
                        <path d="M8 2C4.685-1.127 0 1.324 0 5.3 0 7.999 3.354 11.324 8 14c4.646-2.676 8-6.001 8-8.7 0-3.976-4.685-6.427-8-3.3-3.315-3.127-8-.676-8 3.3 0 2.699 3.354 6.024 8 8.7 4.646-2.676 8-6.001 8-8.7 0-3.976-4.685-6.427-8-3.3z"/>
                    </svg>
                    <span class="wish-cnt">${product.wishCnt || 0}</span>

                    <!-- 별점 아이콘 -->
                    <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-star" viewBox="0 0 16 16">
                        <path d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.523-3.356c-.329-.314.158-.888-.283-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.22l-4.898.696c-.441.062-.612.636-.282.95l3.522 3.356-.83 4.73z"/>
                    </svg>
                    <span class="avg-rating">${product.avgR || 0}</span>

                    <!-- 리뷰 개수 -->
                    (<span class="review-cnt">${product.rvwCnt || 0}</span>)
                </div>
            </div>
        </div>`;
        productListContainer.innerHTML += productCard;
    });
}

// ✅ 페이지네이션 업데이트
function updatePagination(pageInfo) {
    const paginationContainer = document.querySelector(".pagination");
    let paginationHTML = "";

    const groupSize = 10;
    const currentGroup = Math.floor(pageInfo.currentPage / groupSize) + 1;
    const startPage = (currentGroup - 1) * groupSize;
    const endPage = Math.min(startPage + groupSize - 1, pageInfo.totalPages - 1);

    if (startPage > 0) {
        paginationHTML += `<a href="#" class="prev-group" data-page="${startPage - 1}">&laquo;</a>`;
    }

    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `
        <span class="${pageInfo.currentPage === i ? 'active' : ''}">
            <a href="#" class="page-link" data-page="${i}">${i + 1}</a>
        </span>`;
    }

    if (endPage < pageInfo.totalPages - 1) {
        paginationHTML += `<a href="#" class="next-group" data-page="${endPage + 1}">&raquo;</a>`;
    }

    paginationContainer.innerHTML = paginationHTML;
}

// ✅ 카테고리 active 상태 업데이트
function updateCategoryActiveState(selectedCategory) {
    document.querySelectorAll(".category-menu li").forEach(li => {
        li.classList.remove("active");
    });

    if (!selectedCategory) {
        document.querySelector(".category-menu li:first-child").classList.add("active");
    } else {
        document.querySelector(`.category-menu a[data-category="${selectedCategory}"]`)
            ?.closest("li").classList.add("active");
    }
}


const selectedFiltersContainer = document.querySelector(".selected-filters");
// ✅ 전역 변수 초기화 (global.js에서 선언)
window.selectedColors = window.selectedColors || [];
window.selectedInfoSearches = window.selectedInfoSearches || [];
window.selectedPriceCondition = window.selectedPriceCondition || null;
window.customMinPrice = window.customMinPrice || null;
window.customMaxPrice = window.customMaxPrice || null;



// ✅ 선택된 필터 업데이트
function updateSelectedFilters() {
    const urlParams = new URLSearchParams(window.location.search);
    selectedFiltersContainer.innerHTML = ""; // 기존 내용 초기화

    // 🔹 색상 필터 추가
    const colorConditions = urlParams.get("colorConditions");
    if (colorConditions) {
        colorConditions.split(",").forEach(color => {
            addFilterTag(color, "colorConditions");
        });
    }

    // 🔹 상품정보 필터 추가
    const infoSearches = urlParams.get("infoSearches");
    if (infoSearches) {
        infoSearches.split(",").forEach(info => {
            addFilterTag(info, "infoSearches");
        });
    }

    // 🔹 가격대 필터 추가
    const priceCondition = urlParams.get("priceCondition");
    if (priceCondition) {
        addFilterTag(priceCondition, "priceCondition");
    }
}

// ✅ 필터 태그 추가 (보이는 값은 한글로 변환)
function addFilterTag(value, key) {
    const filterTag = document.createElement("div");
    filterTag.classList.add("filter-tag");

    let displayText = value; // 기본값으로 value 사용

    // 🔹 색상 필터의 한글명 가져오기
    if (key === "colorConditions") {
        const colorElement = document.querySelector(`.color-filter li[data-color="${value}"]`);
        if (colorElement) {
            displayText = colorElement.textContent.trim(); // 색상명 가져오기
        }
    }
    // 🔹 가격대 필터의 한글명 가져오기
    else if (key === "priceCondition") {
        if (value === "CUSTOM") {
            // 직접 입력한 가격이 있을 경우 가져오기
            const minPrice = window.customMinPrice || "";
            const maxPrice = window.customMaxPrice || "";
            if (minPrice && maxPrice) {
                displayText = `${parseInt(minPrice).toLocaleString()}원 ~ ${parseInt(maxPrice).toLocaleString()}원`;
            } else if (minPrice) {
                displayText = `${parseInt(minPrice).toLocaleString()}원 이상`;
            } else if (maxPrice) {
                displayText = `${parseInt(maxPrice).toLocaleString()}원 이하`;
            }
        } else {
            const priceLabel = document.querySelector(`.price-filter input[value="${value}"] + label`);
            if (priceLabel) {
                displayText = priceLabel.textContent.trim();
            }
        }
    }
    // 🔹 상품정보 필터의 한글명 가져오기
    else if (key === "infoSearches") {
        const infoLabel = document.querySelector(`.product-info-section input[id="${value}"] + label`);
        if (infoLabel) {
            displayText = infoLabel.textContent.trim();
        }
    }

    filterTag.innerHTML = `
        ${displayText}
        <button class="remove-filter" data-key="${key}" data-value="${value}">X</button>
    `;

    selectedFiltersContainer.appendChild(filterTag);
}



// ✅ X 버튼 클릭 이벤트 처리
selectedFiltersContainer.addEventListener("click", function (event) {
    if (event.target.matches(".remove-filter")) {
        const key = event.target.getAttribute("data-key");
        const value = event.target.getAttribute("data-value");

        const urlParams = new URLSearchParams(window.location.search);

        // ✅ listSide 요소들 가져오기
        const priceFilterInputs = document.querySelectorAll(".price-filter input");
        const minPriceInput = document.getElementById("min-price");
        const maxPriceInput = document.getElementById("max-price");
        const colorFilterItems = document.querySelectorAll(".color-filter li");
        const checkboxes = document.querySelectorAll(".product-info-section input[type='checkbox']");

        if (key === "colorConditions") {
            // ✅ 색상 필터 전역 상태 업데이트
            window.selectedColors = window.selectedColors.filter(color => color !== value);
            colorFilterItems.forEach(item => {
                if (item.getAttribute("data-color") === value) {
                    item.classList.remove("selected");
                }
            });
            urlParams.set("colorConditions", window.selectedColors.join(","));
            if (window.selectedColors.length === 0) {
                urlParams.delete("colorConditions");
            }
        } else if (key === "infoSearches") {
            // ✅ 상품정보 필터 전역 상태 업데이트
            window.selectedInfoSearches = window.selectedInfoSearches.filter(info => info !== value);
            checkboxes.forEach(checkbox => {
                if (checkbox.id === value) {
                    checkbox.checked = false;
                }
            });
            urlParams.set("infoSearches", window.selectedInfoSearches.join(","));
            if (window.selectedInfoSearches.length === 0) {
                urlParams.delete("infoSearches");
            }
        } else if (key === "priceCondition") {
            // ✅ 가격 필터 초기화
            window.selectedPriceCondition = null;
            window.customMinPrice = null;
            window.customMaxPrice = null;

            priceFilterInputs.forEach(input => input.checked = false);
            if (minPriceInput && maxPriceInput) {
                minPriceInput.value = "";
                maxPriceInput.value = "";
                minPriceInput.disabled = true;
                maxPriceInput.disabled = true;
            }
            urlParams.delete("priceCondition");
            urlParams.delete("customMinPrice");
            urlParams.delete("customMaxPrice");
        }

        // ✅ 비동기 요청
        sendAjaxRequest(urlParams);

        // ✅ 필터 태그 업데이트
        updateSelectedFilters();
    }
});





