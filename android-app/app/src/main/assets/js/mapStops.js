define(["map", "stopCache", "eventServices/mapCenterChanged", "eventServices/mapStopClicked", "eventServices/stopsAdded", "utils/distance"], 
function(map, stopCache, mapCenterChanged, mapStopClicked, stopsAdded, distance) {

    function searchForStops() {
        stopCache.addStopsAround(map.getCenter().lat, map.getCenter().lng);
    }

    var icon = L.icon({
        iconUrl: 'images/bus_marker.png',
        iconSize: [33, 61],
        iconAnchor: [16, 63],
        shadowUrl: 'images/bus_marker_shadow.png',
        shadowSize: [64, 61],
        shadowAnchor: [16, 63],
        className: 'bus-stop-marker'
    });

    function createStopMarkers(stops) {
        stops.forEach(function(stop) {
            var marker = L.marker([stop.location.latitude, stop.location.longitude], {icon: icon});
            map.addMarkerToCluster(marker);

            marker.on('click', function(e) {
                mapStopClicked.broadcast(stop);
            });
        });
    }

    mapCenterChanged.listen(function() {
        searchForStops();
    });

    stopsAdded.listen(createStopMarkers);

    searchForStops();
})