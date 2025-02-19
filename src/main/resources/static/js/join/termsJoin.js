async function toggleAllChecks(checkAllBox) {
    var checkboxes = document.querySelectorAll("input[name='termsAgreement']");
    checkboxes.forEach(checkbox => {
        checkbox.checked = checkAllBox.checked;
    });
    await updateNextButtonState();
}

async function updateNextButtonState() {
    var checkboxes = document.querySelectorAll("input[name='termsAgreement']");
    var checkAllBox = document.getElementById("checkAll");
    var requiredCheckboxes = document.querySelectorAll("input[name='termsAgreement'][data-required='true']");

    var allChecked = Array.from(checkboxes).every(checkbox => checkbox.checked);
    var requiredChecked = Array.from(requiredCheckboxes).every(checkbox => checkbox.checked);

    checkAllBox.checked = allChecked;

    var nextButton = document.getElementById("nextButton");
    if (requiredChecked) {
        nextButton.classList.add("active");
        nextButton.disabled = false;
    } else {
        nextButton.classList.remove("active");
        nextButton.disabled = true;
    }
}

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

async function submitAgreements() {
    var errorElement = document.getElementById("termsAgreementsError");
    errorElement.textContent = '';
    errorElement.style.display = 'none';

    var termsAgreements = Array.from(document.querySelectorAll("input[name='termsAgreement']")).map(input => ({
        no: input.value,
        name: input.getAttribute("data-name"),
        agreeYn: input.checked ? "Y" : "N",
        level: input.getAttribute("data-level"),
        termsYn: input.getAttribute("data-termsYn"),
    }));

    try {
        var response = await fetch("/api/member/terms-next", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(termsAgreements),
        });

        if (!response.ok) {
            var errorData = await response.json();
            handleErrors(errorData);
        } else {
            await loadMailVerificationPage();
        }
    } catch (error) {
        handleErrors("서버와의 통신 중 문제가 발생했습니다.");
    }
}

function handleErrors(errorData) {
    var errorElement = document.getElementById("termsAgreementsError");

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

// 메일 인증 화면 로드 함수
 function loadMailVerificationResources() {
    var cssLink = document.createElement("link");
    cssLink.rel = "stylesheet";
    cssLink.href = "/css/join/mailVerification.css";
    document.head.appendChild(cssLink);

    var script = document.createElement("script");
    script.src = "/js/join/mailVerification.js";
    script.defer = true;
    script.onload = () => {
        initializeMailVerification(); // 동적 로드 후 초기화 함수 호출
    };
    document.body.appendChild(script);
}

async function loadMailVerificationPage() {
    try {
        var response = await fetch("/mail/mailVerificationFragment", {
            method: "GET",
            headers: {
                "Content-Type": "text/html"
            }
        });

        if (!response.ok) {
            throw new Error("서버 응답이 올바르지 않습니다.");
        }

        var html = await response.text();
        document.querySelector("main").innerHTML = html;
        await loadMailVerificationResources();
    } catch (error) {
        console.error("화면 전환 중 오류 발생:", error);
    }
}