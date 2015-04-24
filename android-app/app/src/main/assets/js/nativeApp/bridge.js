define(["eventServices/stopAdded"], function(stopAdded) {
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

    function onStopSelected(stopCode, stopName) {
    	if (window.AndroidBridge) {
        	window.AndroidBridge.onStopSelected(stopCode, stopName);
        }
        else {
        	console.log("Bridge: onStopSelected: " + stopCode + ", " + stopName);
        }
    }

    function requestStopMonitoring(stopCode) {
        if (window.AndroidBridge) {
            window.AndroidBridge.requestStopMonitoring(stopCode);
        }
        else {
            console.log("Bridge: requestStopMonitoring");
            setTimeout(function() {
                var visit = function(line) { return {"ExpectedArrivalTime":"\/Date(1427919976000)\/","PublishedLineName":""+(line*100),"DestinationRef":"21165"} }
                onMonitoringInfoArrived([stopCode], {"Stops": [{"MotiroringRef":stopCode,"StopVisits": Array.apply(null, new Array(2)).map(function(_, x){return visit(x)}) }],"ResponseTimestamp":"\/Date(1427919007162)\/"});
            }, 8000);
        }
    }

    stopAdded.listen(function() {
        return onStopsDisplayed();
    })

	return {
		onStopSelected: onStopSelected,
        // getStopCode: getStopCode,
        // getStopName: getStopName,
        requestStopMonitoring: requestStopMonitoring
	}
})