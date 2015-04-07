define(function() {
	var getString = function(paramName) {
		paramName = paramName.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]")
	    var regex = new RegExp("[\\?&]" + paramName + "=([^&#]*)")
	    var results = regex.exec(location.search);
	    if (results === null) return null
	    var stringValue = decodeURIComponent(results[1].replace(/\+/g, " "));
	}

	var getNumeric = function(paramName, defaultValue) {
	    var stringValue = getString(paramName)
		var numericValue = parseFloat(stringValue)
	    return (isNaN(numericValue) ? defaultValue : numericValue)
	}

	return {
		getString: getString,
		getNumeric: getNumeric
	}
})