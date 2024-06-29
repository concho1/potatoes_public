function boxcheck(){
    if(!$("input[name='chk']").is(":checked")) {
        alert("탈퇴 안내 사항에 동의하셔야합니다.");
        return false;
    }else {
        return true;
    }
}