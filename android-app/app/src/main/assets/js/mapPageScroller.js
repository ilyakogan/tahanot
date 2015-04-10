define(["map", "eventServices/mapStopClicked", "eventServices/addressEntered"], function(map, mapStopClicked, addressEntered) {
	function scrollTo(selector) {
		var scrollPos = $(selector).offset().top;
		$('body,html').animate({ scrollTop: scrollPos}, 500, function () {});
	}

	mapStopClicked.listen(function(place) {
		map.panTo(place.geometry.location);
		setTimeout(function() {
			scrollTo("#nearby-stops-section");
		}, 1000);
	})

	addressEntered.listen(function(location) {
		scrollTo("#map-section");
		map.panTo(location);
	});
});