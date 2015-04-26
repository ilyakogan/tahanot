define(["eventServices/mapCenterChanged", "eventServices/stopsAdded", "nativeApp/nativeAppCallbacks/onLocationChanged", 
    "text!customControls/focusMapControl.html", "require-css/css!customControls/focusMapControl.css"], 
    function(mapCenterChanged, stopsAdded, onLocationChanged, focusMapControlTemplate, focusMapControlCss) {

    var map;
    var markerClusterGroup;
    var locationUpdatedOnce = false;
    var locationMarker;

    function initialize() {
        var lat = 32.08;
        var lng = 34.781;
        
        map = L.map('map-canvas', {zoomControl: false});

        map.setView([lat, lng], 17);

        L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        new L.Control.Zoom({ position: 'bottomright' }).addTo(map);

        var locateControl = L.control.locate({ 
            position: 'bottomright', 
            remainActive: true, 
            locateOptions: { maxZoom: 17 } 
        }).addTo(map);
        locateControl.start();
        map.on('dragstart', locateControl._stopFollowing, locateControl);

        markerClusterGroup = L.markerClusterGroup({ disableClusteringAtZoom: 16 });
        map.addLayer(markerClusterGroup);

        registerMapEvents();
    }

    function addMarkerToCluster(marker) {
        marker.addTo(markerClusterGroup);
    }

    function addMarker(marker) {
        marker.addTo(map);
    }

    function getCenter() {
        return map.getCenter();
    }

    function registerMapEvents() {
        map.on('moveend', function() {
            mapCenterChanged.broadcastDelayed(getCenter);
        });
    }

    function addIdleListenerOnce(callback) { 
        map.once('moveend', function() {
            callback();
        }); 
    }

    initialize();

    return {
        panTo: function(lat, lng) { map.panTo([lat, lng]); },
        getCenter: getCenter,
        addIdleListenerOnce: addIdleListenerOnce,
        addMarkerToCluster: addMarkerToCluster,
        addMarker: addMarker
    };
})