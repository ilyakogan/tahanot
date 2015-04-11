define(function() {
	var app = angular.module("tahanot", []); 

    // For parsing query params with $location
    app.config(['$locationProvider', function($locationProvider) {
        $locationProvider.html5Mode({
          enabled: true,
          requireBase: false
        });
    }]);

	return {
		app: app
	};
});