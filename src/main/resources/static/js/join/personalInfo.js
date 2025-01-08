function initializePersonalInfoResources() {
    const form = document.getElementById("personalInfoForm");

    form.addEventListener("submit", (event) => {
        event.preventDefault();

        const formData = new FormData(form);
        const formObject = Object.fromEntries(formData.entries());

        fetch("/api/member/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(formObject),
        })
            .then(response => {
                if (response.ok) {
                    alert("회원가입이 완료되었습니다!");
                    window.location.href = "/";
                } else {
                    throw new Error("회원가입 중 오류 발생");
                }
            })
            .catch(error => {
                console.error("회원가입 중 오류:", error);
            });
    });
}