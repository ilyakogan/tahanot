define(["map", "stopCache", "eventServices/mapCenterChanged", "eventServices/mapStopClicked", "eventServices/stopAdded", "utils/distance"], 
function(map, stopCache, mapCenterChanged, mapStopClicked, stopAdded, distance) {

    function searchForStops() {
        stopCache.addStopsAround(map.getCenter().lat(), map.getCenter().lng());
    }

    var image = {
        url: 'images/bus_marker.png',
        size: new google.maps.Size(33, 61),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(16, 63)
    };

    function createStopMarker(stop) {
        var stopMarker = new google.maps.Marker({
            map: map.googleMap,
            title: stop.name,
            icon: image,
            position: new google.maps.LatLng(stop.location.latitude, stop.location.longitude)
        });

        google.maps.event.addListener(stopMarker, 'click', function() {
            mapStopClicked.broadcast(stop);
        });
    }

    mapCenterChanged.listen(function() {
        searchForStops();
    });

    stopAdded.listen(createStopMarker);

    searchForStops();
})