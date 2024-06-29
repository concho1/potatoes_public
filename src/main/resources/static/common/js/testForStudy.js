// 중복 업로드 방지용 flag 비동기일때만 필요 아래 예시는 동기 방식임
let uploadingFlag = false;

$(document).ready(function (){
    // 아래 과정은 빠른 탐색을 위해 사용
    // id = ajax1 인 요소 가져오기 => (tip) 변수이름에 $ 는 jQuery 요소임을 의미
    let $ajax1 = $('#ajax1');
    // ajax1 하위 요소들 중
    // id = fileButtonAjax,
    // id = fileAjax,
    // id = imgAjax,
    // id = nicknameAjax 인 요소 가져오기
    let $button = $ajax1.find('#fileButtonAjax');
    let $file = $ajax1.find('#fileAjax');
    let $img = $ajax1.find('#imgAjax');
    let $nickname = $ajax1.find('#nicknameAjax');
    // 어디로 보내는지
    let goUrl = '/test-api/image';

    // 버튼 요소가 클릭되면 실행
    $button.click(function (){
        // input 값 검증
        if(!$file[0].files[0]){
            alert('파일이 비었습니다.');
            return;
        }
        if($nickname.val() === ''){
            alert('닉네임이 비었습니다.');
            return;
        }

        // ajax 보낼 form 데이터 만들기
        let formData = new FormData();
        // $file[0] 는 jQuery 요소 => 순수 js 로 변경
        // files[0]는 파일 중 사용자가 선택한 첫번쨰 파일 선택
        formData.append('file', $file[0].files[0]);
        formData.append('nickname', $nickname.val());

        // ===========================================================
        // ajax 요청 보내기 post 방식임에 유의
        let response = ajaxFunction(goUrl, formData);

        //ajax 요청 결과 처리
        if(response.isOk === 'ok'){
            alert('이미지 업로드 성공!');
            // 이미지 화면에 표시
            $img.attr('src', response.imageUrl).removeAttr('hidden');
        }else{
            alert('이미지 업로드 실패 ㅠㅠ');
        }
    });
});




// ajax 많이 쓸거같아서 미리 함수로 만들어놓음
function ajaxFunction(url, formData){
    let responseResult = null;
    // 현제 프로젝트 baseUrl 동적으로 얻기 jsp 의 <%=request.getContextPath()%> 와 동일
    let baseUrl = window.location.origin;
    console.log(baseUrl);

    // Ajax 요청
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
            alert('오류'+error);
        }
    });
    return responseResult;
}