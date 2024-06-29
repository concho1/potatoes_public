$(document).ready(function() {

    // 프로필 이미지 변경 버튼 클릭 시 파일 입력 요소 클릭
    $('#profileImage').click(function(event) {
        event.preventDefault(); // event 의 기본 동작 방지
        $('#fileInput').click();
    });

    // 파일 선택 시
    $('#fileInput').change(function(event) {
        var input = event.target;
        if (input.files && input.files[0]) {
            console.log(input.files[0]);
            // 파일이 선택되면 해당 파일을 프로필 이미지로 설정
            $('#photo').attr('src', URL.createObjectURL(input.files[0]));
            // removeImage를 눌러 removeFlag 값이 false가 되었을 때 프로필 사진이 변경됨
            $('#removeFlag').val('false');

        }
    });

    // 파일 삭제 시
    $('#removeImage').on('click', function(event) {
        event.preventDefault();
        removeProfileImage();
    });

});
