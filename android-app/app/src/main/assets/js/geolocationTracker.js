define(["map"], function(map) {
    var locationMarker;

	function createLocationMarker() {
        return new google.maps.Marker({
            clickable: false,
            icon: new google.maps.MarkerImage('//maps.gstatic.com/mapfiles/mobile/mobileimgs2.png', new google.maps.Size(22, 22), new google.maps.Point(0, 18), new google.maps.Point(11, 11)),
            shadow: null,
            zIndex: 999,
            map: map.googleMap
        });
    }

    function trackLocation() {
        navigator.geolocation.getCurrentPosition(function(pos) {
            var me = new google.maps.LatLng(pos.coords.latitude, pos.coords.longitude);
            locationMarker.setPosition(me);
        }, function(error) {
            // No location
        });
    }

    function initialize() {
    	locationMarker = createLocationMarker();
		if (navigator.geolocation) trackLocation();
    }

    initialize();
})