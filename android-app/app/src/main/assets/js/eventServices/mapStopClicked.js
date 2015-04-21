define("eventServices/mapStopClicked", function() {
	var callbacks = $.Callbacks();

	return {
		broadcast: function(stop) { callbacks.fire(stop); },
	    listen: function(callback) { callbacks.add(callback); }
	};
});