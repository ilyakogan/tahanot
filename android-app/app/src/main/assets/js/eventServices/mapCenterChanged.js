define(["map", "utils/distance"], function(map, distance) {
	var callbacks = $.Callbacks();

    var centerBefore;
    var activeTimerId = null;

    return {
		broadcastDelayed: function(getCenter) {
            if (activeTimerId) {
                clearTimeout(activeTimerId);
            }
            centerBefore = getCenter();
            activeTimerId = setTimeout(function() {
                activeTimerId = null;
                var dist = distance(centerBefore, getCenter());
                if (dist > 0.001) {
                    // Map is still moving
                    return;
                }
                callbacks.fire();
            }, 2000);
        },

        broadcastNow: function() {
            callbacks.fire(); 
        },

	    listen: function(callback) { callbacks.add(callback); }
	};
});