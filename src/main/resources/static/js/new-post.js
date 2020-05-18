document.addEventListener("DOMContentLoaded", function() {
    let textArea = document.getElementById("newPostTextArea")
    let previewDiv = document.getElementById("newPostPreview")

    document.getElementById("write").onclick = function() {
        textArea.style.display = "initial"
        previewDiv.style.display = "none"
    };

    document.getElementById("preview").onclick = function() {
        previewDiv.innerHTML = textArea.value

        textArea.style.display = "none"
        previewDiv.style.display = "initial"
    };
});
