using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities;
using GtfsBackend.Entities.Raw;

namespace GtfsBackend
{
    class StopSignGenerator
    {
        static int MAX_MIDPOINTS = 10;
        static int MIN_STOPS_IN_TOWN = 2;

        private GtfsDatabaseRetriever db;

        public StopSignGenerator(GtfsDatabaseRetriever gtfsDatabaseRetriever)
        {
            this.db = gtfsDatabaseRetriever;
        }

        public StopSign CreateStopSignByStopCode(int stopCode)
        {
            GtfsStop rawStop = db.GetStops().GetItems().FirstOrDefault(s => s.Code == stopCode);
            if (rawStop == null) return null;
            return CreateStopSign(rawStop.Id);
        }

        public StopSign CreateStopSign(int stopId)
        {
            Stop stop = db.GetStop(stopId);
            StopSign stopSign = new StopSign(stop.RawStop);

            var otherStops = stop.OtherStopsOnRoutes.ToDictionary(s => s.Id, s => s);
            List<TrueRouteForSign> trueRoutes = new List<TrueRouteForSign>();
            foreach (Route route in stop.Routes)
            {
                TrueRouteForSign trueRoute = CreateRoute(route, stop.RawStop, otherStops);
                trueRoutes.Add(trueRoute);
            }

            var routesOnSign = UnifyAlternatives(trueRoutes);

            stopSign.Routes = routesOnSign.OrderByDescending(r => r.TripsTotal).ToList();
            return stopSign;
        }

        private TrueRouteForSign CreateRoute(Route route, GtfsStop thisStop, Dictionary<int, StopOnRoute> otherStops)
        {
            TrueRouteForSign trueRoute = new TrueRouteForSign();
            trueRoute.NumOfTripsInSchedule = route.Trips.Count;
            SetRouteDestination(trueRoute, route, thisStop, otherStops);
            SetRouteMidpoints(trueRoute, route, thisStop, otherStops);
            trueRoute.RouteName = route.RouteName;
            trueRoute.RouteId = route.RouteId;
            return trueRoute;
        }

        private static void SetRouteDestination(TrueRouteForSign trueRoute, Route route, GtfsStop thisStop, Dictionary<int, StopOnRoute> otherStops)
        {
            int lastStopId = route.OrderedStopTimes.Last().StopId;
            StopOnRoute lastStop = otherStops[lastStopId];

            StopOnRoute[] stopsBeforeThis = 
                route.OrderedStopTimes
                .TakeWhile(stopTime => stopTime.StopId != thisStop.Id)
                .Select(stopTime => otherStops[stopTime.StopId])
                .ToArray();

            bool thisStopIsOnTheWayFromSomeTownToItself = stopsBeforeThis.Any(s => s.Town == lastStop.Town);

            if (thisStop.Id == lastStopId)
            {
                trueRoute.IsThisDestination = true;
            }

            trueRoute.Destination = new StopSignRouteDestination { StopId = lastStop.Id };

            if (thisStop.Town == lastStop.Town)
            {
                // The town is not important
                trueRoute.Destination.Text1 = lastStop.Name;
                //trueRoute.Destination.Text2 = lastStop.StreetAddress;            
            }
            else
            {
                if (thisStopIsOnTheWayFromSomeTownToItself)
                {
                    // The town is important, but is not enough
                    trueRoute.Destination.Text1 = lastStop.Town + " - " + lastStop.Name;
                }
                else
                {
                    // The town is important and is enough
                    trueRoute.Destination.Text1 = lastStop.Town;
                }
                trueRoute.Destination.Text2 = lastStop.Name;
            }
        }

        private static void SetRouteMidpoints(TrueRouteForSign trueRoute, Route route, GtfsStop thisStop, Dictionary<int, StopOnRoute> otherStops)
        {
            string lastStopTown = otherStops[route.OrderedStopTimes.Last().StopId].Town;

            var stopsFurtherOnRoute = route.OrderedStopTimes.SkipWhile(stopTime => stopTime.StopId != thisStop.Id).Skip(1);

            IEnumerable<string> wayTowns =
                from stopTime in stopsFurtherOnRoute
                let stop = otherStops[stopTime.StopId]
                let isIntercity = !string.IsNullOrEmpty(stop.StreetAddress) && "0123456789".Contains(stop.StreetAddress[0])
                where !isIntercity
                select stop.Town;

            // Only select towns with X or more stops
            trueRoute.Midpoints = wayTowns
                .GroupBy(x => x)
                .Where(group => group.Count() >= MIN_STOPS_IN_TOWN)
                .Select(group => group.Key).ToList();

            trueRoute.Midpoints.Remove(thisStop.Town);
            trueRoute.Midpoints.Remove(lastStopTown);
            trueRoute.Midpoints = trueRoute.Midpoints.Take(MAX_MIDPOINTS).ToList();
        }

        private List<StopSignRoute> UnifyAlternatives(List<TrueRouteForSign> trueRoutes)
        {
            List<StopSignRoute> routesOnSign = new List<StopSignRoute>();

            foreach (TrueRouteForSign trueRoute in trueRoutes)
            {
                StopSignRoute routeOnSign = routesOnSign.FirstOrDefault(r => r.RouteName == trueRoute.RouteName && r.Destination.StopId == trueRoute.Destination.StopId);
                if (routeOnSign == null)
                {
                    routeOnSign = new StopSignRoute(trueRoute.RouteName, trueRoute.IsThisDestination, trueRoute.Destination);
                    routeOnSign.Midpoints = new List<string>(trueRoute.Midpoints);
                    routesOnSign.Add(routeOnSign);
                }

                routeOnSign.RouteIds.Add(trueRoute.RouteId);
                routeOnSign.TripsTotal += trueRoute.NumOfTripsInSchedule;
                routeOnSign.Midpoints.RemoveAll(midpoint => !trueRoute.Midpoints.Contains(midpoint));
            }

            return routesOnSign;
        }

        class TrueRouteForSign
        {
            /// <summary>
            /// The same trip on Sunday to Thursday counts as one trip, but Friday and Saturday are always distinct.
            /// </summary>
            public int NumOfTripsInSchedule;

            public StopSignRouteDestination Destination;
            public int RouteId;
            public string RouteName;
            public bool IsThisDestination;
            public List<string> Midpoints;
        }
    }
}
