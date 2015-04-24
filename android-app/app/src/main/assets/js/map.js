define(["eventServices/mapCenterChanged", "eventServices/stopAdded", "nativeApp/nativeAppCallbacks/onLocationChanged", 
    "text!customControls/focusMapControl.html", "require-css/css!customControls/focusMapControl.css"], 
    function(mapCenterChanged, stopAdded, onLocationChanged, focusMapControlTemplate, focusMapControlCss) {

    var map;
    var mapMover;
    var locationUpdatedOnce = false;
    var locationMarker;

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
                    updateLocation(pos.coords.latitude, pos.coords.longitude);
                }
            })
        }

        onLocationChanged.listen(updateLocation)
    }

    function updateLocation(lat, lng) {
        var location = new google.maps.LatLng(lat, lng);
        if (!locationUpdatedOnce) {
            locationUpdatedOnce = true;
            map.setCenter(location);
            mapCenterChanged.broadcastNow();
        }

        if (!locationMarker) {
            var circle = {
                path: google.maps.SymbolPath.CIRCLE,
                fillColor: 'blue',
                fillOpacity: .6,
                scale: 6,
                strokeColor: 'black',
                strokeWeight: 1
            };

            locationMarker = new google.maps.Marker({
                position: location,
                icon: circle,
                map: map,
            });

            var controlDiv = $(focusMapControlTemplate);
            controlDiv.click(function() {
                map.panTo(locationMarker.position);
            });
            map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(controlDiv[0]);
        }
        else {
            locationMarker.setPosition(location);
        }
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