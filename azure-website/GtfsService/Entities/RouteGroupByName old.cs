//using System;
//using System.Collections.Generic;
//using System.Linq;
//using System.Text;

//namespace GtfsBackend.Entities
//{
//    public class RouteGroupByName
//    {
//        public string RouteName;
//        public List<StopSignRoute> Routes;

//        public RouteGroupByName(string routeName, List<StopSignRoute> list)
//        {
//            RouteName = routeName;
//            Routes = list;
//        }

//        /// <summary>
//        /// The same trip on Sunday to Thursday counts as one trip, but Friday and Saturday are always distinct.
//        /// </summary>
//        public int NumOfTripsInSchedule()
//        {
//            return Routes.Sum(r => r.NumOfTripsInSchedule);
//        }
//    }
//}
