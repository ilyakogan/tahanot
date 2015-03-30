using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace GtfsBackend.Entities
{
    public class Route
    {
        [XmlAttribute]
        public int RouteId;
        [XmlAttribute]
        public string RouteName;

        [XmlArrayItem("StopTime")]
        public List<StopTimeInRoute> OrderedStopTimes;

        public List<Trip> Trips;
    }
}
