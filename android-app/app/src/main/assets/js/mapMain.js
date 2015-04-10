require(["map"]);
require(["geolocationTracker"]);
require(["searchBar"]);
require(["mapPageScroller"]);
require(["angular/nearbyStopsController"]);

function visualTouches() {
	// Set sections heights so they're prepared for navigation
	$(document).ready(function(){
		$(".page-section").height($(window).height()).width("100%");
		var navBarHeight = $("#navbar-main").outerHeight();
		$(".page-section").css("padding-top", navBarHeight);
	});

	// Make the pagination control work as a scrollspy
	$('.pagination > li').click(function (e) {
		$('[data-spy="scroll"]').scrollspy('refresh');
	})

	// Make scrolling smooth
	$('.pagination a').click(function (event) {
		var scrollPos = jQuery('body').find($(this).attr('href')).offset().top;
		$('body,html').animate({ scrollTop: scrollPos}, 500, function () {});
		return false;
	});
}

visualTouches();