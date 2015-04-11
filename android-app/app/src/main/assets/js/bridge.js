define(["eventServices/newStopsDisplayed"], function(newStopsDisplayed) {
	var wasStopDisplayedSent = false

	function onStopsDisplayed() {
		if (wasStopDisplayedSent) return;
		wasStopDisplayedSent = true;
		
		if (window.AndroidBridge) {
        	window.AndroidBridge.onFirstStopDisplayed();
        }
        else {
        	console.log("Bridge: onFirstStopDisplayed");
        }
    }

    function onStopSelected(place, source) {
    	if (window.AndroidBridge) {
        	window.AndroidBridge.onStopSelected(place.geometry.location.k, place.geometry.location.D, place.name, source);
        }
        else {
        	console.log("Bridge: onStopSelected: " + place.name + ", from source: " + source);
        }
    }

    function getStopCode(place) {
        if (window.AndroidBridge) {
            return window.AndroidBridge.getStopCode(place.geometry.location.k, place.geometry.location.D);
        }
        else {
            console.log("Bridge: getStopCode");
            return 21470;
        }
    }

    function requestStopMonitoring(stopCode) {
        if (window.AndroidBridge) {
            window.AndroidBridge.requestStopMonitoring(stopCode);
        }
        else {
            console.log("Bridge: requestStopMonitoring");
        }
    }

    newStopsDisplayed.listen(function() {
        return onStopsDisplayed();
    })

	return {
		onStopSelected: onStopSelected,
        getStopCode: getStopCode,
        requestStopMonitoring: requestStopMonitoring
	}
})