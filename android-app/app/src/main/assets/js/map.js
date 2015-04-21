define(["eventServices/mapCenterChanged", "eventServices/newStopsDisplayed", "nativeApp/nativeAppCallbacks/setInitialLocation"], 
    function(mapCenterChanged, newStopsDisplayed, setInitialLocation) {

    var map;
    var mapMover;

    function initialize() {
        var lat = 32.08;
        var lng = 34.781;
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

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(pos) { 
                if (pos && pos.coords) {
                    initMapCenter(pos.coords.latitude, pos.coords.longitude);
                }
            })
        }
        
        setInitialLocation.listen(initMapCenter)
    }

    function initMapCenter(lat, lng) {
        map.setCenter(new google.maps.LatLng(lat, lng));
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