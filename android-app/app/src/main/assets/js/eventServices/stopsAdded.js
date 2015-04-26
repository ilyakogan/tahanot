define(function() {
    var callbacks = $.Callbacks();

    return {
        broadcast: function(stops) { callbacks.fire(stops); },
        listen: function(callback) { callbacks.add(callback); }
    };
});