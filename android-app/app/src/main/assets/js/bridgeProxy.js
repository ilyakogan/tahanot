define(function() {
	var wasStopDisplayedSent = false

	function onStopsDisplayed() {
		if (wasStopDisplayedSent) return
		wasStopDisplayedSent = true
		
		if (window.AndroidBridge) {
        	window.AndroidBridge.onFirstStopDisplayed()        	
        }
        else {
        	console.log("No bridge. onFirstStopDisplayed called.")
        }
    }

    function onStopSelected(place) {
    	if (window.AndroidBridge) {
        	window.AndroidBridge.onStopSelected(place.geometry.location.k, place.geometry.location.D, place.name);
        }
        else {
        	console.log("No bridge. onStopSelected called.")
        }
    }

	return {
		onStopsDisplayed: onStopsDisplayed,
		onStopSelected: onStopSelected
	}
})