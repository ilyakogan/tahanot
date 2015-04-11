require(["angular/tahanotApp", "map", "stopsRepository", "bridge", "mapPageScroller", "eventServices/mapCenterChanged", "eventServices/newStopsDisplayed", "utils/distance"], 
function(tahanotApp, map, stopsRepository, bridge, mapPageScroller, mapCenterChanged, newStopsDisplayed, distance) {

	tahanotApp.app.controller('nearbyStopsController', function($scope, $http) {
	    $scope.stops = [];
	    var mapCenter;

	    $scope.selectForWidget = function(stop) { 
	    	bridge.onStopSelected(stop.place);
	    };

	    $scope.showOnMap = function(stop) {
	    	mapPageScroller.showOnMap(stop.place);
	    }

	    function isStopSelected(stop) {
	    	return stop && mapCenter && distance(stop.place.geometry.location, mapCenter) < 0.0000001;
	    }

	    function refresh() {
    		mapCenter = map.getCenter();
	        var places = stopsRepository.getStopsAround(mapCenter);
        	$scope.stops = [];
	    	places.slice(0,10).forEach(function(place) {
	    		$scope.stops.push({
	    			stopCode: place.stopCode,
	    			name: place.name,
	    			place: place,
	    			visitsAvailable: false,
	    			isSelected: function() { return isStopSelected(this); }
	    		});				
	    		bridge.requestStopMonitoring(place.stopCode);
	    	});
	    }

		$scope.canRefreshVisits = function() {
			var visitsAvailable = false;
			$scope.stops.forEach(function(stop) {
				if (stop.visitsAvailable) {
					visitsAvailable = true;
				}
			});
			return visitsAvailable;
		}

		$scope.refreshVisits = refresh;

	    
	    function parseDate(msAjaxDate) {
	    	return new Date(parseInt(msAjaxDate.replace("/Date(", "").replace(")/",""), 10));
	    }

	    function minutesBetween(olderDate, newerDate) {
	    	return Math.floor((newerDate - olderDate) / 60000);
	    }

		// Response from Android
		window.onMonitoringInfoArrived = function(monitoringInfo) {
			$scope.$apply(function() {
				$scope.stops.forEach(function(stopModel) {
        			stopModel.visitsAvailable = true;
        		});
	        	monitoringInfo.Stops.forEach(function(monitoringStop) {
	        		$scope.stops.forEach(function(stopModel) {
	        			if (stopModel.stopCode !== monitoringStop.MotiroringRef) return; // note typo
	        			stopModel.visits = [];
	        			monitoringStop.StopVisits.forEach(function(visit) {
	        				var minutesToArrival = minutesBetween(parseDate(monitoringInfo.ResponseTimestamp), parseDate(visit.ExpectedArrivalTime));
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

		mapCenterChanged.listen(function() { $scope.$apply(refresh); });
	    newStopsDisplayed.listen(function() { $scope.$apply(refresh); });
	});

	angular.element(document).ready(function() {
	    angular.bootstrap(document, ["tahanot"]);
	    $("#nearby-stops-section").show();
	});	
});
