// 썸네일 이미지 리스트
let thumbnailFiles = [];
// 상세 이미지 리스트
let detailFiles = [];

// 썸네일 이미지 업로드 핸들러
function handleThumbnailUpload(event) {
    const files = Array.from(event.target.files);

    if (thumbnailFiles.length + files.length > 6) {
        alert("썸네일 이미지는 최대 6장까지 업로드 가능합니다.");
        return;
    }

    files.forEach(file => {
        const reader = new FileReader();
        reader.onload = () => {
            const img = document.createElement("img");
            img.src = reader.result;
            img.onclick = () => removeThumbnail(file);
            document.getElementById("thumbnailPreview").appendChild(img);
        };
        reader.readAsDataURL(file);
    });

    thumbnailFiles = thumbnailFiles.concat(files);
}

// 상세 이미지 업로드 핸들러
function handleDetailUpload(event) {
    const files = Array.from(event.target.files);

    if (detailFiles.length + files.length > 10) {
        alert("상세 이미지는 최대 10장까지 업로드 가능합니다.");
        return;
    }

    files.forEach(file => {
        const reader = new FileReader();
        reader.onload = () => {
            const img = document.createElement("img");
            img.src = reader.result;
            img.onclick = () => removeDetail(file);
            document.getElementById("detailPreview").appendChild(img);
        };
        reader.readAsDataURL(file);
    });

    detailFiles = detailFiles.concat(files);
}

// 썸네일 이미지 삭제
function removeThumbnail(file) {
    thumbnailFiles = thumbnailFiles.filter(f => f !== file);
    renderPreview("thumbnailPreview", thumbnailFiles);
}

// 상세 이미지 삭제
function removeDetail(file) {
    detailFiles = detailFiles.filter(f => f !== file);
    renderPreview("detailPreview", detailFiles);
}

// 미리보기 렌더링 함수
function renderPreview(containerId, files) {
    const container = document.getElementById(containerId);
    container.innerHTML = ""; // 기존 미리보기 초기화

    files.forEach(file => {
        const reader = new FileReader();
        reader.onload = () => {
            const img = document.createElement("img");
            img.src = reader.result;
            img.onclick = () => {
                if (containerId === "thumbnailPreview") {
                    removeThumbnail(file);
                } else {
                    removeDetail(file);
                }
            };
            container.appendChild(img);
        };
        reader.readAsDataURL(file);
    });
}

// 최종 데이터 서버로 전달
async function submitImages() {
    if (thumbnailFiles.length < 1) {
        alert("썸네일 이미지는 최소 1장 업로드해야 합니다.");
        return;
    }
    if (detailFiles.length < 1) {
        alert("상세 이미지는 최소 1장 업로드해야 합니다.");
        return;
    }

    const formData = new FormData();

    thumbnailFiles.forEach(file => formData.append("thumbnailImages", file));
    detailFiles.forEach(file => formData.append("detailImages", file));

    try {
        const response = await fetch("/api/products/new", {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            throw new Error("이미지 업로드 실패");
        }

        const result = await response.json();
        console.log("업로드 성공:", result);
    } catch (error) {
        console.error("업로드 오류:", error);
    }
}