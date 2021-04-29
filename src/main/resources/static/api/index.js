$(function() {
    $(".single_nav_dd").click(function(e) {
        var operVal = $(this).attr("operVal");
        //console.log(operVal);
        $(".singleApi").each(function() {
            $(this).css("display", "none");
        })
        $(("#"+operVal)).css("display", "block");
        //console.log($(("#"+operVal)).html())
    })
});