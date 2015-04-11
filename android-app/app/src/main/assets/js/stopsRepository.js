define(function() {
	var places = [];
    var placeIds = [];
    		
	function add(place) {
		places.push(place);
		placeIds.push(place.place_id);
	}

	function exists(place) {
		return (placeIds.indexOf(place.place_id) != -1)
	}

	function lineDistance(location1, location2)
    {
      var x = location1.k - location2.k;         
      var y = location1.D - location2.D;          
      return Math.sqrt(x*x + y*y);
    }

    function byDistanceFrom(center) {
    	return function(place1, place2) {
	        distance1 = lineDistance(place1.geometry.location, center);
	        distance2 = lineDistance(place2.geometry.location, center);
	        return distance1 - distance2;
	    };
    }

    function getStopsAround(center) {
        places.slice().sort(byDistanceFrom(center));
        return places;
    }

	return {
		add: add,
		exists: exists,
		getStopsAround: getStopsAround
	};
});
    