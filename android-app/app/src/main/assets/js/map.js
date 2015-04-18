define(["queryParamHandler", "eventServices/mapCenterChanged", "eventServices/newStopsDisplayed"], 
    function(queryParamHandler, mapCenterChanged, newStopsDisplayed) {

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

        registerMapEvents();

        mapCenterChanged.broadcastNow();
    }

    function getCenter() {
        return map.getCenter();
    }

    function registerMapEvents() {
        google.maps.event.addListener(map, 'idle', function() {
            mapCenterChanged.broadcastDelayed(getCenter);
        });
    }

    function addIdleListenerOnce(callback) { 
        google.maps.event.addListenerOnce(map, 'idle', function() {
            callback();
        }); 
    }

    initialize();

    return {
        panTo: function(location) { map.panTo(location); },
        getCenter: getCenter,
        addIdleListenerOnce: addIdleListenerOnce,
        googleMap: map
    };
})