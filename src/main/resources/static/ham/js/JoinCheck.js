function joinCheck(){
    var id = $("#email").val();
    var pwd = $("#pwd").val();
    var pwdchk = $("#pwd_chk").val();
    var nickname = $("#nickname").val();
    var loc = $("#sample6_address").val();

    if(id == ""){
        alert("아이디를 입력해주세요.");
        $("#email").focus();
        return false;
    }else if(pwd == ""){
        alert("비밀번호를 입력해주세요");
        $("#pwd").focus();
        return false;
    }else if(pwdchk == ""){
        alert("비밀번호가 일치하는지 확인주세요");
        $("#pwd_chk").focus();
        return false;
    }else if(nickname == ""){
        alert("닉네임을 입력해주세요.");
        $("#nickname").focus();
        return false;
    }else if(loc == ""){
        alert("주소를 입력해주세요");
        return false;
    }else if($('#pwdError').is(':visible')){
        alert("비밀번호가 일치하지않습니다.");
        $("#pwd_chk").focus();
        return false;
    }else if(!emailchk){
        alert("이메일 인증을 진행해주세요.");
        $("#pwd_chk").focus();
        return false;
    }else if(!$("#terchk").is(":checked")){
        alert("필수동의 사항에 체크해주세요.")
        return false
    }else if($("#nicknameError").is(':visible')){
        alert("이미 존재하는 닉네임입니다.");
        return false;
    }else if(pwd.length != pwdchk.length){
        alert("비밀번호가 일치하지않습니다.");
        $("#pwd_chk").focus();
        return false;
    } else{
        $('.needs-validation').submit();
        return true;
    }


}
$(document).ready(function() {
    $('#pwd').on('keyup',function () {
        var pwd = $("#pwd").val();

        if (pwd.length < 5 || pwd.length > 20) {
            $('#pwdlenError').show();
        } else {
            $('#pwdlenError').hide();

        }
    });

    $('#pwd_chk').on('keyup',function () {
        var pwd = $("#pwd").val();
        var pwdchk = $("#pwd_chk").val();

        if (pwd != pwdchk) {
            $('#pwdError').show();
        } else {
            $('#pwdError').hide();

        }
    });

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
