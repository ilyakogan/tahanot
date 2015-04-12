require(["angular/tahanotApp", "map", "stopsRepository", "bridge", "mapPageScroller", "eventServices/mapStopClicked", "eventServices/mapCenterChanged", "eventServices/newStopsDisplayed", "utils/distance"], 
function(tahanotApp, map, stopsRepository, bridge, mapPageScroller, mapStopClicked, mapCenterChanged, newStopsDisplayed, distance) {

	tahanotApp.app.controller('nearbyStopsController', ['$scope', '$http', '$location', function($scope, $http, $location) {
	    $scope.stops = [];
	    $scope.isForWidget = $location.search().isForWidget;
	    var mapCenter;
	    var selectedStopPlace;

	    $scope.selectForWidget = function(stop) { 
	    	bridge.onStopSelected(stop.place);
	    };

	    $scope.showOnMap = function(stop) {
	    	selectedStopPlace = stop.place;
	    	mapPageScroller.showOnMap(stop.place);
	    }

	    function createStop(place, isSelected) {
			return {
    			stopCode: place.stopCode,
    			name: place.name,
    			place: place,
    			visitsAvailable: false,
    			isSelected: isSelected
    		}
	    }

	    function refresh() {
    		var stopsAroundCenter = stopsRepository.getStopsAround(map.getCenter());
        	$scope.stops = [];
        	if (selectedStopPlace) {
        		$scope.stops.push(createStop(selectedStopPlace, true));
	    		bridge.requestStopMonitoring(selectedStopPlace.stopCode);
        	}
	    	stopsAroundCenter.slice(0,8).forEach(function(place) {
	    		if (place == selectedStopPlace) {
	    			return;
	    		}
	    		$scope.stops.push(createStop(place, false));
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
	    mapStopClicked.listen(function(place) { selectedStopPlace = place; })
	}]);

	angular.element(document).ready(function() {
	    angular.bootstrap(document, ["tahanot"]);
	    $("#nearby-stops-section").show();
	});	
});
