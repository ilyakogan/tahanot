define("eventServices/addressEntered", function() {
	var callbacks = $.Callbacks();

	return {
		broadcast: function(lat, lng) { callbacks.fire(lat, lng); },
	    listen: function(callback) { callbacks.add(callback); }
	};
});