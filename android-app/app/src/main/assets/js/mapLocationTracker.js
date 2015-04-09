define(function() {
    return function(map) {    	
        var geocoder = new google.maps.Geocoder();
        var locationMarker;

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
                locationMarker.setPosition(me);
            }, function(error) {
                // No location
            });
        }

        function tryCenterOnAddress(address) {
          geocoder.geocode( { 'address': address}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
              map.setCenter(results[0].geometry.location);
            } else {
              // Geocoding error
            }
          });
        }

        function initialize() {
        	locationMarker = createLocationMarker()
			if (navigator.geolocation) trackLocation()
        }

        return {
        	initialize: initialize,
        	tryCenterOnAddress: tryCenterOnAddress
        }
    }
})