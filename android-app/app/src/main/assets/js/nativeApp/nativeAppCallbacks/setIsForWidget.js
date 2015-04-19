define(function() {
    var callbacks = $.Callbacks();

    window.setIsForWidget = function(isForWidget) {
        callbacks.fire(isForWidget);
    };

    return {
        listen: function(callback) { callbacks.add(callback); }
    }
})