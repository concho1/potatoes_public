function updateCheck(){
    var nickname = $("#nickname").val();
    var loc = $("#sample6_address").val();

    if(nickname == ""){
        alert("닉네임을 입력해주세요.")
        $("#nickname").focus();
        return false;
    }else if(loc == ""){
        alert("주소를 입력해주세요");
        return false;
    }else if($('#nicknameError').is(':visible')){
        alert("이미 사용중인 닉네임입니다.");
        $("#nickname").focus();
        return false;
    }
}

$(document).ready(function() {
    $('#nickname').on('keyup',function (){
        var nickname = $(this).val();

        if (nickname === "") {
            $('#nicknameError').hide();
            $('#nicknameGood').hide();
            $('#nicknameEmpty').show();
            return;
        } else {
            $('#nicknameEmpty').hide();
        }

        $.ajax({
            url: '/checkDupNickname',
            type: 'post',
            data: {nickname: nickname},
            dataType: "json",
            success: function(result){
                console.log("받아온 값 : "+result)
                if(result){
                    console.log(result);
                    $('#nicknameError').show();
                    $('#nicknameGood').hide();
                }else {
                    console.log(result);
                    $('#nicknameError').hide();
                    $('#nicknameGood').show();
                }
            },
            error: function(result){
                console.log(result);
                eflag = false;
            }
        });
    })

});
