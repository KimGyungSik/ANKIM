function toggleAllChecks(checkAllBox) {
    var checkboxes = document.querySelectorAll("input[name='termsAgreement']");
    checkboxes.forEach(checkbox => {
        checkbox.checked = checkAllBox.checked;
    });
    updateNextButtonState();
}

function updateNextButtonState() {
    var checkboxes = document.querySelectorAll("input[name='termsAgreement']");
    var checkAllBox = document.getElementById("checkAll");
    var requiredCheckboxes = document.querySelectorAll("input[name='termsAgreement'][data-required='true']");

    // 모든 체크박스가 체크되었는지 확인
    var allChecked = Array.from(checkboxes).every(checkbox => checkbox.checked);

    // 필수 약관이 모두 체크되었는지 확인
    var requiredChecked = Array.from(requiredCheckboxes).every(checkbox => checkbox.checked);

    // 전체 동의 체크 상태 업데이트
    checkAllBox.checked = allChecked;

    // 다음 버튼 활성화/비활성화 처리
    var nextButton = document.getElementById("nextButton");
    if (requiredChecked) {
        nextButton.classList.add("active"); // 활성화 스타일 추가
        nextButton.disabled = false;       // 버튼 활성화
    } else {
        nextButton.classList.remove("active"); // 활성화 스타일 제거
        nextButton.disabled = true;           // 버튼 비활성화
    }
}

/*
// 테스트용
function updateNextButtonState() {
    // "다음" 버튼 항상 활성화
    const nextButton = document.getElementById("nextButton");
    nextButton.classList.add("active"); // 활성화 스타일 추가
    nextButton.disabled = false;       // 버튼 활성화
}
*/

function showModal(contents) {
    var modal = document.querySelector('.modal');
    var modalBody = modal.querySelector('.modal-body');

    modalBody.textContent = contents.getAttribute('data-contents'); // 약관 내용 설정
    modal.style.display = 'flex'; // 모달 표시
}

function closeModal() {
    var modal = document.querySelector('.modal');
    modal.style.display = 'none'; // 모달 숨김
}

function submitAgreements() {
    const errorElement = document.getElementById("termsAgreementsError");

    // 기존 에러 메시지 초기화
    errorElement.textContent = '';
    errorElement.style.display = 'none';

    var termsAgreements = Array.from(document.querySelectorAll("input[name='termsAgreement']")).map(input => ({
        no: input.value, // 약관 번호
        name: input.getAttribute("data-name"), // 약관명
        agreeYn: input.checked ? "Y" : "N", // 체크 여부
        level: input.getAttribute("data-level"), // 약관 레벨
        termsYn: input.getAttribute("data-termsYn") // 필수 여부
    }));

    fetch("/api/member/terms-next", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(termsAgreements)
    })
        .then(response => {
            if (response.ok) {
                // 성공 시 다음 페이지로 이동
                window.location.href = "/mail/mailVerification"; // FIXME: 다음 페이지 URL로 변경
            } else {
                // 실패 시 에러 메시지 표시
                return response.json().then(errorData => {
                    handleErrors(errorData);
                });
            }
        })
        .catch(error => {
            handleErrors("서버와의 통신 중 문제가 발생했습니다.");
        });
}

function handleErrors(errorData) {
    const errorElement = document.getElementById("termsAgreementsError");

    // 기존 에러 메시지 초기화
    errorElement.textContent = '';
    errorElement.style.display = 'none';

    if (errorData && errorData.message) {
        // 서버에서 반환된 에러 메시지를 표시
        errorElement.textContent = errorData.message;
        errorElement.style.display = 'block';
    } else {
        // 기본 오류 메시지 처리
        errorElement.textContent = '알 수 없는 오류가 발생했습니다. 다시 시도해주세요.';
        errorElement.style.display = 'block';
    }
}