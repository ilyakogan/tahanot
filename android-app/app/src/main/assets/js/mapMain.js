require(["mapStops"]);
require(["geolocationTracker"]);
require(["searchBar"]);
require(["mapPageScroller"]);
require(["angular/nearbyStopsController"]);
require(["nativeApp/nativeAppCallbacks"])

function visualTouches() {
	// Set sections heights so they're prepared for navigation
	$(document).ready(function(){
		$("#map-section").height($(window).height()).width("100%");
		$("#hello-section").height($(window).height()).width("100%");
		var navBarHeight = $("#navbar-main").outerHeight();
		$(".page-section").css("padding-top", navBarHeight);
	});

	// Make scrolling smooth
	$('.pagination a').click(function (event) {
		var scrollPos = jQuery('body').find($(this).attr('href')).offset().top;
		$('body,html').animate({ scrollTop: scrollPos}, 500, function () {});
		return false;
	});

	$('body').scrollspy({ target: '#navbar-main' })
}

visualTouches();
