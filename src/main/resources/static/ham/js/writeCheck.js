function writeCheck(){
    var minor_cate = $("#categorySelect").val();
    var title = $("#title").val();
    var cont = $("#cont").val();

    if(minor_cate == "대분류를 선택해주세요."){
        alert("물건의 대,소 분류를 해주세요");
        return false;
    }else if(title == ""){
        alert("물건이름을 작성해주세요");
        return false;
    }else if(cont == ""){
        alert("물건의 특징을 소개해주세요!");
        return false;
    }else{
        return true;
    }
}