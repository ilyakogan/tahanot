define(function() {
    function extractStop(obj) {
        return {
            code: obj.get("code"),
            name: obj.get("name"),
            town: obj.get("town"),
            location: obj.get("location")
        };
    }

    return {
        getNearbyStops: function(lat, lng) {
            var point = new Parse.GeoPoint(lat, lng);
            var query = new Parse.Query(Parse.Object.extend("stop"));
            query.near("location", point);
            query.limit(10);
            return query.find().then(function(objs) {
                return $.map(objs, extractStop);
            });
        },

        getStopByCode: function(stopCode) {
            var query = new Parse.Query(Parse.Object.extend("stop"));
            query.equalTo("code", stopCode);
            return query.first().then(extractStop);
        }
    }
})