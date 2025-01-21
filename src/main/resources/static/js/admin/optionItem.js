// 선택된 품목 데이터를 유지할 변수
const selectedOptions = [];

// 옵션 여부에 따른 필드 표시/숨기기
function toggleOptionFields() {
    const optionToggle = document.getElementById("optionToggle");
    const singleOptionFields = document.getElementById("singleOptionFields");
    const multipleOptionFields = document.getElementById("multipleOptionFields");
    const optionStatus = document.getElementById("optionStatus");

    if (optionToggle.checked) {
        singleOptionFields.style.display = "none";
        multipleOptionFields.style.display = "block";
        optionStatus.innerText = "옵션 있음";
    } else {
        singleOptionFields.style.display = "flex";
        multipleOptionFields.style.display = "none";
        optionStatus.innerText = "옵션 없음 (단품)";
    }
}

// 옵션 항목 개수에 따라 옵션 그룹 추가
function renderOptionFields() {
    const optionCount = parseInt(document.getElementById("optionCount").value, 10);
    const dynamicOptionFields = document.getElementById("dynamicOptionFields");

    // 기존 옵션값 저장
    const existingOptions = [];
    Array.from(dynamicOptionFields.querySelectorAll(".option-group")).forEach((group, index) => {
        const groupName = group.querySelector(`input[name="optionGroup${index + 1}"]`).value;
        const optionValues = Array.from(group.querySelectorAll(".custom-tag"))
            .map(tag => tag.childNodes[0]?.nodeValue.trim());
        existingOptions.push({ groupName, optionValues });
    });

    dynamicOptionFields.innerHTML = ""; // 기존 필드 초기화

    const defaultGroupNames = ["사이즈", "컬러", "재질"]; // 기본 그룹명 배열

    for (let i = 1; i <= optionCount; i++) {
        const existingGroup = existingOptions[i - 1]; // 기존 값이 있는 경우 가져오기
        const groupName = existingGroup?.groupName || defaultGroupNames[i - 1] || "";

        const fieldSet = document.createElement("div");
        fieldSet.className = "option-group";
        fieldSet.innerHTML = `
                <div class="form-row" style="align-items: center;">
                    <div class="form-field" style="flex: 2;">
                        <label>옵션 그룹명</label>
                        <input type="text" name="optionGroup${i}" value="${groupName}" placeholder="옵션 그룹명 입력">
                    </div>
                </div>
                <div class="form-row" style="flex-direction: column; gap: 10px;">
                    <label>옵션 값 (옵션 입력 후 Enter)</label>
                    <input type="text" name="optionValue${i}" placeholder="예: small, medium, large" onkeypress="addTag(event, 'optionTags${i}')">
                    <div id="optionTags${i}" class="custom-tag-container"></div>
                </div>
            `;
        dynamicOptionFields.appendChild(fieldSet);

        // 기존 옵션값 복원
        if (existingGroup && existingGroup.optionValues) {
            const container = document.getElementById(`optionTags${i}`);
            existingGroup.optionValues.forEach(value => {
                const tag = document.createElement("span");
                tag.className = "custom-tag";
                tag.textContent = value;

                const removeBtn = document.createElement("button");
                removeBtn.innerText = "x";
                removeBtn.onclick = () => tag.remove();
                tag.appendChild(removeBtn);

                container.appendChild(tag);
            });
        }
    }
}

function addTag(event, containerId) {
    if (event.key === "Enter") {
        event.preventDefault();
        const value = event.target.value.trim();
        if (value) {
            const container = document.getElementById(containerId);
            const tag = document.createElement("span");
            tag.className = "custom-tag";
            tag.innerText = value;
            const removeBtn = document.createElement("button");
            removeBtn.innerText = "x";
            removeBtn.onclick = () => tag.remove();
            tag.appendChild(removeBtn);
            container.appendChild(tag);
            event.target.value = "";
        }
    }
}

// "품목 생성하기" 버튼 눌렀을 때 기존 선택된 데이터 유지
async function generateOptionsPreview() {
    const dynamicOptionFields = document.getElementById("dynamicOptionFields");
    const optionGroups = [];

    // 옵션 그룹명과 값 가져오기
    Array.from(dynamicOptionFields.querySelectorAll(".option-group")).forEach((group, index) => {
        const groupName = group.querySelector(`input[name="optionGroup${index + 1}"]`).value;
        const optionValues = Array.from(group.querySelectorAll(".custom-tag"))
            .map(tag => ({
                valueName: tag.childNodes[0]?.nodeValue.trim() // 버튼 텍스트 제외한 내용 추출
            }));

        if (groupName && optionValues.length > 0) {
            optionGroups.push({ groupName, optionValues });
        }
    });

    if (optionGroups.length === 0) {
        alert("옵션을 입력해주세요.");
        return;
    }

    try {
        const response = await fetch("/api/items/preview", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(optionGroups)
        });

        if (!response.ok) throw new Error("옵션 생성에 실패했습니다.");

        const result = await response.json();
        const data = result.data; // 'data' 필드 추출
        renderOptionPreview(data);
    } catch (error) {
        console.error("옵션 생성 오류:", error);
        alert("옵션 생성 중 문제가 발생했습니다.");
    }
}

function renderOptionPreview(data) {
    const modal = document.getElementById("optionPreviewModal");
    const content = document.getElementById("optionPreviewContent");

    content.innerHTML = ""; // 기존 내용 초기화

    data.forEach(item => {
        const row = document.createElement("tr");

        // 이름 비교를 위해 공백과 줄바꿈 제거
        const formattedName = item.name.replace(/,/g, '\n').trim();
        const isSelected = selectedOptions.some(opt => opt.name.trim() === formattedName); // 이미 선택된 옵션인지 확인

        const existingOption = selectedOptions.find(opt => opt.name.trim() === formattedName);

        row.innerHTML = `
            <td><input type="checkbox" class="option-select" ${isSelected ? "checked" : ""}></td>
            <td><div class="option-name">${formattedName}</div></td>
            <td><input type="number" class="additional-price" placeholder="0" value="${existingOption?.additionalPrice || 0}"></td>
            <td><input type="number" class="stock-quantity" placeholder="0" value="${existingOption?.stockQuantity || 0}"></td>
            <td><input type="number" class="safe-stock" placeholder="0" value="${existingOption?.safeStock || 0}"></td>
            <td><input type="number" class="max-quantity" placeholder="0" value="${existingOption?.maxQuantity || 0}"></td>
            <td><input type="number" class="min-quantity" placeholder="0" value="${existingOption?.minQuantity || 0}"></td>
        `;

        content.appendChild(row);
    });

    modal.style.display = "block"; // 모달 표시
}

function toggleSelectAll(checkbox) {
    const checkboxes = document.querySelectorAll(".option-select");
    checkboxes.forEach(cb => cb.checked = checkbox.checked);
}

// 유효성 검사 함수
function validateRow(row) {
    const errors = [];
    const additionalPrice = parseInt(row.querySelector(".additional-price").value) || 0;
    const stockQuantity = parseInt(row.querySelector(".stock-quantity").value) || 0;
    const safeStock = parseInt(row.querySelector(".safe-stock").value) || 0;
    const maxQuantity = parseInt(row.querySelector(".max-quantity").value) || 0;
    const minQuantity = parseInt(row.querySelector(".min-quantity").value) || 0;

    // 숫자 필드 검증
    if (isNaN(additionalPrice) || additionalPrice < 0) {
        errors.push("추가 금액은 0 이상이어야 합니다.");
    }
    if (isNaN(stockQuantity) || stockQuantity <= 30) {
        errors.push("재고량은 30 이상이어야 합니다.");
    }
    if (isNaN(safeStock) || safeStock < 0) {
        errors.push("안전 재고량은 0 이상이어야 합니다.");
    }
    if (isNaN(maxQuantity) || maxQuantity < 0) {
        errors.push("최대 구매 수량은 0 이상이어야 합니다.");
    }
    if (isNaN(minQuantity) || minQuantity < 0) {
        errors.push("최소 구매 수량은 0 이상이어야 합니다.");
    }

    // 최소/최대 수량 관계 검증
    if (maxQuantity !== 0 && minQuantity > maxQuantity) {
        errors.push("최소 구매 수량은 최대 구매 수량보다 작아야 합니다.");
    }

    return errors;
}

// "품목 선택완료" 버튼 클릭 시 검증
function finalizeOptions() {
    const rows = document.querySelectorAll("#optionPreviewContent tr");
    let isValid = true;
    let isAnySelected = false;

    selectedOptions.length = 0; // 기존 선택된 데이터 초기화

    rows.forEach(row => {
        const isSelected = row.querySelector(".option-select").checked;
        if (isSelected) {
            isAnySelected = true; // 체크된 항목이 있는지 확인
            const errors = validateRow(row);

            if (errors.length > 0) {
                // 에러 메시지를 alert 창으로 표시
                errors.forEach(error => alert(error));
                isValid = false;
            } else {
                const optionName = row.querySelector(".option-name").textContent.trim();
                const additionalPrice = row.querySelector(".additional-price").value;
                const stockQuantity = row.querySelector(".stock-quantity").value;
                const safeStock = row.querySelector(".safe-stock").value;
                const maxQuantity = row.querySelector(".max-quantity").value;
                const minQuantity = row.querySelector(".min-quantity").value;

                console.log(row.querySelector(".option-name").textContent.trim());
                selectedOptions.push({
                    name: optionName,
                    additionalPrice,
                    stockQuantity,
                    safeStock,
                    maxQuantity,
                    minQuantity
                });
            }
        }
    });

    // 체크박스를 하나도 선택하지 않았을 경우 경고 메시지 출력
    if (!isAnySelected) {
        alert("1개 이상 품목을 선택해야 합니다.");
        return;
    }

    if (!isValid) {
        // 모든 에러 메시지가 표시된 후, 검증 중단
        return;
    }

    console.log("최종 선택된 품목:", selectedOptions);
    alert("품목이 선택되었습니다.");
    closeModal();
}

// 모달 닫기 버튼
function closeModal() {
    const modal = document.getElementById("optionPreviewModal");
    modal.style.display = "none";
}

// 모달 바깥 클릭 시 닫기
window.onclick = function (event) {
    const modal = document.getElementById("optionPreviewModal");
    if (event.target === modal) {
        closeModal();
    }
};