define(["mapStops", "queryParamHandler", "bridge", "nearbyStops", "eventServices/mapCenterChanged"], 
    function(MapStops, queryParamHandler, bridge, nearbyStops, mapCenterChanged) {

    var map;
    var mapMover;

    function initialize() {
        var lat = queryParamHandler.getNumeric('lat', 32.08);
        var lng = queryParamHandler.getNumeric('lng', 34.781);
        var initialLocation = new google.maps.LatLng(lat, lng);
        map = new google.maps.Map(document.getElementById('map-canvas'), {
            center: initialLocation,
            streetViewControl: false,
            panControl: false,
            zoomControlOptions: {
                style: google.maps.ZoomControlStyle.SMALL,
                position: google.maps.ControlPosition.RIGHT_BOTTOM
            },
            mapTypeControlOptions: {
                mapTypeIds: [
                    google.maps.MapTypeId.ROADMAP,
                    google.maps.MapTypeId.SATELLITE
                ]
            },
            zoom: 17
        });

        mapStops = MapStops(map, onStopsDisplayed);
        mapCenterChanged.broadcast(map.getCenter());
        
        registerMapEvents();
    }

    function onStopsDisplayed() {
        bridge.onStopsDisplayed();
        nearbyStops.refresh(map.getCenter());
    }

    function registerMapEvents() {
        google.maps.event.addListener(map, 'center_changed', function() {
            // The timeout fixes a bug when handling the event prevents the map from actually moving
            window.setTimeout(function() { 
                mapCenterChanged.broadcast(map.getCenter());
            }, 0)
        });
    }

    initialize();

    return {
        panTo: function(location) { map.panTo(location); },
        googleMap: map
    };
})