require(["mapLocationTracker", "mapStops", "queryParamHandler", "bridge", "nearbyStops"], 
    function(MapLocationTracker, MapStops, queryParamHandler, bridge, nearbyStops) {

    var map;
    var mapLocationTracker;

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

        mapLocationTracker = MapLocationTracker(map);
        mapLocationTracker.initialize();

        mapStops = MapStops(map, onStopsDisplayed, onStopSelected);
        mapStops.searchForStops();
        registerUiEvents();
        registerMapEvents();
    }

    function onStopsDisplayed() {
        bridge.onStopsDisplayed();
        nearbyStops.refresh(map.getCenter());
    }

    function onStopSelected(place) {
        bridge.onStopSelected(place, "map");
    }

    function onAddressEntered() {
      var address = document.getElementById('address').value;
      mapLocationTracker.tryCenterOnAddress(address);
    }

    function blurControls() {
        // Unfocus the text box to remove keyboard on Android
        var activeElement = document.activeElement;
        if (activeElement) {
           activeElement.blur();
        } else if (document.parentElement) {
           document.parentElement.focus();
        } else {
           window.focus();
        }
    }

    function registerMapEvents() {
        google.maps.event.addListener(map, 'center_changed', function() {
            window.setTimeout(function() { 
                mapStops.searchForStops();
            }, 0)
        });
        google.maps.event.addListener(map, 'dragend', function() {
            nearbyStops.refresh(map.getCenter());
        })
    }

    function registerUiEvents() {
        $("#address").on("keypress", function() {
            if (event.keyCode == 13) {
                onAddressEntered()
                blurControls()
            }
        })

        $("#address-btn").on("click", function () {
            onAddressEntered()
            blurControls() 
        })
    }

    initialize()    
})