define("eventServices/mapStopClicked", function() {
	var callbacks = $.Callbacks();

	return {
		broadcast: function(place) { callbacks.fire(place); },
	    listen: function(callback) { callbacks.add(callback); }
	};
});