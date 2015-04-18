define(function() {
    var callbacks = $.Callbacks();

    function parseDate(msAjaxDate) {
        return new Date(parseInt(msAjaxDate.replace("/Date(", "").replace(")/",""), 10));
    }

    function minutesBetween(olderDate, newerDate) {
        return Math.floor((newerDate - olderDate) / 60000);
    }

    window.onMonitoringInfoArrived = function(stopCodes, monitoringInfo) {
        visitsByStopCode = [];
        stopCodes.forEach(function(stopCode) {
            visitsByStopCode[stopCode] = [];
        })

        monitoringInfo.Stops.forEach(function(monitoringStop) {
            var stopCode = monitoringStop.MotiroringRef;
            var visits = [];

            monitoringStop.StopVisits.forEach(function(visit) {
                var minutesToArrival = minutesBetween(parseDate(monitoringInfo.ResponseTimestamp), parseDate(visit.ExpectedArrivalTime));
                visits.push({
                    lineNumber: visit.PublishedLineName,
                    destination: '', // todo
                    minutesToArrival: minutesToArrival,
                    isAlreadyHere: (minutesToArrival < 1)
                });
            });
            visitsByStopCode[stopCode] = visits;            
        });

        for (var stopCode in visitsByStopCode) {
            var visits = visitsByStopCode[stopCode];
            callbacks.fire(stopCode, visits);
    };
        };

    return {
        listen: function(callback) { callbacks.add(callback); }
    }
});