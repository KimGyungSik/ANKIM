import { fetchWithAccessToken } from '../utils/fetchUtils.js';

document.addEventListener("DOMContentLoaded", async () => {
    // [A] 탈퇴사유 선택 섹션
    var leaveSection        = document.getElementById("leaveSection");
    var leaveReasonList     = document.getElementById("leaveReasonList");
    var etcReasonCheck      = document.getElementById("etcReasonCheck");
    var etcTextareaWrapper  = document.getElementById("etcTextareaWrapper");
    var etcReasonTextarea   = document.getElementById("etcReasonTextarea");
    var etcReasonCount      = document.getElementById("etcReasonCount");

    var leaveNextBtn        = document.getElementById("leaveNextBtn");
    var leaveCancelBtn      = document.getElementById("leaveCancelBtn");

    // [B] 탈퇴 약관/비밀번호 섹션
    var leaveConfirmSection = document.getElementById("leaveConfirmSection");
    var leaveTermContainer  = document.getElementById("leaveTermContainer");
    var leaveCancelBtn2     = document.getElementById("leaveCancelBtn2");
    var leaveBtn            = document.getElementById("leaveBtn");
    var agreeLeaveTermChk   = document.getElementById("agreeLeaveTerm");

    // [C] 모달 관련 요소
    var modal               = document.getElementById("leaveModal");
    var leaveModalConfirmBtn = document.getElementById("leaveModalConfirmBtn");
    var leaveModalCancelBtn  = document.getElementById("leaveModalCancelBtn");
    var modalCloseButton     = modal.querySelector(".close-button");


    // 최종 서버에 보낼 객체
    let leaveRequest = {
        leaveReasonNo : null,  // 사유 번호(예: DB PK)
        leaveReason   : null,  // 사유 문자열("사고 싶은 상품이 없어서", "기타", 등)
        leaveMessage  : "",    // 기타 사유 입력
        agreeYn       : false, // 탈퇴 약관 동의 여부
        password      : "",    // 비밀번호
    };

    /**
     * "다음" 버튼 활성/비활성 로직
     */
    function updateNextButtonState() {
        // 아무 사유도 선택되지 않은 경우
        if (!leaveRequest.leaveReason) {
            leaveNextBtn.classList.add("inactive");
            return;
        }
        // "기타"인 경우, textarea 내용이 없으면 inactive 처리
        if (leaveRequest.leaveReason === "기타" && leaveRequest.leaveMessage.trim().length === 0) {
            leaveNextBtn.classList.add("inactive");
        } else {
            leaveNextBtn.classList.remove("inactive");
        }
    }

    // [1] /api/mypage 로 로그인 아이디 가져오기 (아이디 표시)
    try {
        var mypageRes = await fetchWithAccessToken("/api/mypage", { method: "GET" });
        if (mypageRes.code === 200 && mypageRes.data) {
            document.getElementById("loginIdConfirm").textContent = mypageRes.data.loginId || "";
        }
    } catch (err) {
        console.error(err);
    }

    // [2] /api/leaveReason 로 탈퇴 사유 + 약관 가져오기
    try {
        var res = await fetchWithAccessToken("/api/leaveReason", {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });
        if (res.code === 200 && res.data) {
            var { reason: reasons, leaveTerm: leaveTerms } = res.data;

            // (a) 일반 탈퇴 사유만 렌더링 (기타 제외)
            // 기타 항목은 별도 체크박스(etcReasonCheck)로 처리
            var normalReasons = reasons.filter(r => r.reason.trim() !== "기타");

            normalReasons.forEach((r) => {
                // 각 사유를 감싸는 div
                var itemDiv = document.createElement("div");
                itemDiv.className = "leave item-info";

                // 체크박스
                var checkbox = document.createElement("input");
                checkbox.className = "leave-reasons"
                checkbox.type  = "checkbox";
                checkbox.name  = "leaveReason"; // 단일 선택처럼 동작하려면 동일 name
                checkbox.id    = `reason_${r.no}`;

                // 체크박스에 no와 reason 저장
                checkbox.dataset.reasonNo = r.no;       // ex) 1, 2, ...
                checkbox.dataset.reason   = r.reason;   // ex) "사고 싶은 상품이 없어서"

                // 라벨
                var label = document.createElement("label");
                label.htmlFor = checkbox.id;
                label.textContent = r.reason;

                itemDiv.appendChild(label);
                itemDiv.appendChild(checkbox);

                // 체크 이벤트 (단일 선택)
                checkbox.addEventListener("change", function() {
                    // 다른 체크박스 해제 + "기타" 체크 해제
                    document.querySelectorAll('input[name="leaveReason"]').forEach(cb => {
                        if (cb !== this) cb.checked = false;
                    });
                    etcReasonCheck.checked = false;

                    // leaveRequest에 저장
                    if (this.checked) {
                        leaveRequest.leaveReasonNo = this.dataset.reasonNo;
                        leaveRequest.leaveReason   = this.dataset.reason;
                    } else {
                        leaveRequest.leaveReasonNo = null;
                        leaveRequest.leaveReason   = null;
                    }
                    updateNextButtonState();
                });

                // DOM에 추가
                leaveReasonList.appendChild(itemDiv);
            });

            // (b) "기타" 체크박스는 이미 HTML에 있음 -> 이벤트 연결
            etcReasonCheck.addEventListener("change", function() {
                // 다른 체크박스 해제
                document.querySelectorAll('#leaveReasonList input[name="leaveReason"]').forEach(cb => {
                    cb.checked = false;
                });

                if (this.checked) {
                    // reasonNo를 기타용으로 설정(서버 DB에서 "기타" 항목의 no가 있으면 설정)
                    // 없으면 leaveReasonNo = null 로 두어도 됨
                    var etcItem = reasons.find(r => r.reason.trim() === "기타");
                    if (etcItem) {
                        leaveRequest.leaveReasonNo = etcItem.no;
                    } else {
                        leaveRequest.leaveReasonNo = null;
                    }
                    leaveRequest.leaveReason = etcItem.reason;

                    // textarea 표시
                    etcTextareaWrapper.style.display = "block";
                } else {
                    leaveRequest.leaveReasonNo = null;
                    leaveRequest.leaveReason   = null;
                }
                updateNextButtonState();
            });

            // (c) 기타 textarea 입력 시 -> leaveRequest.leaveMessage 업데이트 + 글자수 표시
            etcReasonTextarea.addEventListener("input", function() {
                if (this.value.length > 500) {
                    this.value = this.value.slice(0, 500);
                }
                leaveRequest.leaveMessage = this.value;
                etcReasonCount.textContent = `${this.value.length}/500`;
                updateNextButtonState();
            });

            // (d) 탈퇴 약관(leaveTerm) 표시
            leaveTerms.forEach(term => {
                var termDiv = document.createElement("div");
                termDiv.className = "leaveTermBox";

                // 제목
                var title = document.createElement("h4");
                title.textContent = term.name;
                termDiv.appendChild(title);

                // 내용 (줄바꿈 반영 위해 white-space: pre-wrap)
                var contents = document.createElement("div");
                contents.className = "leaveTermContent";
                contents.style.whiteSpace = "pre-wrap";
                contents.textContent = term.contents;
                termDiv.appendChild(contents);

                leaveTermContainer.appendChild(termDiv);
            });

            // 초기 화면: 탈퇴 사유 섹션 보이기
            leaveSection.style.display = "block";
        } else {
            alert("탈퇴 사유를 불러오는데 실패했습니다.");
            window.location.href = "/mypage/edit/info";
        }
    } catch (error) {
        alert("탈퇴 사유를 불러오는데 오류가 발생했습니다.");
        window.location.href = "/mypage/edit/info";
    }

    // [3] "다음" 버튼 -> 약관/비밀번호 섹션으로 전환
    leaveNextBtn.addEventListener("click", () => {
        console.log("leaveRequest.leaveReason =", leaveRequest.leaveReason);
        console.log("leaveRequest.leaveMessage =", leaveRequest.leaveMessage);

        // 사유가 전혀 선택되지 않았으면
        if (!leaveRequest.leaveReason) {
            alert("탈퇴 사유를 선택해주세요.");
            return;
        }
        // "기타"인 경우, textarea에 내용 필수
        if (leaveRequest.leaveReason === "기타" && !leaveRequest.leaveMessage.trim()) {
            alert("기타 사유를 입력해주세요.");
            return;
        }
        // 섹션 전환
        leaveSection.style.display = "none";
        leaveConfirmSection.style.display = "block";
    });

    // [4] 취소 버튼 -> 마이페이지 수정으로 돌아가기
    leaveCancelBtn.addEventListener("click", () => {
        window.location.href = "/mypage/edit/info";
    });
    leaveCancelBtn2?.addEventListener("click", () => {
        window.location.href = "/mypage/edit/info";
    });


    // [5] 모달 열기
    leaveBtn.addEventListener("click", () => {
        // 모달 열기
        modal.style.display = "flex";
    });

    // 모달의 취소 버튼 (leaveModalCancelBtn)와 close 버튼 클릭 시 모달 닫기
    leaveModalCancelBtn.addEventListener("click", () => {
        modal.style.display = "none";
    });
    modalCloseButton.addEventListener("click", () => {
        modal.style.display = "none";
    });

    // [6] "탈퇴하기"버튼
    leaveModalConfirmBtn.addEventListener("click", async () => {
        // 모달 닫기
        modal.style.display = "none";

        // 비밀번호
        var passwordInput = document.getElementById("password");
        leaveRequest.password = passwordInput.value.trim();
        if (!leaveRequest.password) {
            alert("비밀번호를 입력해주세요.");
            return;
        }

        // 약관 동의 여부(필수 체크박스가 있다면 여기서 확인)
        if (!agreeLeaveTermChk.checked) {
            alert("탈퇴 약관에 동의가 필요합니다.");
            return;
        }
        leaveRequest.agreeYn = true;

        console.log("최종 leaveRequest:", leaveRequest);

        // 서버로 최종 전송
        try {
            var res = await fetchWithAccessToken("/api/leave/info", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(leaveRequest),
            });
            if (res.code === 200) {
                alert("회원 탈퇴가 완료되었습니다.");
                localStorage.removeItem("access");
                window.location.href = "/";
            } else {
                alert("회원 탈퇴에 실패했습니다: " + res.message);
            }
        } catch (err) {
            console.error(err);
            alert("회원 탈퇴 요청 중 오류가 발생했습니다.");
        }
    });
});