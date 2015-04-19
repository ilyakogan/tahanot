define(function() {
    var callbacks = $.Callbacks();
    
    window.setInitialLocation = function(lat, lon) {
        callbacks.fire(lat, lon);
    };

    return {
        listen: function(callback) { callbacks.add(callback); }
    }
})