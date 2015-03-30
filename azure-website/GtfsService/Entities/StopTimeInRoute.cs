using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities.Raw;
using System.Xml.Serialization;

namespace GtfsBackend.Entities
{
    public class StopTimeInRoute
    {
        [XmlAttribute]
        public int StopId;
        [XmlAttribute]
        public int SecFromDeparture;
        [XmlAttribute]
        public string Type;

        public StopTimeInRoute()
        {

        }

        public StopTimeInRoute(GtfsStopTime rawStopTime, TimeSpan timeAtFirstStop)
        {
            StopId = rawStopTime.StopId;
            SecFromDeparture = (int)(rawStopTime.DepartureTimeOfDay - timeAtFirstStop).TotalSeconds;
            Type = rawStopTime.Type;          
        }
    }
}
