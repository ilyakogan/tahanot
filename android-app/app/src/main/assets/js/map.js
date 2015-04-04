var map
var service
var locationMarker
var placeIds = []



function initialize() {
    var lat = getNumericParameter('lat', 32.08)
    var lng = getNumericParameter('lng', 34.781)
    var initialLocation = new google.maps.LatLng(lat, lng)
    map = new google.maps.Map(document.getElementById('map-canvas'), {
        center: initialLocation,
        streetViewControl: false,
        panControl: false,
        zoomControlOptions: {
            style: google.maps.ZoomControlStyle.SMALL
        },
        mapTypeControlOptions: {
            mapTypeIds: [
                google.maps.MapTypeId.ROADMAP,
                google.maps.MapTypeId.SATELLITE
            ]
        },
        zoom: 17
    });
    service = new google.maps.places.PlacesService(map);
    locationMarker = createLocationMarker();
    if (navigator.geolocation) trackLocation();

    searchForStops()
    google.maps.event.addListener(map, 'center_changed', searchForStops);
}

function searchForStops() {
    var request = {
        location: map.getCenter(),
        types: ['bus_station'],
        rankBy: google.maps.places.RankBy.DISTANCE
    }
    var service = new google.maps.places.PlacesService(map);
    service.nearbySearch(request, onStopsFound);
}

function onStopsFound(results, status, pagination) {
    if (status == google.maps.places.PlacesServiceStatus.OK) {
        var anyNewStops = false
        for (var i = 0; i < results.length; i++) {
            if (placeIds.indexOf(results[i].place_id) == -1) {
                placeIds.push(results[i].place_id)
                createStopMarker(results[i])
                anyNewStops = true
            }
        }
        if (anyNewStops && pagination.hasNextPage) {
            pagination.nextPage()
        }
    }
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
        window.AndroidBridge.onStopSelected(place.geometry.location.k, place.geometry.location.D, place.name);
    });
}

function createLocationMarker() {
    return new google.maps.Marker({
        clickable: false,
        icon: new google.maps.MarkerImage('//maps.gstatic.com/mapfiles/mobile/mobileimgs2.png', new google.maps.Size(22, 22), new google.maps.Point(0, 18), new google.maps.Point(11, 11)),
        shadow: null,
        zIndex: 999,
        map: map
    });
}

function trackLocation() {
    navigator.geolocation.getCurrentPosition(function(pos) {
        var me = new google.maps.LatLng(pos.coords.latitude, pos.coords.longitude);
        myloc.setPosition(me);
    }, function(error) {
        // No location
    });
}

function getNumericParameter(name, fallback) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    if (results === null) return fallback
    var stringValue = decodeURIComponent(results[1].replace(/\+/g, " "));
    var numericValue = parseFloat(stringValue)
    return (isNaN(numericValue) ? fallback : numericValue)
}

google.maps.event.addDomListener(window, 'load', initialize);