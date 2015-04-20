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
        	window.AndroidBridge.onStopSelected(place.geometry.location.k, place.geometry.location.D, place.name);
        }
        else {
        	console.log("Bridge: onStopSelected: " + place.name);
        }
    }

    function getStopCode(place) {
        if (window.AndroidBridge) {
            return window.AndroidBridge.getStopCode(place.geometry.location.k, place.geometry.location.D);
        }
        else {
            console.log("Bridge: getStopCode");
            return Math.round(place.geometry.location.D % 1 * 1000000);
        }
    }

    function getStopName(stopCode) {
        if (window.AndroidBridge) {
            return window.AndroidBridge.getStopName(stopCode);
        }
        else {
            console.log("Bridge: getStopName");
            return "ביר אלמכסור 6/קופת חולים כללית,ביר אל מכסור";
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
        getStopCode: getStopCode,
        getStopName: getStopName,
        requestStopMonitoring: requestStopMonitoring
	}
})