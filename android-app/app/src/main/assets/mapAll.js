function visualTouches(){$(document).ready(function(){$("#map-section").height($(window).height()).width("100%"),$("#hello-section").height($(window).height()).width("100%");var e=$("#navbar-main").outerHeight();$(".page-section").css("padding-top",e)}),$(".pagination a").click(function(e){var t=jQuery("body").find($(this).attr("href")).offset().top;return $("body,html").animate({scrollTop:t},500,function(){}),!1}),$("body").scrollspy({target:"#navbar-main"})}define("queryParamHandler",[],function(){var e=function(e){e=e.replace(/[\[]/,"\\[").replace(/[\]]/,"\\]");var t=new RegExp("[\\?&]"+e+"=([^&#]*)"),n=t.exec(location.search);return n===null?null:decodeURIComponent(n[1].replace(/\+/g," "))},t=function(t,n){var r=e(t),i=parseFloat(r);return isNaN(i)?n:i};return{getString:e,getNumeric:t}}),define("eventServices/mapCenterChanged",[],function(){var e=$.Callbacks();return{broadcast:function(){e.fire()},listen:function(t){e.add(t)}}}),define("eventServices/newStopsDisplayed",[],function(){var e=$.Callbacks();return{broadcast:function(){e.fire()},listen:function(t){e.add(t)}}}),define("map",["queryParamHandler","eventServices/mapCenterChanged","eventServices/newStopsDisplayed"],function(e,t,n){function s(){var n=e.getNumeric("lat",32.08),i=e.getNumeric("lng",34.781),s=new google.maps.LatLng(n,i);r=new google.maps.Map(document.getElementById("map-canvas"),{center:s,streetViewControl:!1,panControl:!1,zoomControlOptions:{style:google.maps.ZoomControlStyle.SMALL,position:google.maps.ControlPosition.RIGHT_BOTTOM},mapTypeControlOptions:{mapTypeIds:[google.maps.MapTypeId.ROADMAP,google.maps.MapTypeId.SATELLITE]},zoom:17}),o(),t.broadcast()}function o(){google.maps.event.addListener(r,"idle",function(){t.broadcast()})}var r,i;return s(),{panTo:function(e){r.panTo(e)},getCenter:function(){return r.getCenter()},googleMap:r}}),define("utils/distance",[],function(){return function(e,t){var n=e.k-t.k,r=e.D-t.D;return Math.sqrt(n*n+r*r)}}),define("stopsRepository",["utils/distance"],function(e){function r(e){t.push(e),n.push(e.place_id)}function i(e){return n.indexOf(e.place_id)!=-1}function s(t){return function(n,r){return distance1=e(n.geometry.location,t),distance2=e(r.geometry.location,t),distance1-distance2}}function o(e){var n=t.slice();return n.sort(s(e)),n}var t=[],n=[];return{add:r,exists:i,getStopsAround:o}}),define("bridge",["eventServices/newStopsDisplayed"],function(e){function n(){if(t)return;t=!0,window.AndroidBridge?window.AndroidBridge.onFirstStopDisplayed():console.log("Bridge: onFirstStopDisplayed")}function r(e,t){window.AndroidBridge?window.AndroidBridge.onStopSelected(e.geometry.location.k,e.geometry.location.D,e.name):console.log("Bridge: onStopSelected: "+e.name)}function i(e){return window.AndroidBridge?window.AndroidBridge.getStopCode(e.geometry.location.k,e.geometry.location.D):(console.log("Bridge: getStopCode"),(e.geometry.location.D%1*1e6).toFixed())}function s(e){window.AndroidBridge?window.AndroidBridge.requestStopMonitoring(e):console.log("Bridge: requestStopMonitoring")}var t=!1;return e.listen(function(){return n()}),{onStopSelected:r,getStopCode:i,requestStopMonitoring:s}}),define("eventServices/mapStopClicked",[],function(){var e=$.Callbacks();return{broadcast:function(t){e.fire(t)},listen:function(t){e.add(t)}}}),define("mapStops",["map","stopsRepository","bridge","eventServices/mapCenterChanged","eventServices/mapStopClicked","eventServices/newStopsDisplayed","utils/distance"],function(e,t,n,r,i,s,o){function u(t){var n=e.getCenter();setTimeout(function(){if(o(n,e.getCenter())>.01)return;var t={location:n,types:["bus_station"],rankBy:google.maps.places.RankBy.DISTANCE},r=new google.maps.places.PlacesService(e.googleMap);r.nearbySearch(t,a)},500)}function a(e,r,i){if(r==google.maps.places.PlacesServiceStatus.OK){var o=!1;for(var u=0;u<e.length;u++){var a=e[u];t.exists(a)||(a.stopCode=n.getStopCode(a),a.stopCode!=0&&(t.add(a),l(a),o=!0))}}o&&s.broadcast()}function l(t){var n=t.geometry.location,r=new google.maps.Marker({map:e.googleMap,title:t.name,icon:f,position:t.geometry.location});google.maps.event.addListener(r,"click",function(){i.broadcast(t)})}var f={url:"images/bus_marker.png",size:new google.maps.Size(33,61),origin:new google.maps.Point(0,0),anchor:new google.maps.Point(16,63)};r.listen(function(){u()}),u(e.getCenter())}),define("geolocationTracker",["map"],function(e){function n(){return new google.maps.Marker({clickable:!1,icon:new google.maps.MarkerImage("//maps.gstatic.com/mapfiles/mobile/mobileimgs2.png",new google.maps.Size(22,22),new google.maps.Point(0,18),new google.maps.Point(11,11)),shadow:null,zIndex:999,map:e.googleMap})}function r(){navigator.geolocation.getCurrentPosition(function(e){var n=new google.maps.LatLng(e.coords.latitude,e.coords.longitude);t.setPosition(n)},function(e){})}function i(){t=n(),navigator.geolocation&&r()}var t;i()}),define("eventServices/addressEntered",[],function(){var e=$.Callbacks();return{broadcast:function(t){e.fire(t)},listen:function(t){e.add(t)}}}),define("searchBar",["map","eventServices/addressEntered"],function(e,t){function r(){autocomplete=new google.maps.places.Autocomplete(document.getElementById("address"),{types:["geocode"]}),google.maps.event.addListener(autocomplete,"place_changed",function(){s(),o()}),$("#address").click(function(){$("#address").select()})}function i(e){var t=$.Deferred();return n.geocode({address:e},function(e,n){n==google.maps.GeocoderStatus.OK?t.resolve(e[0].geometry.location):t.reject()}),t}function s(){var e=document.getElementById("address").value;i(e).done(t.broadcast)}function o(){var e=document.activeElement;e?e.blur():document.parentElement?document.parentElement.focus():window.focus()}var n=new google.maps.Geocoder;r()}),define("mapPageScroller",["map","eventServices/mapStopClicked","eventServices/addressEntered"],function(e,t,n){function r(e,t){var n=$(e).offset().top;$("body,html").animate({scrollTop:n},500,t)}return t.listen(function(t){e.panTo(t.geometry.location),setTimeout(function(){r("#nearby-stops-section")},700)}),n.listen(function(t){r("#map-section",function(){e.panTo(t)})}),{showOnMap:function(t){r("#map-section",function(){e.panTo(t.geometry.location)})}}}),define("angular/tahanotApp",[],function(){var e=angular.module("tahanot",[]);return e.config(["$locationProvider",function(e){e.html5Mode({enabled:!0,requireBase:!1})}]),{app:e}}),require(["angular/tahanotApp","map","stopsRepository","bridge","mapPageScroller","eventServices/mapStopClicked","eventServices/mapCenterChanged","eventServices/newStopsDisplayed","utils/distance"],function(e,t,n,r,i,s,o,u,a){e.app.controller("nearbyStopsController",["$scope","$http","$location",function(e,a,f){function h(e,t){return{stopCode:e.stopCode,name:e.name,place:e,visitsAvailable:!1,isSelected:t}}function p(){var i=n.getStopsAround(t.getCenter());e.stops=[],c&&(e.stops.push(h(c,!0)),r.requestStopMonitoring(c.stopCode)),i.slice(0,8).forEach(function(t){if(t==c)return;e.stops.push(h(t,!1)),r.requestStopMonitoring(t.stopCode)})}function d(e){return new Date(parseInt(e.replace("/Date(","").replace(")/",""),10))}function v(e,t){return Math.floor((t-e)/6e4)}e.stops=[],e.isForWidget=f.search().isForWidget;var l,c;e.selectForWidget=function(e){r.onStopSelected(e.place)},e.showOnMap=function(e){c=e.place,i.showOnMap(e.place)},e.canRefreshVisits=function(){var t=!1;return e.stops.forEach(function(e){e.visitsAvailable&&(t=!0)}),t},e.refreshVisits=p,window.onMonitoringInfoArrived=function(t){e.$apply(function(){e.stops.forEach(function(e){e.visitsAvailable=!0}),t.Stops.forEach(function(n){e.stops.forEach(function(e){if(e.stopCode!==n.MotiroringRef)return;e.visits=[],n.StopVisits.forEach(function(n){var r=v(d(t.ResponseTimestamp),d(n.ExpectedArrivalTime));e.visits.push({lineNumber:n.PublishedLineName,destination:"",minutesToArrival:r,isAlreadyHere:r<1})})})})})},o.listen(function(){e.$apply(p)}),u.listen(function(){e.$apply(p)}),s.listen(function(e){c=e})}]),angular.element(document).ready(function(){angular.bootstrap(document,["tahanot"]),$("#nearby-stops-section").show()})}),define("angular/nearbyStopsController",function(){}),require(["mapStops"]),require(["geolocationTracker"]),require(["searchBar"]),require(["mapPageScroller"]),require(["angular/nearbyStopsController"]),visualTouches(),define("mapMain",function(){});