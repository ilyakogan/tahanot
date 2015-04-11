require(["angular/tahanotApp", "map", "stopsRepository", "bridge", "mapPageScroller", "eventServices/mapCenterChanged", "eventServices/newStopsDisplayed"], 
function(tahanotApp, map, stopsRepository, bridge, mapPageScroller, mapCenterChanged, newStopsDisplayed) {

	tahanotApp.app.controller('nearbyStopsController', function($scope, $http) {
	    $scope.stops = [];
	    
	    $scope.selectForWidget = function(stop) { 
	    	bridge.onStopSelected(stop.place, "nearbyStops");
	    };

	    $scope.showOnMap = function(stop) {
	    	mapPageScroller.showOnMap(stop.place);
	    }

	    function refresh(newCenter) {
	    	$scope.$apply(function() {
		    	var center = map.getCenter();
		        var places = stopsRepository.getStopsAround(center);
	        	$scope.stops = [];
		    	places.slice(0,10).forEach(function(place) {
		    		$scope.stops.push({
		    			stopCode: place.stopCode,
		    			name: place.name,
		    			place: place
		    		});				
		    		bridge.requestStopMonitoring(place.stopCode);
		    	});		    	
			});
	    }

	    function parseDate(msAjaxDate) {
	    	return new Date(parseInt(msAjaxDate.replace("/Date(", "").replace(")/",""), 10));
	    }

	    function minutesBetween(olderDate, newerDate) {
	    	return Math.floor((newerDate - olderDate) / 60000);
	    }

		// Response from Android
		window.onMonitoringInfoArrived = function(info) {
			$scope.$apply(function() {
	        	var stops = info.Stops;
	        	stops.forEach(function(monitoringStop) {
	        		$scope.stops.forEach(function(stopModel) {
	        			if (stopModel.stopCode !== monitoringStop.MotiroringRef) return; // note typo
	        			stopModel.visits = [];
	        			monitoringStop.StopVisits.forEach(function(visit) {
	        				var minutesToArrival = minutesBetween(parseDate(info.ResponseTimestamp), parseDate(visit.ExpectedArrivalTime));
	        				stopModel.visits.push({
	        					lineNumber: visit.PublishedLineName,
	        					destination: '', // todo
	        					minutesToArrival: minutesToArrival,
	        					isAlreadyHere: (minutesToArrival < 1)
	        				});
	        			});
	        		});
	        	});
			});
	    };

		mapCenterChanged.listen(refresh);
	    newStopsDisplayed.listen(refresh);
	});

	angular.element(document).ready(function() {
	    angular.bootstrap(document, ["tahanot"]);
	    $("#nearby-stops-section").show();
	});	
});
