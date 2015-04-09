define(["stopsRepository", "bridge"], function(stopsRepository, bridge) {
    return function(map, onStopsDisplayed, onStopSelected) {

        function searchForStops() {
            var request = {
                location: map.getCenter(),
                types: ['bus_station'],
                rankBy: google.maps.places.RankBy.DISTANCE
            }
            var service = new google.maps.places.PlacesService(map)
            service.nearbySearch(request, onStopsFound);
        }

        function onStopsFound(results, status, pagination) {
            if (status == google.maps.places.PlacesServiceStatus.OK) {
                var anyNewStops = false
                for (var i = 0; i < results.length; i++) {
                    var place = results[i];
                    if (!stopsRepository.exists(place)) {
                        place.stopCode = bridge.getStopCode(place);
                        stopsRepository.add(place);
                        createStopMarker(place);
                        anyNewStops = true;
                    }
                }

                // Disable pagination to improve performance. Pagination allows 60 results instead of 20.
                // if (anyNewStops && pagination.hasNextPage) {
                //     pagination.nextPage();
                // }
            }
            if (anyNewStops) onStopsDisplayed();
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
                map: map,
                title: place.name,
                icon: image,
                position: place.geometry.location
            });

            google.maps.event.addListener(stopMarker, 'click', function() {
                onStopSelected(place)        
            });
        }

        return {
            searchForStops: searchForStops
        };
    }
})