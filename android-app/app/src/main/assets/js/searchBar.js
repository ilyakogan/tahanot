define(["map"], function(map) {

	var mapLocationTracker = map.getMapLocationTracker();

   	function initSearchBar() {
        autocomplete = new google.maps.places.Autocomplete((document.getElementById('address')), { 
            types: ['geocode'] 
        });

        google.maps.event.addListener(autocomplete, 'place_changed', function() {
            onAddressEntered()
            blurControls() 
        });

        // Will be needed only if search button is shown again
        // $("#address-btn").on("click", function () {
        //     onAddressEntered()
        //     blurControls() 
        // });
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

    initSearchBar();
});
