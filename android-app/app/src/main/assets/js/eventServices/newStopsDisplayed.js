define("eventServices/newStopsDisplayed", function() {
    var callbacks = $.Callbacks();

    return {
        broadcast: function() { callbacks.fire(); },
        listen: function(callback) { callbacks.add(callback); }
    };
});