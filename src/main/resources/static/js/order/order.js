document.addEventListener("DOMContentLoaded", () => {
    const tabs = document.querySelectorAll(".shipping-tab .tab-item");
    const existingAddress = document.querySelector(".existing-address");
    const newAddress = document.querySelector(".new-address");

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
});