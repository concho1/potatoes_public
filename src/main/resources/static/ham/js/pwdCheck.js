function pwdCheck() {
    var pwd = $("#pwd").val();
    var pwdchk = $("#pwd_chk").val();

    if (pwd == "") {
        alert("비밀번호를 입력해주세요");
        $("#pwd").focus();
        return false;
    } else if (pwdchk == "") {
        alert("비밀번호가 일치하는지 확인주세요");
        $("#pwd_chk").focus();
        return false;
    } else if ($('#pwdError').is(':visible')) {
        alert("비밀번호가 일치하지않습니다.");
        $("#pwd_chk").focus();
        return false;
    } else if ($('#pwdlenError').is(':visible')) {
        alert("비밀번호는 5자리이상 20자리 이하로 입력해주세요.");
        $("#pwd_chk").focus();
        return false;
    } else if(pwd.length != pwdchk.length){
        alert("비밀번호가 일치하지않습니다.");
        $("#pwd_chk").focus();
        return false;
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

        $.ajax({
            url: '/pwdDupPwd',
            type: 'post',
            data: {pwd: pwd},
            dataType: "json",
            success: function(result){
                console.log("받아온 값 : "+result)
                if(result){
                    $('#pwdDupError').show();
                    $('#pwdBtn').prop('disabled', true);
                }else {
                    $('#pwdDupError').hide();
                    $('#pwdBtn').prop('disabled', false);
                }
            },
            error: function(result){
                console.log(result);
                $('#sendEmail').prop('disabled', true);
                eflag = false;
            }
        });
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
});