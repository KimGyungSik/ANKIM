import { selectedAddress } from './addressStore.js';

// // 우편번호 찾기 화면을 넣을 element
// var element_layer = document.getElementById('layer');
//
// function closeDaumPostcode() {
//     // iframe을 넣은 element를 안보이게 한다.
//     element_layer.style.display = 'none';
// }

export async function execDaumPostcode(container) {
    var element_layer = container.querySelector('.layer');
    console.log("addrSearchBtn clicked");
    new daum.Postcode({
        oncomplete: function(data) {
            // 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if(data.userSelectedType === 'R'){
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                    extraAddr += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if(data.buildingName !== '' && data.apartment === 'Y'){
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if(extraAddr !== ''){
                    extraAddr = ' (' + extraAddr + ')';
                }

                // 조합된 참고항목을 해당 필드에 넣는다.
                // document.getElementById("addressMainInput").value = extraAddr;
                container.querySelector('.addressMainInput').value = extraAddr;

            } else {
                // document.getElementById("addressMainInput").value = '';
                container.querySelector('.addressMainInput').value = '';
            }


            // 실제 데이터 저장
            selectedAddress.zipCode = data.zonecode;
            selectedAddress.addressMain = addr;

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            container.querySelector('.zipCodeInput').value = data.zonecode;
            container.querySelector('.addressMainInput').value = data.roadAddress || data.jibunAddress;
            container.querySelector('.addressDetailInput').focus();

            // iframe을 넣은 element를 안보이게 한다.
            // (autoClose:false 기능을 이용한다면, 아래 코드를 제거해야 화면에서 사라지지 않는다.)
            element_layer.style.display = 'none';
        },
        width : '100%',
        height : '100%',
        maxSuggestItems : 5
    }).embed(element_layer);

    // iframe을 넣은 element를 보이게 한다.
    element_layer.style.display = 'block';

    // iframe을 넣은 element의 위치를 화면의 가운데로 이동시킨다.
    initLayerPosition(element_layer);
}

function initLayerPosition(element_layer){
    // 부모 요소(컨테이너)의 현재 너비를 가져온다.
    var parentWidth = element_layer.parentElement.clientWidth;
    // 부모 너비를 그대로 사용하거나 너비를 500으로 사용한다.
    // 단 부모 너비가 500이 넘어가면 500으로 사용하고 부모 너비가 400보다 작으면 그대로 사용 500도 400도 아니라면 400으로 사용
    var width = parentWidth > 500 ? 500 : (parentWidth < 400 ? parentWidth : 400);
    var height = 400; //우편번호서비스가 들어갈 element의 height
    var borderWidth = 1; //샘플에서 사용하는 border의 두께

    // 위에서 선언한 값들을 실제 element에 넣는다.
    element_layer.style.width = width + 'px';
    element_layer.style.height = height + 'px';
    element_layer.style.border = borderWidth + 'px solid';
    // 실행되는 순간의 화면 너비와 높이 값을 가져와서 중앙에 뜰 수 있도록 위치를 계산한다.
    element_layer.style.left = (((window.innerWidth || document.documentElement.clientWidth) - width)/2 - borderWidth) + 'px';
    element_layer.style.top = (((window.innerHeight || document.documentElement.clientHeight) - height)/2 - borderWidth) + 'px';
}