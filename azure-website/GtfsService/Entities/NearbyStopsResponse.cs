using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace GtfsBackend.Entities
{
    public class NearbyStopsResponse
    {
        public class StopSignWithDistance
        {
            [XmlAttribute]
            public double DistanceFromPoi;
            public StopSign StopSign;
        }

        public List<StopSignWithDistance> StopsByDistance;

        public NearbyStopsResponse()
        {

        }

        public NearbyStopsResponse(Dictionary<double, StopSign> StopsByDistance)
        {
            this.StopsByDistance = 
                StopsByDistance
                .Select(s => new StopSignWithDistance { StopSign = s.Value, DistanceFromPoi = s.Key })
                .OrderBy(s => s.DistanceFromPoi)
                .ToList();
        }

        public void RemoveFirstStops(int numStopsToRemove)
        {
            StopsByDistance = StopsByDistance.Skip(numStopsToRemove).ToList();
        }
    }
}
