define("stopsRepository", ["utils/distance"], function(distance) {
	var places = [];
    var placeIds = [];
    		
	function add(place) {
		places.push(place);
		placeIds.push(place.place_id);
	}

	function exists(place) {
		return (placeIds.indexOf(place.place_id) != -1)
	}

    function byDistanceFrom(center) {
    	return function(place1, place2) {
	        distance1 = distance(place1.geometry.location, center);
	        distance2 = distance(place2.geometry.location, center);
	        return distance1 - distance2;
	    };
    }

    function getStopsAround(center) {
    	var placesCopy = places.slice()
        placesCopy.sort(byDistanceFrom(center));
        return placesCopy;
    }

	return {
		add: add,
		exists: exists,
		getStopsAround: getStopsAround
	};
});
    