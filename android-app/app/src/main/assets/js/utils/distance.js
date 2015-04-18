define("utils/distance", function() {
    return function(location1, location2)
    {
      var x = location1.lat() - location2.lat();
      var y = location1.lng() - location2.lng();
      return Math.sqrt(x*x + y*y);
    }
});