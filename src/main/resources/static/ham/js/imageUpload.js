$(document).ready(function(){
    $('#fileAjax').on('change', function(event){
        let input = event.target;
        if (input.files && input.files[0]) {
            let reader = new FileReader();
            reader.onload = function(e) {
                $('#preview-image').attr('src', e.target.result).show();
            }
            reader.readAsDataURL(input.files[0]);
        }
    });
});