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

    function onStopSelected(stopCode, stopName) {
    	if (window.AndroidBridge) {
        	window.AndroidBridge.onStopSelected(stopCode, stopName);
        }
        else {
        	console.log("Bridge: onStopSelected: " + name);
        }
    }

    function requestStopMonitoring(stopCode) {
        if (window.AndroidBridge) {
            window.AndroidBridge.requestStopMonitoring(stopCode);
        }
        else {
            console.log("Bridge: requestStopMonitoring");
            setTimeout(function() {
                if (stopCode % 2 == 0) {
                    var visit = function(line) { return {"ExpectedArrivalTime":"\/Date(1427919976000)\/","PublishedLineName":""+(line*100),"DestinationRef":"21165"} }
                    onMonitoringInfoArrived([stopCode], {"Stops": [{"MotiroringRef":stopCode,"StopVisits": Array.apply(null, new Array(10)).map(function(_, x){return visit(x)}) }],"ResponseTimestamp":"\/Date(1427919007162)\/"});
                }
                else {
                    onMonitoringInfoArrived([stopCode], {"Stops":[{"MotiroringRef":11111111111,"StopVisits":[{"ExpectedArrivalTime":"\/Date(1427919876000)\/","PublishedLineName":"66","DestinationRef":"21165"}]}],"ResponseTimestamp":"\/Date(1427919007162)\/"});
                }
            }, 2000);
        }
    }

    newStopsDisplayed.listen(function() {
        return onStopsDisplayed();
    })

	return {
		onStopSelected: onStopSelected,
        // getStopCode: getStopCode,
        // getStopName: getStopName,
        requestStopMonitoring: requestStopMonitoring
	}
})