$(document).ready(function (){
    let $ajax1 = $('#ajax1');
    let $button = $ajax1.find('#fileButtonAjax');
    let $file = $ajax1.find('#fileAjax');
    let $img = $ajax1.find('#imgAjax');
    let $nickname = $ajax1.find('#nicknameAjax');
    let goUrl = '/test-api/image';

    $button.click(function (){
        if(!$file[0].files[0]){
            alert('파일이 비었습니다.');
            return;
        }
        if($nickname.val() === ''){
            alert('닉네임이 비었습니다.');
            return;
        }
        let formData = new FormData();
        formData.append('file', $file[0].files[0]);
        formData.append('nickname', $nickname.val());
        
        let response = ajaxFunction(goUrl, formData);   //ajax 보내고 응답 받기

        if(response.isOk === 'ok'){
            alert('이미지 업로드 성공!');
            // 이미지 화면에 표시
            $img.attr('src', response.imageUrl).removeAttr('hidden');
        }else{
            alert('이미지 업로드 실패 ㅠㅠ');
        }
    });
});



function ajaxFunction(url, formData){
    let responseResult = null;
    let baseUrl = window.location.origin;

    $.ajax({
        url: baseUrl + url,
        type: 'POST',
        data: formData,
        async: false,
        contentType: false,
        processData: false,
        success: function(response) {
            responseResult = response;
        },
        error: function(error) {
            alert('통신 오류: ' + error);
        }
    });
    return responseResult;
}