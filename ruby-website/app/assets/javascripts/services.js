function loadFootnotes()
{
	$(".service").each(function(index, element) {
		var stop_code = $("#stop_code").data("stopcode");
		var service_ids = $(element).data("serviceids");
		$(element).find("#footnotes").load("/stops/" + stop_code + "/services/" + service_ids + "/footnotes");
	});
};
