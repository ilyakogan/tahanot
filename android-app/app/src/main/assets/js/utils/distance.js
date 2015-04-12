define("utils/distance", function() {
    return function(location1, location2)
    {
      var x = location1.k - location2.k;         
      var y = location1.D - location2.D;          
      return Math.sqrt(x*x + y*y);
    }
});