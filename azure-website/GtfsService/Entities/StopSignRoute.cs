using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace GtfsBackend.Entities
{
    public class StopSignRoute
    {
        /// <summary>
        /// The same trip on Sunday to Thursday counts as one trip, but Friday and Saturday are always distinct.
        /// </summary>
        [XmlAttribute]
        public int TripsTotal;

        public StopSignRouteDestination Destination;
        public List<int> RouteIds;
        [XmlAttribute]
        public string RouteName;
        [XmlIgnore]
        public bool IsThisDestination;
        public List<string> Midpoints;

        public StopSignRoute()
        {

        }

        public StopSignRoute(string routeName, bool isThisDestination, StopSignRouteDestination destination)
        {
            RouteName = routeName;
            IsThisDestination = isThisDestination;
            Destination = destination;
            RouteIds = new List<int>();
            TripsTotal = 0;
        }
    }
}
