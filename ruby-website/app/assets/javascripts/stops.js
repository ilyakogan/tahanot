
$(window).load(function() {
	var stop_code = $('#services').data("stopcode");
	$("#services").load("/stops/" + stop_code + "/services", null,
		function() {
			loadFootnotes();
			$("#times").load("/stops/" + stop_code + "/times");
		});
 });