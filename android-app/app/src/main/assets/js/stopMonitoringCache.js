define(["nativeApp/bridge", "nativeApp/nativeAppCallbacks/onMonitoringInfoArrived"], function(bridge, onMonitoringInfoArrived) {

    const cacheTimeout = 30000;

    function Stop(stopCode) {
        var thisStop = this;
        this.stopCode = stopCode;
        this.ageOfData = undefined;
        this.lastRequestSent = undefined;
        this.visits = [];
        this.deferreds = [];

        this.updateVisits = function(visits) {
            thisStop.visits = visits;
            thisStop.ageOfData = new Date();
            thisStop.deferreds.forEach(function(deferred)  {
                deferred.resolve(visits);
            });
            thisStop.deferreds = [];
        }

        this.requestStopMonitoring = function(deferred) {
            this.ageOfData = undefined;
            bridge.requestStopMonitoring(stopCode);
            thisStop.lastRequestSent = new Date();
            thisStop.deferreds = [deferred];
        }
    }

    function get(stopCode, force, giveUpAfter) {
        var deferred = $.Deferred();

        var stop = getOrAddStop(stopCode);

        if (force) {
            stop.requestStopMonitoring(deferred);
            setTimeout(function() { deferred.reject(); }, giveUpAfter);
        }
        else if (isRecent(stop.ageOfData)) {
            deferred.resolve(stop.visits);
        }
        else if (isRecent(stop.lastRequestSent)) {
            stop.deferreds.push(deferred);
            setTimeout(function() { deferred.reject(); }, giveUpAfter);
        }
        else {
            stop.requestStopMonitoring(deferred);
            setTimeout(function() { deferred.reject(); }, giveUpAfter);
        }

        return deferred.promise();
    }

    var stops = [];

    function getOrAddStop(stopCode)
    {
        var stop = getStop(stopCode);
        if (!stop) {
            stop = new Stop(stopCode);
            stops.push(stop);
        }
        return stop;
    }

    function getStop(stopCode) {
        for (var i = 0; i < stops.length; i++) {
            if (stops[i].stopCode === stopCode) {
                return stops[i];
            }
        };        
    }

    function isRecent(time) {
        return time && (time > new Date() - cacheTimeout);
    }

    function prepareToGiveUp(deferred) {
        setTimeout(function() {
            deferred.reject();
        }, giveUpAfter);
    }

    onMonitoringInfoArrived.listen(function(stopCode, visits) {
        var stop = getStop(stopCode);
        if (!stop) return;
        stop.updateVisits(visits);
    })

    return {
        get: get
    }
})