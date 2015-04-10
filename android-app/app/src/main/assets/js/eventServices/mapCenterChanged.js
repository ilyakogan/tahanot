define(["utils/throttle"], function(Throttle) {
	var callbacks = $.Callbacks();

    var throttle = new Throttle(1000);

	return {
		broadcast: function(centerLocation) { 
			throttle(function() { 
				callbacks.fire(centerLocation); 
			})
		},
	    listen: function(callback) { callbacks.add(callback); }
	};
});