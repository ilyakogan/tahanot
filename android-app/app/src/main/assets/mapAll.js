<<<<<<< HEAD
function visualTouches(){$(document).ready(function(){$("#map-section").height($(window).height()).width("100%"),$("#hello-section").height($(window).height()).width("100%");var e=$("#navbar-main").outerHeight();$(".page-section").css("padding-top",e)}),$(".pagination a").click(function(){var e=jQuery("body").find($(this).attr("href")).offset().top;return $("body,html").animate({scrollTop:e},500,function(){}),!1}),$("body").scrollspy({target:"#navbar-main"})}define("utils/distance",[],function(){return function(e,n){var t=e.lat()-n.lat(),o=e.lng()-n.lng();return Math.sqrt(t*t+o*o)}}),define("eventServices/mapCenterChanged",["map","utils/distance"],function(e,n){var t,o=$.Callbacks(),i=null;return{broadcastDelayed:function(e){i&&clearTimeout(i),t=e(),i=setTimeout(function(){i=null;var a=n(t,e());a>.001||o.fire()},1e3)},broadcastNow:function(){o.fire()},listen:function(e){o.add(e)}}}),define("eventServices/stopAdded",[],function(){var e=$.Callbacks();return{broadcast:function(n){e.fire(n)},listen:function(n){e.add(n)}}}),define("nativeApp/nativeAppCallbacks/onLocationChanged",[],function(){var e=$.Callbacks();return window.onLocationChanged=function(n,t){e.fire(n,t)},{listen:function(n){e.add(n)}}}),define("map",["eventServices/mapCenterChanged","eventServices/stopAdded","nativeApp/nativeAppCallbacks/onLocationChanged"],function(e,n,t){function o(){var e=32.08,n=34.781,o=new google.maps.LatLng(e,n);c=new google.maps.Map(document.getElementById("map-canvas"),{center:o,streetViewControl:!1,panControl:!1,zoomControlOptions:{style:google.maps.ZoomControlStyle.SMALL,position:google.maps.ControlPosition.RIGHT_BOTTOM},mapTypeControlOptions:{mapTypeIds:[google.maps.MapTypeId.ROADMAP,google.maps.MapTypeId.SATELLITE]},zoom:17}),r(),navigator.geolocation&&navigator.geolocation.getCurrentPosition(function(e){e&&e.coords&&i(e.coords.latitude,e.coords.longitude)}),t.listen(i)}function i(n,t){var o=new google.maps.LatLng(n,t);if(l||(l=!0,c.setCenter(o),e.broadcastNow()),d)d.setPosition(o);else{var i={path:google.maps.SymbolPath.CIRCLE,fillColor:"red",fillOpacity:.4,scale:4.5,strokeColor:"black",strokeWeight:1};d=new google.maps.Marker({position:o,icon:i,map:c})}}function a(){return c.getCenter()}function r(){google.maps.event.addListener(c,"idle",function(){e.broadcastDelayed(a)})}function s(e){google.maps.event.addListenerOnce(c,"idle",function(){e()})}var c,d,l=!1;return o(),{panTo:function(e){c.panTo(e)},getCenter:a,addIdleListenerOnce:s,googleMap:c}}),define("parseClient",[],function(){function e(e){return{code:e.get("code"),name:e.get("name"),town:e.get("town"),location:e.get("location")}}return{getNearbyStops:function(n,t){var o=new Parse.GeoPoint(n,t),i=new Parse.Query(Parse.Object.extend("stop"));return i.near("location",o),i.limit(10),i.find().then(function(n){return $.map(n,e)})},getStopByCode:function(n){var t=new Parse.Query(Parse.Object.extend("stop"));return t.equalTo("code",n),t.first().then(e)}}}),define("stopCache",["parseClient","eventServices/stopAdded"],function(e,n){function t(e){return void 0!==s[e.code]}function o(e){return function(n,t){return distance1=n.location.kilometersTo(e),distance2=t.location.kilometersTo(e),distance1-distance2}}function i(e,n,t){var i=new Parse.GeoPoint(e,n),a=[];for(var r in s)s[r].location.kilometersTo(i)<t&&a.push(s[r]);return a.sort(o(i)),a}function a(o,i){e.getNearbyStops(o,i).then(function(e){e.forEach(function(e){t(e)||(s[e.code]=e,n.broadcast(e))})})}function r(n){var t=$.Deferred(),o=s[n];return o?t.resolve(o):e.getStopByCode(n).then(function(e){s[e.code]=e,t.resolve(e)}),t.promise()}var s={};return{getStopsAround:i,addStopsAround:a,getOrAdd:r,get:function(e){return s[e]}}}),define("eventServices/mapStopClicked",[],function(){var e=$.Callbacks();return{broadcast:function(n){e.fire(n)},listen:function(n){e.add(n)}}}),define("mapStops",["map","stopCache","eventServices/mapCenterChanged","eventServices/mapStopClicked","eventServices/stopAdded","utils/distance"],function(e,n,t,o,i){function a(){n.addStopsAround(e.getCenter().lat(),e.getCenter().lng())}function r(n){var t=new MarkerWithLabel({position:new google.maps.LatLng(n.location.latitude,n.location.longitude),map:e.googleMap,labelContent:n.code,labelClass:"labels",labelAnchor:new google.maps.Point(-10,32),title:n.name,icon:s});google.maps.event.addListener(t,"click",function(){o.broadcast(n)})}var s={url:"images/bus_marker_wide.png",size:new google.maps.Size(74,37),origin:new google.maps.Point(0,0),anchor:new google.maps.Point(15,41)};t.listen(function(){a()}),i.listen(r),a()}),define("geolocationTracker",["map"],function(e){function n(){return new google.maps.Marker({clickable:!1,icon:new google.maps.MarkerImage("//maps.gstatic.com/mapfiles/mobile/mobileimgs2.png",new google.maps.Size(22,22),new google.maps.Point(0,18),new google.maps.Point(11,11)),shadow:null,zIndex:999,map:e.googleMap})}function t(){navigator.geolocation.getCurrentPosition(function(e){var n=new google.maps.LatLng(e.coords.latitude,e.coords.longitude);i.setPosition(n)},function(){})}function o(){i=n(),navigator.geolocation&&t()}var i;o()}),define("eventServices/addressEntered",[],function(){var e=$.Callbacks();return{broadcast:function(n,t){e.fire(n,t)},listen:function(n){e.add(n)}}}),define("searchBar",["map","eventServices/mapStopClicked","eventServices/addressEntered","stopCache"],function(e,n,t,o){function i(){autocomplete=new google.maps.places.Autocomplete(document.getElementById("address"),{types:["geocode"],componentRestrictions:{country:"il"}}),google.maps.event.addListener(autocomplete,"place_changed",function(){r(),s(),$("#searchCollapse").collapse("hide")}),$("#address").blur(function(){$("#searchCollapse").collapse("hide")}),$("#address").click(function(){$("#address").select()})}function a(e){var n=$.Deferred();return c.geocode({address:e},function(e,t){t==google.maps.GeocoderStatus.OK?n.resolve(e[0].geometry.location):n.reject()}),n}function r(){var e=document.getElementById("address").value;isNaN(e)?a(e).done(function(e){t.broadcast(e.lat(),e.lng())}):o.getOrAdd(parseInt(e)).then(function(e){n.broadcast(e)})}function s(){var e=document.activeElement;e?e.blur():document.parentElement?document.parentElement.focus():window.focus()}var c=new google.maps.Geocoder;i()}),define("mapPageScroller",["map","eventServices/mapStopClicked","eventServices/addressEntered","eventServices/mapCenterChanged"],function(e,n,t,o){function i(e,n){var t=$(e).offset().top;$("body,html").animate({scrollTop:t},500,n)}return n.listen(function(n){e.panTo(new google.maps.LatLng(n.location.latitude,n.location.longitude)),setTimeout(function(){i("#nearby-stops-section")},700)}),t.listen(function(n,t){i("#map-section",function(){e.addIdleListenerOnce(function(){o.broadcastNow()}),e.panTo(new google.maps.LatLng(n,t))})}),{showOnMap:function(n,t){i("#map-section",function(){e.panTo(new google.maps.LatLng(n,t))})}}}),define("angular/tahanotApp",[],function(){var e=angular.module("tahanot",[]);return e.config(["$locationProvider",function(e){e.html5Mode({enabled:!0,requireBase:!1})}]),{app:e}}),define("nativeApp/bridge",["eventServices/stopAdded"],function(e){function n(){i||(i=!0,window.AndroidBridge?window.AndroidBridge.onFirstStopDisplayed():console.log("Bridge: onFirstStopDisplayed"))}function t(e,n){window.AndroidBridge?window.AndroidBridge.onStopSelected(e,n):console.log("Bridge: onStopSelected: "+name)}function o(e){window.AndroidBridge?window.AndroidBridge.requestStopMonitoring(e):(console.log("Bridge: requestStopMonitoring"),setTimeout(function(){if(e%2==0){var n=function(e){return{ExpectedArrivalTime:"/Date(1427919976000)/",PublishedLineName:""+100*e,DestinationRef:"21165"}};onMonitoringInfoArrived([e],{Stops:[{MotiroringRef:e,StopVisits:Array.apply(null,new Array(10)).map(function(e,t){return n(t)})}],ResponseTimestamp:"/Date(1427919007162)/"})}else onMonitoringInfoArrived([e],{Stops:[{MotiroringRef:11111111111,StopVisits:[{ExpectedArrivalTime:"/Date(1427919876000)/",PublishedLineName:"66",DestinationRef:"21165"}]}],ResponseTimestamp:"/Date(1427919007162)/"})},2e3))}var i=!1;return e.listen(function(){return n()}),{onStopSelected:t,requestStopMonitoring:o}}),define("nativeApp/nativeAppCallbacks/onMonitoringInfoArrived",[],function(){function e(e){return visitsByStopCode=[],e.forEach(function(e){visitsByStopCode[e]=[]}),visitsByStopCode}function n(e,n){n.Stops.forEach(function(t){var a=t.MotiroringRef,r=[];t.StopVisits.forEach(function(e){var t=i(o(n.ResponseTimestamp),o(e.ExpectedArrivalTime));r.push({lineNumber:e.PublishedLineName,destinationRef:parseInt(e.DestinationRef),minutesToArrival:t,isAlreadyHere:1>t})}),e[a]=r})}function t(e){for(var n in e){var t=e[n];a.fire(parseInt(n),t)}}function o(e){return new Date(parseInt(e.replace("/Date(","").replace(")/",""),10))}function i(e,n){return Math.floor((n-e)/6e4)}var a=$.Callbacks();return window.onMonitoringInfoArrived=function(o,i){visitsByStopCode=e(o),n(visitsByStopCode,i),t(visitsByStopCode)},{listen:function(e){a.add(e)}}}),define("stopMonitoringCache",["nativeApp/bridge","nativeApp/nativeAppCallbacks/onMonitoringInfoArrived"],function(e,n){function t(n){var t=this;this.stopCode=n,this.ageOfData=void 0,this.lastRequestSent=void 0,this.visits=[],this.deferreds=[],this.updateVisits=function(e){t.visits=e,t.ageOfData=new Date,t.deferreds.forEach(function(n){n.resolve(e)}),t.deferreds=[]},this.requestStopMonitoring=function(o){this.ageOfData=void 0,e.requestStopMonitoring(n),t.lastRequestSent=new Date,t.deferreds=[o]}}function o(e,n,t){var o=$.Deferred(),a=i(e);return n?(a.requestStopMonitoring(o),setTimeout(function(){o.reject()},t)):r(a.ageOfData)?o.resolve(a.visits):r(a.lastRequestSent)?(a.deferreds.push(o),setTimeout(function(){o.reject()},t)):(a.requestStopMonitoring(o),setTimeout(function(){o.reject()},t)),o.promise()}function i(e){var n=a(e);return n||(n=new t(e),c.push(n)),n}function a(e){for(var n=0;n<c.length;n++)if(c[n].stopCode===e)return c[n]}function r(e){return e&&e>new Date-s}const s=3e4;var c=[];return n.listen(function(e,n){var t=a(e);t&&t.updateVisits(n)}),{get:o}}),define("nativeApp/nativeAppCallbacks/setIsForWidget",[],function(){var e=$.Callbacks();return window.setIsForWidget=function(n){e.fire(n)},{listen:function(n){e.add(n)}}}),require(["angular/tahanotApp","map","stopCache","stopMonitoringCache","nativeApp/bridge","mapPageScroller","eventServices/mapStopClicked","eventServices/mapCenterChanged","eventServices/stopAdded","nativeApp/nativeAppCallbacks/setIsForWidget"],function(e,n,t,o,i,a,r,s,c,d){e.app.controller("nearbyStopsController",["$scope","$http",function(e){function l(){e.stops=[];var o=t.getStopsAround(n.getCenter().lat(),n.getCenter().lng(),5);if(v){var i=t.get(v);p(u(i,!0))}o.slice(0,8).forEach(function(e){e.code!=v&&p(u(e,!1))})}function u(e,n){return e.location||console.log(e.code),{stopCode:e.code,name:e.name,lat:e.location.latitude,lng:e.location.longitude,visitsAvailable:!1,isSelected:n}}function p(n){e.stops.push(n),f(n)}function f(n,i){n.visits=[],n.isReceivingVisits=!0,n.failedReceivingVisits=!1,o.get(n.stopCode,i,3e4).then(function(o){g(function(){-1!==e.stops.indexOf(n)&&(n.visits=o,n.isReceivingVisits=!1,o.forEach(function(e){t.getOrAdd(e.destinationRef).then(function(n){g(function(){e.destination=n.name+", "+n.town})})}))})},function(){e.$apply(function(){stop.isReceivingVisits=!1,stop.failedReceivingVisits=!0})})}function g(n){e.$$phase?n():e.$apply(n)}e.stops=[],e.isForWidget=!1;var v;e.selectForWidget=function(e){i.onStopSelected(e.code,e.name)},e.showOnMap=function(e){v=e.code,a.showOnMap(e.lat,e.lng)},e.refreshVisits=function(e){f(e,!0)},s.listen(function(){e.$apply(l)}),c.listen(function(){e.$apply(function(){e.stops&&e.stops.length||l()})}),r.listen(function(e){v=e.code}),d.listen(function(n){e.$apply(function(){e.isForWidget=n})})}]),angular.element(document).ready(function(){angular.bootstrap(document,["tahanot"]),$("#nearby-stops-section").show()})}),define("angular/nearbyStopsController",function(){}),require(["mapStops"]),require(["geolocationTracker"]),require(["searchBar"]),require(["mapPageScroller"]),require(["angular/nearbyStopsController"]),require(["nativeApp/nativeAppCallbacks/onMonitoringInfoArrived"]),require(["nativeApp/nativeAppCallbacks/onLocationChanged"]),require(["nativeApp/nativeAppCallbacks/setIsForWidget"]),Parse.initialize("Yyxpr1XarbQvtfHNYWKHUGlFdKDjWttPKLYgMXBe","xscYP2yXXd0udn26sXIhaCCIEW28pN2Ux2th2IJS"),visualTouches(),define("mapMain",function(){});
=======
function visualTouches(){$(document).ready(function(){$("#map-section").height($(window).height()).width("100%"),$("#hello-section").height($(window).height()).width("100%");var e=$("#navbar-main").outerHeight();$(".page-section").css("padding-top",e)}),$(".pagination a").click(function(){var e=jQuery("body").find($(this).attr("href")).offset().top;return $("body,html").animate({scrollTop:e},500,function(){}),!1}),$("body").scrollspy({target:"#navbar-main"})}define("utils/distance",[],function(){return function(e,n){var t=e.lat()-n.lat(),o=e.lng()-n.lng();return Math.sqrt(t*t+o*o)}}),define("eventServices/mapCenterChanged",["map","utils/distance"],function(e,n){var t,o=$.Callbacks(),i=null;return{broadcastDelayed:function(e){i&&clearTimeout(i),t=e(),i=setTimeout(function(){i=null;var a=n(t,e());a>.001||o.fire()},1e3)},broadcastNow:function(){o.fire()},listen:function(e){o.add(e)}}}),define("eventServices/stopAdded",[],function(){var e=$.Callbacks();return{broadcast:function(n){e.fire(n)},listen:function(n){e.add(n)}}}),define("nativeApp/nativeAppCallbacks/onLocationChanged",[],function(){var e=$.Callbacks();return window.onLocationChanged=function(n,t){e.fire(n,t)},{listen:function(n){e.add(n)}}}),define("map",["eventServices/mapCenterChanged","eventServices/stopAdded","nativeApp/nativeAppCallbacks/onLocationChanged"],function(e,n,t){function o(){var e=32.08,n=34.781,o=new google.maps.LatLng(e,n);c=new google.maps.Map(document.getElementById("map-canvas"),{center:o,streetViewControl:!1,panControl:!1,zoomControlOptions:{style:google.maps.ZoomControlStyle.SMALL,position:google.maps.ControlPosition.RIGHT_BOTTOM},mapTypeControlOptions:{mapTypeIds:[google.maps.MapTypeId.ROADMAP,google.maps.MapTypeId.SATELLITE]},zoom:17}),r(),navigator.geolocation&&navigator.geolocation.getCurrentPosition(function(e){e&&e.coords&&i(e.coords.latitude,e.coords.longitude)}),t.listen(i)}function i(n,t){var o=new google.maps.LatLng(n,t);if(l||(l=!0,c.setCenter(o),e.broadcastNow()),d)d.setPosition(o);else{var i={path:google.maps.SymbolPath.CIRCLE,fillColor:"red",fillOpacity:.4,scale:4.5,strokeColor:"black",strokeWeight:1};d=new google.maps.Marker({position:o,icon:i,map:c})}}function a(){return c.getCenter()}function r(){google.maps.event.addListener(c,"idle",function(){e.broadcastDelayed(a)})}function s(e){google.maps.event.addListenerOnce(c,"idle",function(){e()})}var c,d,l=!1;return o(),{panTo:function(e){c.panTo(e)},getCenter:a,addIdleListenerOnce:s,googleMap:c}}),define("parseClient",[],function(){function e(e){return{code:e.get("code"),name:e.get("name"),town:e.get("town"),location:e.get("location")}}return{getNearbyStops:function(n,t){var o=new Parse.GeoPoint(n,t),i=new Parse.Query(Parse.Object.extend("stop"));return i.near("location",o),i.limit(10),i.find().then(function(n){return $.map(n,e)})},getStopByCode:function(n){var t=new Parse.Query(Parse.Object.extend("stop"));return t.equalTo("code",n),t.first().then(e)}}}),define("stopCache",["parseClient","eventServices/stopAdded"],function(e,n){function t(e){return void 0!==s[e.code]}function o(e){return function(n,t){return distance1=n.location.kilometersTo(e),distance2=t.location.kilometersTo(e),distance1-distance2}}function i(e,n,t){var i=new Parse.GeoPoint(e,n),a=[];for(var r in s)s[r].location.kilometersTo(i)<t&&a.push(s[r]);return a.sort(o(i)),a}function a(o,i){e.getNearbyStops(o,i).then(function(e){e.forEach(function(e){t(e)||(s[e.code]=e,n.broadcast(e))})})}function r(n){var t=$.Deferred(),o=s[n];return o?t.resolve(o):e.getStopByCode(n).then(function(e){s[e.code]=e,t.resolve(e)}),t.promise()}var s={};return{getStopsAround:i,addStopsAround:a,getOrAdd:r,get:function(e){return s[e]}}}),define("eventServices/mapStopClicked",[],function(){var e=$.Callbacks();return{broadcast:function(n){e.fire(n)},listen:function(n){e.add(n)}}}),define("mapStops",["map","stopCache","eventServices/mapCenterChanged","eventServices/mapStopClicked","eventServices/stopAdded","utils/distance"],function(e,n,t,o,i){function a(){n.addStopsAround(e.getCenter().lat(),e.getCenter().lng())}function r(n){var t=new google.maps.Marker({map:e.googleMap,title:n.name,icon:s,position:new google.maps.LatLng(n.location.latitude,n.location.longitude)});google.maps.event.addListener(t,"click",function(){o.broadcast(n)})}var s={url:"images/bus_marker.png",size:new google.maps.Size(33,61),origin:new google.maps.Point(0,0),anchor:new google.maps.Point(16,63)};t.listen(function(){a()}),i.listen(r),a()}),define("geolocationTracker",["map"],function(e){function n(){return new google.maps.Marker({clickable:!1,icon:new google.maps.MarkerImage("//maps.gstatic.com/mapfiles/mobile/mobileimgs2.png",new google.maps.Size(22,22),new google.maps.Point(0,18),new google.maps.Point(11,11)),shadow:null,zIndex:999,map:e.googleMap})}function t(){navigator.geolocation.getCurrentPosition(function(e){var n=new google.maps.LatLng(e.coords.latitude,e.coords.longitude);i.setPosition(n)},function(){})}function o(){i=n(),navigator.geolocation&&t()}var i;o()}),define("eventServices/addressEntered",[],function(){var e=$.Callbacks();return{broadcast:function(n,t){e.fire(n,t)},listen:function(n){e.add(n)}}}),define("searchBar",["map","eventServices/mapStopClicked","eventServices/addressEntered","stopCache"],function(e,n,t,o){function i(){autocomplete=new google.maps.places.Autocomplete(document.getElementById("address"),{types:["geocode"],componentRestrictions:{country:"il"}}),google.maps.event.addListener(autocomplete,"place_changed",function(){r(),s(),$("#searchCollapse").collapse("hide")}),$("#address").blur(function(){$("#searchCollapse").collapse("hide")}),$("#address").click(function(){$("#address").select()})}function a(e){var n=$.Deferred();return c.geocode({address:e},function(e,t){t==google.maps.GeocoderStatus.OK?n.resolve(e[0].geometry.location):n.reject()}),n}function r(){var e=document.getElementById("address").value;isNaN(e)?a(e).done(function(e){t.broadcast(e.lat(),e.lng())}):o.getOrAdd(parseInt(e)).then(function(e){n.broadcast(e)})}function s(){var e=document.activeElement;e?e.blur():document.parentElement?document.parentElement.focus():window.focus()}var c=new google.maps.Geocoder;i()}),define("mapPageScroller",["map","eventServices/mapStopClicked","eventServices/addressEntered","eventServices/mapCenterChanged"],function(e,n,t,o){function i(e,n){var t=$(e).offset().top;$("body,html").animate({scrollTop:t},500,n)}return n.listen(function(n){e.panTo(new google.maps.LatLng(n.location.latitude,n.location.longitude)),setTimeout(function(){i("#nearby-stops-section")},700)}),t.listen(function(n,t){i("#map-section",function(){e.addIdleListenerOnce(function(){o.broadcastNow()}),e.panTo(new google.maps.LatLng(n,t))})}),{showOnMap:function(n,t){i("#map-section",function(){e.panTo(new google.maps.LatLng(n,t))})}}}),define("angular/tahanotApp",[],function(){var e=angular.module("tahanot",[]);return e.config(["$locationProvider",function(e){e.html5Mode({enabled:!0,requireBase:!1})}]),{app:e}}),define("nativeApp/bridge",["eventServices/stopAdded"],function(e){function n(){i||(i=!0,window.AndroidBridge?window.AndroidBridge.onFirstStopDisplayed():console.log("Bridge: onFirstStopDisplayed"))}function t(e,n){window.AndroidBridge?window.AndroidBridge.onStopSelected(e,n):console.log("Bridge: onStopSelected: "+e+", "+n)}function o(e){window.AndroidBridge?window.AndroidBridge.requestStopMonitoring(e):(console.log("Bridge: requestStopMonitoring"),setTimeout(function(){if(e%2==0){var n=function(e){return{ExpectedArrivalTime:"/Date(1427919976000)/",PublishedLineName:""+100*e,DestinationRef:"21165"}};onMonitoringInfoArrived([e],{Stops:[{MotiroringRef:e,StopVisits:Array.apply(null,new Array(10)).map(function(e,t){return n(t)})}],ResponseTimestamp:"/Date(1427919007162)/"})}else onMonitoringInfoArrived([e],{Stops:[{MotiroringRef:11111111111,StopVisits:[{ExpectedArrivalTime:"/Date(1427919876000)/",PublishedLineName:"66",DestinationRef:"21165"}]}],ResponseTimestamp:"/Date(1427919007162)/"})},2e3))}var i=!1;return e.listen(function(){return n()}),{onStopSelected:t,requestStopMonitoring:o}}),define("nativeApp/nativeAppCallbacks/onMonitoringInfoArrived",[],function(){function e(e){return visitsByStopCode=[],e.forEach(function(e){visitsByStopCode[e]=[]}),visitsByStopCode}function n(e,n){n.Stops.forEach(function(t){var a=t.MotiroringRef,r=[];t.StopVisits.forEach(function(e){var t=i(o(n.ResponseTimestamp),o(e.ExpectedArrivalTime));r.push({lineNumber:e.PublishedLineName,destinationRef:parseInt(e.DestinationRef),minutesToArrival:t,isAlreadyHere:1>t})}),e[a]=r})}function t(e){for(var n in e){var t=e[n];a.fire(parseInt(n),t)}}function o(e){return new Date(parseInt(e.replace("/Date(","").replace(")/",""),10))}function i(e,n){return Math.floor((n-e)/6e4)}var a=$.Callbacks();return window.onMonitoringInfoArrived=function(o,i){visitsByStopCode=e(o),n(visitsByStopCode,i),t(visitsByStopCode)},{listen:function(e){a.add(e)}}}),define("stopMonitoringCache",["nativeApp/bridge","nativeApp/nativeAppCallbacks/onMonitoringInfoArrived"],function(e,n){function t(n){var t=this;this.stopCode=n,this.ageOfData=void 0,this.lastRequestSent=void 0,this.visits=[],this.deferreds=[],this.updateVisits=function(e){t.visits=e,t.ageOfData=new Date,t.deferreds.forEach(function(n){n.resolve(e)}),t.deferreds=[]},this.requestStopMonitoring=function(o){this.ageOfData=void 0,e.requestStopMonitoring(n),t.lastRequestSent=new Date,t.deferreds=[o]}}function o(e,n,t){var o=$.Deferred(),a=i(e);return n?(a.requestStopMonitoring(o),setTimeout(function(){o.reject()},t)):r(a.ageOfData)?o.resolve(a.visits):r(a.lastRequestSent)?(a.deferreds.push(o),setTimeout(function(){o.reject()},t)):(a.requestStopMonitoring(o),setTimeout(function(){o.reject()},t)),o.promise()}function i(e){var n=a(e);return n||(n=new t(e),c.push(n)),n}function a(e){for(var n=0;n<c.length;n++)if(c[n].stopCode===e)return c[n]}function r(e){return e&&e>new Date-s}const s=3e4;var c=[];return n.listen(function(e,n){var t=a(e);t&&t.updateVisits(n)}),{get:o}}),define("nativeApp/nativeAppCallbacks/setIsForWidget",[],function(){var e=$.Callbacks();return window.setIsForWidget=function(n){e.fire(n)},{listen:function(n){e.add(n)}}}),require(["angular/tahanotApp","map","stopCache","stopMonitoringCache","nativeApp/bridge","mapPageScroller","eventServices/mapStopClicked","eventServices/mapCenterChanged","eventServices/stopAdded","nativeApp/nativeAppCallbacks/setIsForWidget"],function(e,n,t,o,i,a,r,s,c,d){e.app.controller("nearbyStopsController",["$scope","$http",function(e){function l(){e.stops=[];var o=t.getStopsAround(n.getCenter().lat(),n.getCenter().lng(),5);if(v){var i=t.get(v);p(u(i,!0))}o.slice(0,8).forEach(function(e){e.code!=v&&p(u(e,!1))})}function u(e,n){return e.location||console.log(e.code),{stopCode:e.code,name:e.name,lat:e.location.latitude,lng:e.location.longitude,visitsAvailable:!1,isSelected:n}}function p(n){e.stops.push(n),f(n)}function f(n,i){n.visits=[],n.isReceivingVisits=!0,n.failedReceivingVisits=!1,o.get(n.stopCode,i,3e4).then(function(o){g(function(){-1!==e.stops.indexOf(n)&&(n.visits=o,n.isReceivingVisits=!1,o.forEach(function(e){t.getOrAdd(e.destinationRef).then(function(n){g(function(){e.destination=n.name+", "+n.town})})}))})},function(){e.$apply(function(){stop.isReceivingVisits=!1,stop.failedReceivingVisits=!0})})}function g(n){e.$$phase?n():e.$apply(n)}e.stops=[],e.isForWidget=!1;var v;e.selectForWidget=function(e){i.onStopSelected(e.stopCode,e.name)},e.showOnMap=function(e){v=e.code,a.showOnMap(e.lat,e.lng)},e.refreshVisits=function(e){f(e,!0)},s.listen(function(){e.$apply(l)}),c.listen(function(){e.$apply(function(){e.stops&&e.stops.length||l()})}),r.listen(function(e){v=e.code}),d.listen(function(n){e.$apply(function(){e.isForWidget=n})})}]),angular.element(document).ready(function(){angular.bootstrap(document,["tahanot"]),$("#nearby-stops-section").show()})}),define("angular/nearbyStopsController",function(){}),require(["mapStops"]),require(["geolocationTracker"]),require(["searchBar"]),require(["mapPageScroller"]),require(["angular/nearbyStopsController"]),require(["nativeApp/nativeAppCallbacks/onMonitoringInfoArrived"]),require(["nativeApp/nativeAppCallbacks/onLocationChanged"]),require(["nativeApp/nativeAppCallbacks/setIsForWidget"]),Parse.initialize("Yyxpr1XarbQvtfHNYWKHUGlFdKDjWttPKLYgMXBe","xscYP2yXXd0udn26sXIhaCCIEW28pN2Ux2th2IJS"),visualTouches(),define("mapMain",function(){});
>>>>>>> df9946b... Fix: Stop selection for widget always sent 0
//# sourceMappingURL=mapAll.js
//# sourceMappingURL=mapAll.js.map