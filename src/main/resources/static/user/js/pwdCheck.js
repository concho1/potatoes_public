function updatePwd(){
    const oldpwd = $('#currentPassword').val();
    const pwd = $('#pwd').val();
    const pwdchk = $("#pwd_chk").val();

    if(oldpwd == ""){
        alert("기존 비밀번호를 입력해주세요");
        $("#currentPassword").focus();
        return false;
    }else if(pwd == ""){
        alert("새 비밀번호를 입력해주세요");
        $("#pwd").focus();
        return false;
    }else if(pwd == oldpwd){
        alert("기존 비밀번호와 새 비밀번호가 일치합니다. 다시 입력해주세요.");
        $("#pwd").focus();
        return false;
    }else if(pwd != pwdchk){
        alert("새 비밀번호와 비밀번호 확인이 일치하는지 확인주세요");
        $("#pwd_chk").focus();
        return false;
    }else if(pwdchk == ""){
        alert("비밀번호가 일치하는지 확인주세요");
        $("#pwd_chk").focus();
        return false;
    }else if($('#pwdError').is(':visible')){
        alert("비밀번호 확인이 일치하지않습니다.");
        $("#pwd_chk").focus();
        return false;
    }
}

$(document).ready(function() {

    $('#pwd').on('keyup',function () {
        const pwd = $("#pwd").val();
        const oldpwd = $('#currentPassword').val();

        if (pwd.length < 5 || pwd.length > 20) {
            $('#pwdlenError').show();
            $('#pwdSame').hide();
        } else if(pwd == oldpwd){
            $('#pwdSame').show();
            $('#pwdlenError').hide();
        }else {
            $('#pwdSame').hide();
            $('#pwdlenError').hide();
        }

    });

    $('#pwd_chk').on('keyup',function () {
        const pwd = $("#pwd").val();
        const pwdchk = $("#pwd_chk").val();

        if (pwd != pwdchk) {
            $('#pwdError').show();
        } else {
            $('#pwdError').hide();

        }
    });

});
