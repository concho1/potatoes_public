$(document).ready(function (){
    $('.seoul').on('click', function(){
        let actId = $(this).find('td').eq(0).text();
        alert('클릭됨' + actId);
    });
});