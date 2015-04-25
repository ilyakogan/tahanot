define(["map", "eventServices/mapStopClicked", "eventServices/addressEntered", "eventServices/mapCenterChanged"], 
function(map, mapStopClicked, addressEntered, mapCenterChanged) {
	function scrollTo(selector, completeCallback) {
		var scrollPos = $(selector).offset().top;
		$('body,html').animate({ scrollTop: scrollPos}, 500, completeCallback);
	}

	mapStopClicked.listen(function(stop) {
		map.panTo(stop.location.latitude, stop.location.longitude);
		setTimeout(function() {
			scrollTo("#nearby-stops-section");
		}, 700);
	})

	addressEntered.listen(function(lat, lng) {
		scrollTo(
			"#map-section",
			function() {
				map.addIdleListenerOnce(function(){
				    mapCenterChanged.broadcastNow();
				});

				map.panTo(lat, lng);
			});
	});

	return {
		showOnMap: function(lat, lng) {
			scrollTo(
				"#map-section",
				function() {
					map.panTo(lat, lng);
				});
		}
	}
});