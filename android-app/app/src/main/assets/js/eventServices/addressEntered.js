define(function() {
	var callbacks = $.Callbacks();

	return {
		broadcast: function(location) { callbacks.fire(location); },
	    listen: function(callback) { callbacks.add(callback); }
	};
});