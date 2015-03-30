function addMainMarker(map, position) {
  var image = {
        url: '/assets/you-are-here.png',
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(25, 25),
        scaledSize: new google.maps.Size(50, 50)
    };
  var stopMarker = new google.maps.Marker({
      position: position,
      icon: image,
      title: 'התחנה נמצאת כאן',
      map: map
    });
  return stopMarker;
}

function addOtherMarker(map, position, title) {
  var stopMarker = new google.maps.Marker({
      position: position,
      icon: '/assets/bus-stop-marker-small.png',
      title: title,
      map: map
    });
  return stopMarker;
}

function initGoogleMap(selector) {
    var position = new google.maps.LatLng($(selector).data('lat'), $(selector).data('lon'));
    var mapOptions = {
      center: position,
      zoom: 16
    };
    var map = new google.maps.Map($(selector).get(0), mapOptions);
    addMainMarker(map, position);

    function loadNearbyStops() {
      return $.ajax({
          url: '/stops?around_stop_code=' + $(selector).data('stopcode'),
          dataType: "json",
          accepts: {
              text: "application/json"
          }
      });
    }

    loadNearbyStops()
      .done(function(stops) {
        var infowindow = new google.maps.InfoWindow();
        stops.forEach(function (stop) {
          var stopPosition = new google.maps.LatLng(parseFloat(stop.stop_lat), parseFloat(stop.stop_lon));
          var marker = addOtherMarker(map, stopPosition, stop.stop_name);
          
          google.maps.event.addListener(marker, 'click', function() {
            infowindow.close();
            infowindow.setContent(
              "<div class='stop_popup'>עבור לתחנה:</div><div><a href='#' onclick='window.location = \"/stops/" + 
                stop.stop_code + "\"'>" + stop.stop_name + "</a></div>");
            infowindow.open(map, marker);
          });
        })
      });
  }

$(window).load(function() {
  initGoogleMap('#google-map');
});