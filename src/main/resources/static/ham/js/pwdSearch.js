let flag = false;
let eflag = false;
let emailchk = false;
let email_regex = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/;

$('#email').on('keyup',function (){
    var email = $(this).val();
    if(email_regex.test(email)){
        $('#emailError').hide();
        $('#sendEmail').prop('disabled', false);
    }else{
        $('#emailError').show();
        $('#sendEmail').prop('disabled', true);
        return;
    }

});

$('#sendEmail').on('click', function(){

     var email = $('#email').val();
     var nickname =$('#nickname').val();

    $.ajax({
        url: '/checkPwdInfo',
        type: 'post',
        data: {
            nickname: nickname,
            email: email
        },
        dataType: "json",
        success: [function(result){
            if(!result){
                alert("일치하는 사용자를 찾을수없습니다.");
                return;
            }

            if(eflag){
                alert('이메일을 보내는 중...');
            }else{
                eflag = true;
                $.ajax({
                    url: '/sendPwdEmail',
                    type: 'post',
                    data: {email : email},
                    dataType: "json",
                    success: [function(map){
                        alert(map.message);
                        console.log(map.message);
                        eflag = false;
                    }],
                    error: function(resp){
                        console.log(resp);
                        eflag = false;
                    }
                })
            }

            if(flag){
                return;
            }else{
                flag = true;
            }

            let input = "";
            input += "<div class='row mb-3'>";
            input += "<label class='col-sm-2 col-form-label'>인증번호 확인</label>"
            input += "<div class='col-sm-10'>";
            input += "<div class='input-group'>";
            input += "<input type='text' name='checkCode' class='form-control' id='checkCode'><br>";
            input += "<input type='button' value='인증번호 확인' class='btn btn-primary' onclick='al()' id='checkCodeButton' >";
            input += "</div>";
            input += "</div>";
            input += "</div>";

            $('#emailError').after(input);
        }],
        error: function(resp){
            console.log(resp);

        }
    })

});


function al(){

    var code = ($('#checkCode').val());

    var data = {
        code: code
    };

    $.ajax({
        url: '/codeCatch',
        type: 'post',
        data: $.param(data),
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        dataType: "json",
        success: [function(map){
            if(map.good ==  null){
                alert(map.fail);
                $('#checkCode').focus();
            }else{
                alert(map.good);
                $('#checkCode').prop('readonly', true).css('background-color', 'lightgray');
                $('#checkCodeButton').prop('disabled', true).css('background-color', 'lightgray');
                $('#email').prop('readonly', true).css('background-color', 'lightgray !important');
                $('#sendEmail').prop('disabled', true).css('background-color', 'lightgray');
                eflag = false;
                emailchk = true;
            }
        }],
        error: function(resp){
            console.log(resp);
            eflag = false;
        }
    })
}