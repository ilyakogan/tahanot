require(["angular/tahanotApp", "nearbyStops", "bridge"], function(tahanotApp, nearbyStops, bridge) {

	tahanotApp.app.controller('nearbyStopsController', function($scope) {
	    $scope.stops = [];
	    $scope.select = function(stop) { 
	    	bridge.onStopSelected(stop.place, "nearbyStops");
	    };

	    nearbyStops.subscribeToUpdates(function(places) {
	    	$scope.$apply(function() {
	    		$scope.stops = [];
		    	places.slice(0,5).forEach(function(place) {
		    		$scope.stops.push({
		    			id: place.stopCode,
		    			name: place.name,
		    			place: place
		    		});
		    	});
			});
	    });
	});

	angular.element(document).ready(function() {
	    angular.bootstrap(document, ["tahanot"]);
	    $("#nearby-stops-table").show();
	});
	
});
