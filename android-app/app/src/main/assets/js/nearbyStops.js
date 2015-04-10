define(["stopsRepository", "eventServices/mapCenterChanged"], function(stopsRepository, mapCenterChanged) {
    var center;
    var updatedCallbacks = $.Callbacks();

    function lineDistance(location1, location2)
    {
      var x = location1.k - location2.k;         
      var y = location1.D - location2.D;          
      return Math.sqrt(x*x + y*y);
    }

    function byDistanceFromCenter(place1, place2) {
        distance1 = lineDistance(place1.geometry.location, center);
        distance2 = lineDistance(place2.geometry.location, center);
        return distance1 - distance2;
    }

    function refresh(newCenter) {
        if (typeof newCenter !== 'undefined') {
            center = newCenter;
        }

        var places = stopsRepository.getAll();
        places.sort(byDistanceFromCenter);
        updatedCallbacks.fire(places);
    }

    function subscribeToUpdates(callback) {
        updatedCallbacks.add(callback);
    }    

    mapCenterChanged.listen(function(centerLocation) {
        refresh(centerLocation);
    });

	return {
		refresh: refresh,
        subscribeToUpdates: subscribeToUpdates
	};
});
