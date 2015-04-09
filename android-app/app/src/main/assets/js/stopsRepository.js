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

	function getAll() {
		return places.slice();
	}

	return {
		add: add,
		exists: exists,
		getAll: getAll
	};
});
    