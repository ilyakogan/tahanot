define("eventServices/mapCenterChanged", function() {
	var callbacks = $.Callbacks();
	return {
		broadcast: function() { callbacks.fire(); },
	    listen: function(callback) { callbacks.add(callback); }
	};
});