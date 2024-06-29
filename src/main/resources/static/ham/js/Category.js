function categoryUpdate(selectElement) {
    var selectedOption = selectElement.options[selectElement.selectedIndex];
    var catemajorkey = selectedOption.getAttribute('data-catemajorkey');
    $.ajax({
        url: '/cateUpdate',
        type: 'post',
        data: {catemajor: catemajorkey},
        success: function(result){
            let select = $('#categorySelect'); // select 요소 선택
            select.empty();
            result.forEach(function(item) {
                let option = $('<option></option>').text(item);
                select.append(option);
            });
        },
        error: function(error){
            console.log("에러 발생: ", error);
        }
    });
}