require(["angular/tahanotApp", "map", "stopsRepository", "stopMonitoringCache", "nativeApp/bridge", "mapPageScroller", "eventServices/mapStopClicked", 
	"eventServices/mapCenterChanged", "eventServices/newStopsDisplayed", "nativeApp/nativeAppCallbacks/setIsForWidget"], 
function(tahanotApp, map, stopsRepository, stopMonitoringCache, bridge, mapPageScroller, mapStopClicked, mapCenterChanged, newStopsDisplayed, setIsForWidget) {

	tahanotApp.app.controller('nearbyStopsController', ['$scope', '$http', function($scope, $http) {
	    $scope.stops = [];
	    $scope.isForWidget = false;
	    var mapCenter;
	    var selectedStopPlace;

	    $scope.selectForWidget = function(stop) { 
	    	bridge.onStopSelected(stop.place);
	    };

	    $scope.showOnMap = function(stop) {
	    	selectedStopPlace = stop.place;
	    	mapPageScroller.showOnMap(stop.place);
	    }

		$scope.refreshVisits = function(stop) { 
			getVisits(stop, true);
		}

	    function refresh() {
    		var stopsAroundCenter = stopsRepository.getStopsAround(map.getCenter());
        	$scope.stops = [];
        	if (selectedStopPlace) {
        		addStop(createStop(selectedStopPlace, true));
        	}
	    	stopsAroundCenter.slice(0,8).forEach(function(place) {
	    		if (place != selectedStopPlace) {
		    		addStop(createStop(place, false));
		    	}
	    	});
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

	    function addStop(stop) {
	    	$scope.stops.push(stop);
	    	getVisits(stop);
	    }

		function getVisits(stop, force) {
			stop.visits = [];
	    	stop.isReceivingVisits = true;
	    	stop.failedReceivingVisits = false;
    		var promise = stopMonitoringCache.get(stop.stopCode, force, 15000);
    		promise.done(function(visits) {
				callInScope(function() {
	    			if ($scope.stops.indexOf(stop) === -1) return;
	    			stop.visits = visits;
	    			stop.isReceivingVisits = false;
	    		});
			});
			promise.fail(function() {
				$scope.$apply(function() {
					stop.isReceivingVisits = false;
					stop.failedReceivingVisits = true;
				});
			});
		}

	    function callInScope(f) { if (!$scope.$$phase) { $scope.$apply(f); } else { f(); } }

		mapCenterChanged.listen(function() { $scope.$apply(refresh); });
	    newStopsDisplayed.listen(function() { $scope.$apply(refresh); });
	    mapStopClicked.listen(function(place) { selectedStopPlace = place; })

	    setIsForWidget.listen(function(isForWidget) { 
	    	$scope.$apply(function() { 
	    		$scope.isForWidget = isForWidget; 
	    	}); 
	    })
	}]);

	angular.element(document).ready(function() {
	    angular.bootstrap(document, ["tahanot"]);
	    $("#nearby-stops-section").show();
	});	
});
