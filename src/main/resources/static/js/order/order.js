import { fetchWithAccessToken } from '../utils/fetchUtils.js';
import { execDaumPostcode } from '../utils/map.js';

document.addEventListener("DOMContentLoaded", async () => {

    try {
        var data = await fetchWithAccessToken("/api/temp-order", { method: "GET" });

        if (!data || data.error) {
            throw new Error(data.message || "서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }

        if (data.code === 200 && data.data) {
            renderDefaultAddress(data.data.addresses); // 기존 배송지
            renderProducts(data.data.items); // 상품
            renderPaymentDetails(data.data); // 결제금액

        } else {
            showErrorModal(data.data.message);
            // setTimeout(() => window.location.href = data.data.referer, 2000); // 2초 후 이전 페이지로 이동
        }
    } catch (error) {
        showErrorModal(error.data.message);
        // setTimeout(() => window.location.href = error.data.referer, 2000); // 2초 후 이전 페이지로 이동
    }
    // 모든 모달 닫기 버튼에 대해 이벤트 리스너 등록
    document.querySelectorAll('.close-button').forEach(btn => {
        btn.addEventListener('click', () => {
            // 가장 가까운 모달(또는 모달 오버레이) 요소를 찾습니다.
            var modal = btn.closest('.modal-overlay') || btn.closest('.modal');
            if (modal) {
                modal.style.display = 'none';
            }
        });
    });

    // 배송 정보
    var shippingInfoBtn = document.getElementById("shippingInfoBtn");
    var shippingInfoModal = document.getElementById("shippingInfoModal");

    // 배송지 기존/신규 선택
    var tabs = document.querySelectorAll(".shipping-tab .tab-item");
    var existingAddress = document.querySelector(".existing-address");
    var newAddress = document.querySelector(".new-address");

    // 배송 요청 사항 드롭 다운
    const dropdowns = document.querySelectorAll(".request-dropdown");

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

    // 기존 배송지 영역의 우편번호 검색 버튼 선택
    var existingAddrSearchBtn = document.querySelector(".existing-address .addrSearchBtn");
    // 신규 배송지 영역의 우편번호 검색 버튼 선택
    var newAddrSearchBtn = document.querySelector(".new-address .addrSearchBtn");

    // 모달 초기상태 안보이게 설정
    window.closeModal = function () {
        var modal = document.querySelector('.modal');
        var modalOverlay = document.querySelector('.modal-overlay');
        modal.style.display = "none";
        modalOverlay.style.display = "none";
    };

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

    // 배송안내 버튼 눌랐을때
    shippingInfoBtn.addEventListener("click", () => {
        shippingInfoModal.style.display = "flex";
    });

    // 기본 배송지 탭에서 우편번호 검색
    if (existingAddrSearchBtn) {
        existingAddrSearchBtn.addEventListener("click", () => {
            // 기존 배송지 영역의 주소 검색 로직 실행
            var container = document.querySelector(".existing-address");
            execDaumPostcode(container);
        });
    }

    // 신규입력 탭에서 우편번호 검색
    if (newAddrSearchBtn) {
        newAddrSearchBtn.addEventListener("click", () => {
            // 신규 배송지 영역의 주소 검색 로직 실행
            var container = document.querySelector(".new-address");
            execDaumPostcode(container);
        });
    }

    // 배송 요청사항
    dropdowns.forEach(dropdown => {
        const inputField = dropdown.querySelector(".request-dropdown-input");
        const dropdownList = dropdown.querySelector(".request-dropdown-list");
        const textarea = dropdown.closest(".form-field").querySelector(".request-textarea");
        const dropdownIcon = dropdown.querySelector(".dropdown-icon");

        // 드롭다운 열고 닫기
        inputField.addEventListener("click", (event) => {
            event.stopPropagation();
            dropdownList.classList.toggle("show");
            dropdown.classList.toggle("open");
            dropdownIcon.style.transform = dropdownList.classList.contains("show") ? "rotate(180deg)" : "rotate(0)";
        });

        // 옵션 선택 시 동작
        dropdownList.addEventListener("click", (e) => {
            if (e.target.tagName === "LI") {
                const selectedValue = e.target.getAttribute("data-value");
                inputField.value = selectedValue;
                dropdownList.classList.remove("show");
                dropdown.classList.remove("open");
                dropdownIcon.style.transform = "rotate(0)";

                // 직접입력 선택 시 textarea 표시, 아니면 숨김
                if (selectedValue === "직접입력") {
                    textarea.style.display = "block";
                    textarea.focus();
                } else {
                    textarea.style.display = "none";
                }
            }
        });

        // 바깥 클릭 시 드롭다운 닫기
        document.addEventListener("click", (e) => {
            if (!dropdown.contains(e.target)) {
                dropdownList.classList.remove("show");
                dropdown.classList.remove("open");
                dropdownIcon.style.transform = "rotate(0)";
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

    // "약관 보기" 버튼들
    const viewButtons = document.querySelectorAll(".view-btn");

    // [수정] 단일 forEach만 사용하여 클릭 이벤트를 등록
    viewButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            // 버튼에 있는 data-fetch-url 속성값 가져오기
            const fetchUrl = btn.getAttribute("data-fetch-url");
            console.log("약관 버튼 클릭 :", fetchUrl);

            if (!fetchUrl) return;

            // HTML 파일 fetch
            fetch(fetchUrl)
                .then(response => response.text())
                .then(html => {
                    // 모달 내부의 .modal-body에 삽입
                    const modalBody = document.querySelector("#termsModal .modal-body");
                    if (modalBody) {
                        modalBody.innerHTML = html;
                    }

                    // 모달 열기
                    document.getElementById("termsModal").style.display = "flex";
                })
                .catch(err => {
                    console.error("약관 파일을 불러오는 중 오류 발생:", err);
                });
        });
    });

    // 결제 방법 섹션 - kyunsik
    main();
    async function main() {
        const button = document.getElementById("payment-button");
        // ------  결제위젯 초기화 ------
        const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
        const tossPayments = TossPayments(clientKey);
        // 회원 결제
        const customerKey = "WQd75CTuJUuF0wLyEP7Ej";
        const widgets = tossPayments.widgets({
            customerKey,
        });
        // ------ 주문의 결제 금액 설정 ------
        await widgets.setAmount({
            currency: "KRW",
            value: data.data.payAmt,
        });

        await Promise.all([
            // ------  결제 UI 렌더링 ------
            widgets.renderPaymentMethods({
                selector: "#payment-method",
                variantKey: "DEFAULT",
            }),
        ]);

        // ------ '결제하기' 버튼 누르면 결제창 띄우기 ------
        button.addEventListener("click", async function () {
            const paymentRequestData = {
                paymentRequest: {
                    payType: "CARD",  // 결제 타입 (실제 결제 방식으로 설정)
                    amount: data.data.payAmt,  // 결제 금액
                    orderName: data.data.orderCode,  // 주문명
                    yourSuccessUrl: window.location.origin + "/toss/success",
                    yourFailUrl: window.location.origin + "/toss/fail",
                },
                deliveryRequest: {
                    addressId: null,  // 배송 주소 ID
                    courier: "CJ대한통운",  // 택배사
                    delReq: "문 앞에 놓아주세요",  // 배송 요청사항
                },
                addressRequest: {
                    addressMain: "서울특별시 강남구 테헤란로 123",  // 기본 주소
                    addressName: "우리집",  // 주소 이름
                    addressDetail: "101호",  // 상세 주소
                    zipCode: 12345,  // 우편번호
                    phoneNumber: "01012341234",  // 전화번호
                    emergencyPhoneNumber: "01056785678",  // 비상 전화번호
                    defaultAddressYn: "Y",  // 기본 주소 여부
                },
            };

            try {
                // 먼저 서버에 결제 정보 요청
                const response = await fetch("/api/v1/payments/toss", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(paymentRequestData),
                });

                if (!response.ok) {
                    const errorJson = await response.json();
                    alert("결제 요청 실패: " + errorJson.message);
                    return;
                }

                // 서버 응답이 성공하면 결제 진행
                await widgets.requestPayment({
                    orderId: data.data.orderNo,  // 주문 ID
                    orderName: "토스 티셔츠 외 2건",
                    successUrl: window.location.origin + "/toss/success",
                    failUrl: window.location.origin + "/toss/fail",
                    customerEmail: "customer123@gmail.com",
                    customerName: "김토스",
                    customerMobilePhone: "01012341234",
                });
            } catch (error) {
                console.error("결제 요청 중 오류 발생:", error);
            }
        });

    }


});

// 기존 주소정보 렌더링하는 함수
function renderDefaultAddress(addresses) {
    // defaultAddressYn이 "Y"인 주소를 찾는다.
    const defaultAddress = addresses.find(addr => addr.defaultAddressYn === "Y");
    if (!defaultAddress) return; // 기본 배송지가 없으면 아무 작업도 하지 않음

    // 기존 배송지 영역 내부의 입력 필드에 값 설정
    const existingAddressSection = document.querySelector('.existing-address');
    if (existingAddressSection) {
        const addressNameInput = existingAddressSection.querySelector('input[name="addressName"]');
        const receiverInput = existingAddressSection.querySelector('input[name="receiver"]');
        // 우편번호, 메인주소, 상세주소는 order.html에서 id로 지정되어 있으므로:
        const zipCodeInput = document.querySelector('.zipCodeInput');
        const addressMainInput = document.querySelector('.addressMainInput');
        const addressDetailInput = document.querySelector('.addressDetailInput');

        if(addressNameInput) addressNameInput.value = defaultAddress.addressName || "";
        if(receiverInput) receiverInput.value = defaultAddress.receiver || "";
        if(zipCodeInput) zipCodeInput.value = defaultAddress.zipCode || "";
        if(addressMainInput) addressMainInput.value = defaultAddress.addressMain || "";
        if(addressDetailInput) addressDetailInput.value = defaultAddress.addressDetail || "";

        // 연락처1 (phoneNumber) - "-" 기준으로 분리
        if (defaultAddress.phoneNumber) {
            const telParts = defaultAddress.phoneNumber.split("-");
            if (telParts.length >= 3) {
                document.querySelector('input[name="tel1_0"]').value = telParts[0];
                document.querySelector('input[name="tel1_1"]').value = telParts[1];
                document.querySelector('input[name="tel1_2"]').value = telParts[2];
            } else {
                // 전화번호 형식이 예상과 다르면 전체 문자열을 첫번째 칸에 할당하는 식으로 처리 가능
                document.querySelector('input[name="tel1_0"]').value = defaultAddress.phoneNumber;
            }
        }

        // 연락처2 (emergencyPhoneNumber) - "-" 기준으로 분리
        if (defaultAddress.emergencyPhoneNumber) {
            const telParts2 = defaultAddress.emergencyPhoneNumber.split("-");
            if (telParts2.length >= 3) {
                document.querySelector('input[name="tel2_0"]').value = telParts2[0];
                document.querySelector('input[name="tel2_1"]').value = telParts2[1];
                document.querySelector('input[name="tel2_2"]').value = telParts2[2];
            } else {
                document.querySelector('input[name="tel2_0"]').value = defaultAddress.emergencyPhoneNumber;
            }
        }

        // "이전 배송지 목록" 버튼에 이벤트 등록
        const prevAddressBtn = document.querySelector(".prev-address-btn");
        if (prevAddressBtn) {
            prevAddressBtn.addEventListener("click", () => {
                // temp-order API에서 받아온 addresses 배열이 있다면,
                // 그걸 이용해서 모달을 열면 됩니다.
                // 이 예시에서는 전역 변수나 state 등에 저장해두고 사용한다고 가정
                openAddressListModal(addresses);
            });
        }
    }
}

// 상품 데이터를 받아서 화면에 렌더링하는 함수
function renderProducts(products) {
    const productList = document.getElementById("product-list");
    const totalCountSpan = document.getElementById("total-product-count");
    if (!productList || !totalCountSpan) return;

    // 실제로는 API에서 couponDiscount 등을 받아야 됨
    // 여기서는 예시로 "사용 가능한 쿠폰 없음" / "쿠폰적용가" 등 하드코딩
    let html = "";
    products.forEach(product => {
        // 임의 쿠폰 적용가
        const couponAppliedPrice = 0;
        // 임의 장바구니 쿠폰 할인액
        const cartCouponDiscount = 0;

        html += `
      <li class="product-item">
        <!-- 왼쪽: 상품 이미지 -->
        <div class="item-img">
          <img src="${product.thumbNailImgUrl}" alt="${product.productName}" />
        </div>
        <!-- 오른쪽: 상품 정보 -->
        <div class="item-info">
          <h3 class="product-name">${product.productName}</h3>
          <p class="product-option">옵션: ${product.name}</p>

          <!-- 가격/수량 + 쿠폰적용가 -->
          <p class="product-price">
            ${(product.finalPrice * product.qty).toLocaleString()}원 / 수량 ${product.qty}개
            <span class="coupon-applied-price">
              쿠폰적용가 : ${couponAppliedPrice.toLocaleString()}원
            </span>
          </p>

          <!-- 쿠폰 정보 -->
          <ul class="coupon-info">
            <li>
              <span class="coupon-label">상품 쿠폰</span>
              <span class="coupon-value">사용 가능한 쿠폰 없음</span>
            </li>
            <li>
              <span class="coupon-label">장바구니 쿠폰</span>
              <span class="coupon-name">사용 가능한 쿠폰 없음</span>
              <span class="coupon-discount-amount">-${cartCouponDiscount.toLocaleString()}원</span>
            </li>
          </ul>
        </div>
      </li>
    `;
    });

    productList.innerHTML = html;
    totalCountSpan.textContent = products.length;
}

// 주소 객체를 받아서 HTML 생성
function createAddressItemHtml(addr, isDefault) {
    // 연락처(전화번호) 두 개를 합쳐 표시 (있을 경우)
    const phoneLine = addr.emergencyPhoneNumber
        ? `${addr.phoneNumber} / ${addr.emergencyPhoneNumber}`
        : (addr.phoneNumber || "");

    // 모양만 간단히 예시
    return `
    <div class="address-item" 
         data-addr='${JSON.stringify(addr)}'>
      <h4>
        ${addr.addressName || "(배송지명 없음)"} / ${addr.receiver || "(수령인 없음)"}
        ${isDefault ? '<span class="default-mark">(기본)</span>' : ''}
      </h4>
      <p>(${addr.zipCode}) ${addr.addressMain} ${addr.addressDetail}</p>
      <p>${phoneLine}</p>
      ${
        !isDefault
            ? `<button type="button" class="delete-address-btn" data-addr-no="${addr.addressNo}">삭제</button>`
            : ""
    }
    </div>
  `;
}

function openAddressListModal(addresses) {
    const modal = document.getElementById("addressListModal");
    if (!modal) return;

    // (1) 기본 주소
    const defaultAddr = addresses.find(a => a.defaultAddressYn === "Y");
    // (2) 그 외 주소 (addressNo 내림차순)
    const otherAddrs = addresses
        .filter(a => a.defaultAddressYn !== "Y")
        .sort((a, b) => b.addressNo - a.addressNo);

    // [기본 배송지 영역]
    const defaultContainer = modal.querySelector(".default-address-container");
    if (defaultAddr) {
        defaultContainer.innerHTML = createAddressItemHtml(defaultAddr, true);
    } else {
        defaultContainer.innerHTML = "기본 배송지가 없습니다.";
    }

    // [나머지 주소 목록]
    const otherList = modal.querySelector(".other-addresses-list");
    // <div>만 쭉 이어붙임 (li 안 쓰기)
    otherList.innerHTML = otherAddrs
        .map(addr => createAddressItemHtml(addr, false))
        .join("");

    // ---- [초기화 부분] ----
    const toggleBtn = modal.querySelector(".toggle-addresses-btn");
    // 매번 모달 열릴 때마다 초기 상태(접힘)로 세팅
    toggleBtn.setAttribute("data-open", "false");
    defaultContainer.classList.remove("with-border");
    otherList.style.display = "none";

    // “다른 배송지 펼쳐보기” 버튼 토글
    toggleBtn.innerHTML = `
      다른 배송지 펼쳐보기
    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" color="primary" class="arrow-icon">
        <g id="weight=regular, fill=false">
            <path id="vector" fill-rule="evenodd" clip-rule="evenodd" d="M12 17.1314L20.5657 8.56569L19.4343 7.43431L12 14.8686L4.5657 7.43431L3.43433 8.56569L12 17.1314Z" fill="black"></path>
        </g>
    </svg>
    `;

    toggleBtn.onclick = () => {
        const isOpen = toggleBtn.getAttribute("data-open") === "true";
        if (isOpen) {
            // 현재 열려있으므로 -> 닫기
            otherList.style.display = "none";
            toggleBtn.innerHTML = `
      다른 배송지 펼쳐보기
    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" color="primary" class="arrow-icon">
        <g id="weight=regular, fill=false">
            <path id="vector" fill-rule="evenodd" clip-rule="evenodd" d="M12 17.1314L20.5657 8.56569L19.4343 7.43431L12 14.8686L4.5657 7.43431L3.43433 8.56569L12 17.1314Z" fill="black"></path>
        </g>
    </svg>
    `;
            toggleBtn.setAttribute("data-open", "false");
            defaultContainer.classList.remove("with-border");
        } else {
            // 현재 닫혀있으므로 -> 열기
            otherList.style.display = "block";
            toggleBtn.innerHTML = `
      다른 배송지 접기
    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" color="primary" class="arrow-icon">
        <g id="weight=regular, fill=false">
            <path id="vector" fill-rule="evenodd" clip-rule="evenodd" d="M12 17.1314L20.5657 8.56569L19.4343 7.43431L12 14.8686L4.5657 7.43431L3.43433 8.56569L12 17.1314Z" fill="black"></path>
        </g>
    </svg>
    `;
            toggleBtn.setAttribute("data-open", "true");
            // 다른 배송지가 1개 이상이면 구분선 추가
            defaultContainer.classList.add("with-border");
        }
    };
    otherList.style.display = "none"; // 초기에는 숨김

    // **클릭 이벤트 등록** : 기본 배송지 + 나머지 배송지
    //   - 전체 .address-item 요소에 이벤트 부여
    const allAddressItems = modal.querySelectorAll(".address-item");
    allAddressItems.forEach(item => {
        item.addEventListener("click", (e) => {
            // 만약 클릭이 "삭제" 버튼이면(이벤트 버블링), 선택 로직 실행 안 하도록
            if (e.target.classList.contains("delete-address-btn")) {
                e.stopPropagation();
                // 삭제 로직 별도 처리
                const addrNo = e.target.dataset.addrNo;
                console.log("삭제 버튼 클릭, 주소번호:", addrNo);
                // TODO: 삭제 API 호출 or UI에서 제거
                return;
            }

            // (1) data-addr에서 주소 JSON 파싱
            const addressJson = item.dataset.addr;
            if (!addressJson) return;
            const addressObj = JSON.parse(addressJson);

            // (2) shipping form에 채워넣기
            fillShippingForm(addressObj);

        });
    });

    // 모달 열기
    modal.style.display = "flex";
}

// 선택된 주소를 existing-address 폼에 반영
function fillShippingForm(addressObj) {
    // 기존 배송지 영역
    const existingAddressSection = document.querySelector('.existing-address');
    if (!existingAddressSection) return;

    // 예: name="addressName", name="receiver" ...
    const addressNameInput = existingAddressSection.querySelector('input[name="addressName"]');
    const receiverInput = existingAddressSection.querySelector('input[name="receiver"]');
    const zipCodeInput = existingAddressSection.querySelector('.zipCodeInput');
    const addressMainInput = existingAddressSection.querySelector('.addressMainInput');
    const addressDetailInput = existingAddressSection.querySelector('.addressDetailInput');

    if (addressNameInput) addressNameInput.value = addressObj.addressName || "";
    if (receiverInput)     receiverInput.value   = addressObj.receiver || "";
    if (zipCodeInput)      zipCodeInput.value    = addressObj.zipCode || "";
    if (addressMainInput)  addressMainInput.value= addressObj.addressMain || "";
    if (addressDetailInput)addressDetailInput.value= addressObj.addressDetail || "";

    // 연락처1
    if (addressObj.phoneNumber) {
        const [p1, p2, p3] = addressObj.phoneNumber.split("-");
        document.querySelector('input[name="tel1_0"]').value = p1 || "";
        document.querySelector('input[name="tel1_1"]').value = p2 || "";
        document.querySelector('input[name="tel1_2"]').value = p3 || "";
    }
    // 연락처2
    if (addressObj.emergencyPhoneNumber) {
        const [p1, p2, p3] = addressObj.emergencyPhoneNumber.split("-");
        document.querySelector('input[name="tel2_0"]').value = p1 || "";
        document.querySelector('input[name="tel2_1"]').value = p2 || "";
        document.querySelector('input[name="tel2_2"]').value = p3 || "";
    }
}

// 결제 금액 요약 정보를 화면에 렌더링 하는 함수
function renderPaymentDetails(res) {
    const paymentDetailsList = document.querySelector(".payment-details ul");
    if (!paymentDetailsList) return;

    // 배송비가 0이면 "무료"로 표시
    const shippingFeeText = res.totalShipFee === 0 ? "무료" : res.totalShipFee.toLocaleString() + "원";

    // HTML 마크업 생성 (예시)
    const html = `
    <li>
      <span class="label">총 상품 금액</span>
      <span class="value">${res.totalPrice.toLocaleString()}원</span>
    </li>
    <li>
      <span class="label">배송비</span>
      <span class="value">${shippingFeeText}</span>
    </li>
    <li>
      <span class="label">상품 할인 금액</span>
      <span class="discount-value">-${res.totalDiscPrice.toLocaleString()}원</span>
    </li>
    <!-- [쿠폰 할인] 토글 영역 -->
    <li class="coupon-discount">
        <!-- (1) 쿠폰 라인: 버튼 + 총 할인 금액 -->
        <div class="coupon-line">
            <button type="button" class="coupon-toggle-btn" data-open="false">
                쿠폰 할인 금액
                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" color="primary" class="arrow-icon">
                    <g id="weight=regular, fill=false">
                        <path id="vector" fill-rule="evenodd" clip-rule="evenodd" d="M12 17.1314L20.5657 8.56569L19.4343 7.43431L12 14.8686L4.5657 7.43431L3.43433 8.56569L12 17.1314Z" fill="black"></path>
                    </g>
                </svg>
            </button>
            <span class="discount-value">-0원</span>
        </div>

        <!-- (2) 쿠폰 상세내역: 초기에는 숨김 -->
        <div class="coupon-breakdown" style="display: none;">
            <div class="coupon-item">
                <span>상품 쿠폰</span>
                <span>-0원</span>
            </div>
            <div class="coupon-item">
                <span>장바구니 쿠폰</span>
                <span>-0원</span>
            </div>
        </div>
    </li>

    <li>
        <span class="label">마일리지 사용</span>
        <span class="value">0P</span>
    </li>
    <li class="final-amount">
      <div class="final-line">
        <span class="label">총 결제 금액</span>
        <span class="value accent">${res.payAmt.toLocaleString()}원</span>
      </div>
    </li>
  `;

    paymentDetailsList.innerHTML = html;
}

function showErrorModal(message) {
    var modal = document.querySelector('.modal');
    var modalBody = modal.querySelector('.modal-body');

    modalBody.textContent = message; // 약관 내용 설정
    modal.style.display = 'flex'; // 모달 표시

    // FIXME 이전 페이지 이동 작업 추가 고려 필요
}

function closeModal() {
    var modal = document.querySelector('.modal');
    modal.style.display = 'none'; // 모달 숨김
    document.querySelectorAll('.modal-overlay').forEach(modal => {
        modal.style.display = 'none';
    });
}