using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SiriBackend
{
    public class StopVisit
    {
        public string LineRef { get; set; }
        public string RouteShortName { get; set; }
        public string RouteLongName { get; set; }
        public DateTime ExpectedArrivalTime { get; set; }
        public string PublishedLineName { get; set; }
        public string DestinationRef { get; set; }
    }
}
