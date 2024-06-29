$(document).ready(function() {
    $("#cbx_chkAll").click(function() {
        if($("#cbx_chkAll").is(":checked")) {
            $("input[name=chk1], input[name=chk2]").prop("checked", true);
        } else {
            $("input[name=chk1], input[name=chk2]").prop("checked", false);
        }
        checkAllSelected();
    });

    $("input[name=chk1], input[name=chk2]").click(function() {
        checkAllSelected();
    });

    function checkAllSelected() {
        var total = $("input[name=chk1], input[name=chk2]").length;
        var checked = $("input[name=chk1]:checked, input[name=chk2]:checked").length;

        if (total != checked) {
            $("#cbx_chkAll").prop("checked", false);
        } else {
            $("#cbx_chkAll").prop("checked", true);
        }

        if (checked == total) {
            $("#terchk").prop("checked", true);
        }
    }
});

function boxcheck() {
    if (!$("input[name='chk1']").is(":checked") || !$("input[name='chk2']").is(":checked")) {
        alert("필수 동의사항에 전부 동의하셔야합니다.");
        return false;
    } else {
        $("#verticalycentered").modal('hide');
        $("#terchk").prop("checked", true).prop("disabled",true);
        return true;
    }
}
