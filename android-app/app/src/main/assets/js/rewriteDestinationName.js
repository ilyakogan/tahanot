define(function() {
    var rewriteRules = [
        {
            canRun: function(destinationStop, currentStop) {            
                return (destinationStop.town === currentStop.town);
            },
            run: function(destinationStop) {
                return {
                    name: destinationStop.name,
                    town: undefined
                };
            }
        },

        {
            canRun: function(destinationStop) {
                return (destinationStop.name.indexOf(destinationStop.town) != -1);
            },
            run: function(destinationStop) {
                return {
                    name: destinationStop.name,
                    town: undefined
                };
            }
        },

        {
            canRun: function(destinationStop) {
                return (destinationStop.name.indexOf("תל אביב") != -1 && destinationStop.town && destinationStop.town.indexOf("תל אביב") != -1);
            },
            run: function(destinationStop) {
                return {
                    name: destinationStop.name,
                    town: undefined
                };
            }
        },

        {
            canRun: function(destinationStop) {
                return (destinationStop.name.indexOf("/הורדה") != -1);
            },
            run: function(destinationStop) {
                return {
                    name: destinationStop.name.replace("/הורדה", ""),
                    town: destinationStop.town
                };
            }
        }
    ];

    function rewrite(destinationStop, currentStop) {
        $.each(rewriteRules, function(i, rule) {
            if (rule.canRun(destinationStop, currentStop)) {
                destinationStop = rule.run(destinationStop);
            }
        });
        return destinationStop;
    }

    return function(destinationStop, currentStop) {
        return rewrite(destinationStop, currentStop);
    }
})