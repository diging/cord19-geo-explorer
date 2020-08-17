$(function() {
    $(".date").each(function(index, element) {
        var dateStr = $(element).text();
        var parsedDate = new Date(dateStr);
        $(element).text(parsedDate.toLocaleString());
    });    
});

function openNav() {
  	$("#mySidenav").css("width", "300px");
}

/* Set the width of the side navigation to 0 */
function closeNav() {
	$("#mySidenav").css("width", "0px");
}