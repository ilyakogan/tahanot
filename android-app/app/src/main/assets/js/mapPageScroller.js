define("mapPageScroller", ["map", "eventServices/mapStopClicked", "eventServices/addressEntered"], 
function(map, mapStopClicked, addressEntered) {
	function scrollTo(selector, completeCallback) {
		var scrollPos = $(selector).offset().top;
		$('body,html').animate({ scrollTop: scrollPos}, 500, completeCallback);
	}

	mapStopClicked.listen(function(place) {
		map.panTo(place.geometry.location);
		setTimeout(function() {
			scrollTo("#nearby-stops-section");
		}, 700);
	})

	addressEntered.listen(function(location) {
		scrollTo(
			"#map-section",
			function() {
				map.panTo(location);
			});
	});

	return {
		showOnMap: function(place) {
			scrollTo(
				"#map-section",
				function() {
					map.panTo(place.geometry.location);
				});
		}
	}
});