define(["map", "stopsRepository", "bridge", "eventServices/mapCenterChanged", "eventServices/mapStopClicked", "eventServices/newStopsDisplayed", "utils/distance"], 
function(map, stopsRepository, bridge, mapCenterChanged, mapStopClicked, newStopsDisplayed, distance) {

    function searchForStops(centerLocation) {
        var center = map.getCenter();
        setTimeout(function() {            
            if (distance(center, map.getCenter()) > 0.01) {
                // Don't waste resources on showing stops, map is still moving
                return;
            }
            var request = {
                location: center,
                types: ['bus_station'],
                rankBy: google.maps.places.RankBy.DISTANCE
            }
            var service = new google.maps.places.PlacesService(map.googleMap)
            service.nearbySearch(request, onStopsFound);
        }, 500);
    }

    function onStopsFound(results, status, pagination) {
        if (status == google.maps.places.PlacesServiceStatus.OK) {
            var anyNewStops = false
            for (var i = 0; i < results.length; i++) {
                var place = results[i];
                if (!stopsRepository.exists(place)) {
                    place.stopCode = bridge.getStopCode(place);
                    if (place.stopCode != 0) {
                        stopsRepository.add(place);
                        createStopMarker(place);
                        anyNewStops = true;
                    }
                }
            }
        }
        if (anyNewStops) newStopsDisplayed.broadcast();
    }

    var image = {
        url: 'images/bus_marker.png',
        size: new google.maps.Size(33, 61),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(16, 63)
    };

    function createStopMarker(place) {
        var placeLoc = place.geometry.location;
        var stopMarker = new google.maps.Marker({
            map: map.googleMap,
            title: place.name,
            icon: image,
            position: place.geometry.location
        });

        google.maps.event.addListener(stopMarker, 'click', function() {
            mapStopClicked.broadcast(place);
        });
    }

    mapCenterChanged.listen(function() {
        searchForStops();
    });

    searchForStops(map.getCenter());
})