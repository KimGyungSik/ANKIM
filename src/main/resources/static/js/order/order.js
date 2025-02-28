document.addEventListener("DOMContentLoaded", () => {
    // 배송지 기존/신규 선택
    var tabs = document.querySelectorAll(".shipping-tab .tab-item");
    var existingAddress = document.querySelector(".existing-address");
    var newAddress = document.querySelector(".new-address");

    // 각 라디오 버튼과 관련 영역 선택
    var incomeRadio = document.querySelector('input[name="receiptType"][value="INCOME"]');
    var spendingRadio = document.querySelector('input[name="receiptType"][value="SPENDING"]');
    var noneRadio = document.querySelector('input[name="receiptType"][value="NONE"]');

    var contactSelect = document.querySelector('.contact-method-select');
    var cashReceiptExtra = document.querySelector('.cash-receipt-extra');
    var cashReceiptSave = document.querySelector('.cash-receipt-save');

    // 현금영수증 번호를 입력하는 input 필드
    var receiptInput = document.querySelector('.cash-receipt-input input');

    // 쿠폰 할인 금액
    var couponToggleBtn = document.querySelector(".coupon-toggle-btn");
    var couponBreakdown = document.querySelector(".coupon-breakdown");

    // 약관 동의 "전체 동의" 체크박스
    var checkAllInput = document.querySelector(".check-all-input");
    // 개별 약관 체크박스들
    var agreementInputs = document.querySelectorAll(".agreement-list input[type='checkbox']");



    // 초기 상태: 기존 배송지 보이고 신규입력 숨김
    if (existingAddress && newAddress) {
        existingAddress.style.display = "block";
        newAddress.style.display = "none";
    }

    tabs.forEach((tab, index) => {
        tab.addEventListener("click", () => {
            // 모든 탭의 active 클래스 제거
            tabs.forEach(t => t.classList.remove("active"));
            // 클릭된 탭에 active 클래스 추가
            tab.classList.add("active");

            // 인덱스 0: 기존 배송지, 1: 신규입력
            if (index === 0) {
                existingAddress.style.display = "block";
                newAddress.style.display = "none";
            } else {
                existingAddress.style.display = "none";
                newAddress.style.display = "block";
            }
        });
    });

    // 라디오 선택에 따른 옵션 업데이트 함수
    function updateReceiptOptions() {
        if (incomeRadio.checked) {
            // 소득공제용 선택 시: 옵션 2개 (휴대폰 번호, 현금영수증 카드번호) / 선택 가능
            contactSelect.disabled = false;
            contactSelect.innerHTML = `
        <option value="PHONE">휴대폰 번호</option>
        <option value="CARDNUM">현금영수증 카드번호</option>
      `;
            // extra와 save 영역 보이기
            cashReceiptExtra.style.display = "";
            cashReceiptSave.style.display = "";
            // 입력 필드 초기화
            receiptInput.value = "";
        } else if (spendingRadio.checked) {
            // 지출증빙용 선택 시: 옵션 1개 (사업자 번호) / 선택 불가능
            contactSelect.disabled = true;
            contactSelect.innerHTML = `<option value="BUSINESS">사업자 번호</option>`;
            cashReceiptExtra.style.display = "";
            cashReceiptSave.style.display = "";
            // 입력 필드 초기화
            receiptInput.value = "";
        } else if (noneRadio.checked) {
            // 미발행 선택 시: extra, save 영역 숨기기
            cashReceiptExtra.style.display = "none";
            cashReceiptSave.style.display = "none";
            // 입력 필드 초기화
            receiptInput.value = "";
        }
    }

    // 모든 라디오 버튼에 change 이벤트 등록
    var receiptRadios = document.querySelectorAll('input[name="receiptType"]');
    receiptRadios.forEach(function(radio) {
        radio.addEventListener("change", updateReceiptOptions);
    });

    // 초기 상태 업데이트
    updateReceiptOptions();


    // 버튼 클릭 -> couponBreakdown 열고/닫기
    couponToggleBtn.addEventListener("click", () => {
        var isOpen = couponToggleBtn.getAttribute("data-open") === "true";
        if (isOpen) {
            couponBreakdown.style.display = "none";
            couponToggleBtn.setAttribute("data-open", "false");
        } else {
            couponBreakdown.style.display = "block";
            couponToggleBtn.setAttribute("data-open", "true");
        }
    });


    // (1) "전체 동의"를 클릭하면 -> 개별 약관 전부 체크/해제
    checkAllInput.addEventListener("change", (e) => {
        var isChecked = e.target.checked;
        agreementInputs.forEach((checkbox) => {
            checkbox.checked = isChecked;
        });
    });

    // (2) 개별 약관 중 하나라도 변경되면
    agreementInputs.forEach((checkbox) => {
        checkbox.addEventListener("change", () => {
            // 만약 체크 해제된 것이 하나라도 있다면 "전체 동의" 해제
            if (!checkbox.checked) {
                checkAllInput.checked = false;
            } else {
                // 모두 체크되었는지 확인
                var allChecked = Array.from(agreementInputs).every((cb) => cb.checked);
                checkAllInput.checked = allChecked;
            }
        });
    });
});