//using System;
//using System.Collections.Generic;
//using System.Linq;
//using System.Text;
//using GtfsBackend.Entities.Raw;
//using System.Xml.Serialization;

//namespace GtfsBackend.Entities
//{
//    public class Trip
//    {
//        [XmlAttribute]
//        public string TripId;
//        [XmlAttribute]
//        public int RouteId;
//        [XmlAttribute]
//        public string RouteName;
        
//        [XmlArray("OrderedTimes")]
//        [XmlArrayItem("Time")]
//        public List<StopTimeInRoute> OrderedStopTimes;

//        public Trip()
//        {

//        }

//        public Trip(GtfsTrip rawTrip, GtfsRoute rawRoute, List<StopTimeInRoute> orderedStopTimes)
//        {
//            TripId = rawTrip.TripId;
//            RouteId = rawTrip.RouteId;
//            RouteName = rawRoute.ShortName;
//            OrderedStopTimes = orderedStopTimes;
//        }
//    }
//}
