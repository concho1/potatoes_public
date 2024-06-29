function searchPwdCheck(){
    var nickname = $("#nickname").val()
    var email = $("#email").val()

    if(nickname == ""){
        alert("닉네임을 입력해주세요");
        $("#nickname").focus();
        return false;
    }else if(email == ""){
        alert("이메일을 입력해주세요");
        $("#email").focus();
        return false;
    }else if(!emailchk){
        alert("이메일 인증을 진행해주세요");
        return false;
    }else{
        return true;
    }
}