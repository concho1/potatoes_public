function loginCheck(){
    var id = $("#id").val();
    var pwd = $("#pwd").val();
    let email_regex = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/;

    if(id == ""){
        alert("아이디를 입력해주세요.")
        $("#id").focus();
        return false;
    }else if(email_regex.test(id) == false){
        alert("아이디는 이메일 형식으로 입력해주세요.")
        $("#id").focus();
        return false;
    }else if(pwd == ""){
        alert("비밀번호를 입력해주세요.")
        $("#pwd").focus();
        return false;
    }else if(pwd.length < 5 || pwd.length > 20){
        alert("비밀번호는 5자이상 20자이하로 입력해주세요.")
        $("#pwd").focus();
        return false;
    }

    return true;
}