define(["map", "eventServices/addressEntered", "stopCache"], function(map, addressEntered, stopCache) {
   	var geocoder = new google.maps.Geocoder();
    
    function initSearchBar() {
        autocomplete = new google.maps.places.Autocomplete((document.getElementById('address')), { 
            types: ['geocode'] 
        });

        google.maps.event.addListener(autocomplete, 'place_changed', function() {
            onAddressEntered()
            blurControls() 
        });

        $("#address").click(function() { $("#address").select(); } );
    }    

    function getLocation(address) {
        var promise = $.Deferred();
        geocoder.geocode( { 'address': address}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                promise.resolve(results[0].geometry.location);
            } else {
                promise.reject();
            }
        });
        return promise;
    }    

    function onAddressEntered() {
        var address = document.getElementById('address').value;
        if (isNaN(address)) {
            getLocation(address).done(function(location) {
                addressEntered.broadcast(location.lat(), location.lng());
            });
        } 
        else {
            stopCache.getOrAdd(parseInt(address)).then(function(stop) {
                addressEntered.broadcast(stop.location.latitude, stop.location.longitude);
            });
        }
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

    initSearchBar();
});
