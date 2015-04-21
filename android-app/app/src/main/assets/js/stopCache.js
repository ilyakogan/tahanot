define(["parseClient"], function(parseClient) {
	var stops = {};
    		
	function exists(stop) {
        return stops[stop.code] !== undefined;
	}

    function byDistanceFrom(centerGeoPoint) {
    	return function(stop1, stop2) {
	        distance1 = stop1.location.kilometersTo(centerGeoPoint);
	        distance2 = stop2.location.kilometersTo(centerGeoPoint);
	        return distance1 - distance2;
	    };
    }

    function getStopsAround(centerLat, centerLng, maxDisatnceKm) {
        var centerGeoPoint = new Parse.GeoPoint(centerLat, centerLng);
        var nearbyStops = []
        for (var stopCode in stops) {
            if (stops[stopCode].location.kilometersTo(centerGeoPoint) < maxDisatnceKm) {
                nearbyStops.push(stops[stopCode])
            }
        }
    	nearbyStops.sort(byDistanceFrom(centerGeoPoint));
        return nearbyStops;
    }

    function addStopsAround(centerLat, centerLng, newStopCallback) {
        parseClient.getNearbyStops(centerLat, centerLng).then(function(nearbyStops) {
            nearbyStops.forEach(function(stop) {
                if (!exists(stop)) {
                    stops[stop.code] = stop;
                    newStopCallback(stop);
                }
            });
        })
    }

    function getOrAdd(stopCode) {
        var d = $.Deferred();
        var stop = stops[stopCode];
        if (stop) {
            d.resolve(stop);
        }
        else {
            parseClient.getStopByCode(stopCode).then(function(stop) {
                stops[stop.code] = stop;
                d.resolve(stop);
            })
        }
        return d.promise();
    }

	return {
		getStopsAround: getStopsAround,
        addStopsAround: addStopsAround,
        getOrAdd: getOrAdd,
        get: function(stopCode) {
            return stops[stopCode];
        }
	};
});
    