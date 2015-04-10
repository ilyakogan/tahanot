define(function() { 
	return function (limit) {
		var callbacks = $.Callbacks();
		var throttlingOn = false;
		var pendingCallback = undefined;

	    var callIfNotThrottled = function (callback) {
	        if (!throttlingOn) {                  
	            callback();
	            throttlingOn = true;
	            setTimeout(function () {
	                throttlingOn = false;
	                if (pendingCallback) pendingCallback();
	                pendingCallback = undefined;
	            }, limit);
	        }
	        else {
	        	pendingCallback = callback;
	        }
	    };

	    return callIfNotThrottled;
	};
});