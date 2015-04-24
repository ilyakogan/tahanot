require(["angular/tahanotApp", "map", "stopCache", "stopMonitoringCache", "nativeApp/bridge", "mapPageScroller", "eventServices/mapStopClicked", 
	"eventServices/mapCenterChanged", "eventServices/stopAdded", "nativeApp/nativeAppCallbacks/setIsForWidget", "rewriteDestinationName"], 
function(tahanotApp, map, stopCache, stopMonitoringCache, bridge, mapPageScroller, mapStopClicked, mapCenterChanged, stopAdded, setIsForWidget, rewriteDestinationName) {

	tahanotApp.app.controller('nearbyStopsController', ['$scope', '$interval', function($scope, $interval) {
	    $scope.stops = [];
	    $scope.isForWidget = false;
	    var mapCenter;
	    var selectedStopCode;

	    $scope.selectForWidget = function(stopModel) { 
	    	bridge.onStopSelected(stopModel.stopCode, stopModel.name);
	    };

	    $scope.showOnMap = function(stopModel) {
	    	selectedStopCode = stopModel.code;
	    	mapPageScroller.showOnMap(stopModel.lat, stopModel.lng);
	    }

		$scope.refreshVisits = function(stopModel) { 
			getVisits(stopModel, true);
		}

	    function refresh() {    		
    		$scope.stops = [];
        	var regularStops = stopCache.getStopsAround(map.getCenter().lat(), map.getCenter().lng(), 5);
    		
    		if (selectedStopCode) {
        		var selectedStop = stopCache.get(selectedStopCode)
        		addStopModel(createStopModel(selectedStop, true));
        	}

        	regularStops.slice(0,8).forEach(function(stop) {
	    		if (stop.code != selectedStopCode) {
		    		addStopModel(createStopModel(stop, false));
		    	}
	    	});
	    }

	    function createStopModel(stop, isSelected) {
	    	if (!stop.location) {
	    		console.log(stop.code);
	    	}
			return {
    			stopCode: stop.code,
    			name: stop.name,
    			town: stop.town,
    			lat: stop.location.latitude,
    			lng: stop.location.longitude,
    			visitsAvailable: false,
    			isSelected: isSelected
    		}
	    }

	    function addStopModel(stopModel) {
	    	$scope.stops.push(stopModel);
	    	getVisits(stopModel);
	    	
	    	stopModel.timelyRefresh = $interval(function() {
	    		if ($scope.stops.indexOf(stopModel) === -1) {
		    		$interval.cancel(stopModel.timelyRefresh);
		    	};
	    		getVisits(stopModel);
	    	}, stopMonitoringCache.cacheTimeout + 1000) // Make sure there's a cache miss next time
	    }

		function getVisits(stopModel, force) {
		    stopModel.isReceivingVisits = true;
	    	stopModel.failedReceivingVisits = false;
    		stopMonitoringCache.get(stopModel.stopCode, force, 30000).then(function(visits) {
				callInScope(function() {
	    			if ($scope.stops.indexOf(stopModel) === -1) return;
	    			stopModel.visits = visits;
	    			stopModel.isReceivingVisits = false;
	    			visits.forEach(function(visit) {
	    				stopCache.getOrAdd(visit.destinationRef).then(function(destinationStop) {
	    					callInScope(function() {
	    						var rewrittenDestinationStop = rewriteDestinationName(destinationStop, stopModel);
	    						var parts = [rewrittenDestinationStop.name, rewrittenDestinationStop.town];
		    					visit.destination = $.grep(parts, function(x) { return(x) }).join(', ');
		    				});
	    				});
		    		});
	    		});
			},
			function(e) { // fail
				console.error(e);
				$scope.$apply(function() {
					stop.isReceivingVisits = false;
					stop.failedReceivingVisits = true;
				});
			});
		}

	    function callInScope(f) { if (!$scope.$$phase) { $scope.$apply(f); } else { f(); } }

		mapCenterChanged.listen(function() { $scope.$apply(refresh); });
	    
	    stopAdded.listen(function() { 
	    	$scope.$apply(function() {
	    		if (!$scope.stops || !$scope.stops.length) {
			    	refresh();		    	
			    }
		    }); 
	    });
	    
	    mapStopClicked.listen(function(stop) { selectedStopCode = stop.code; })

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
