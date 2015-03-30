using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities;
using GtfsBackend.Entities.Raw;
using GtfsBackend.GtfsFiles;
using System.Diagnostics;

namespace GtfsBackend
{
    class StopCreator
    {
        Dictionary<int, StopOnRoute> stopsOnRoutes = new Dictionary<int, StopOnRoute>();
        Dictionary<int, Route> routes = new Dictionary<int, Route>();

        public Stop CreateStopWithFullInfo(
            int stopId, List<GtfsStopTime> stopTimes, StopTimesFile stopTimesFile, TripsFile tripsFile, RoutesFile routesFile, StopsFile stopsFile)
        {
            try
            {
                GtfsStop rawStop = stopsFile.GetItem(stopId);
                Stop stop = new Stop { RawStop = rawStop }; //, Times = new List<StopTimeInStop>() };
                foreach (GtfsStopTime rawStopTime in stopTimes)
                {
                    string tripId = rawStopTime.TripId;
                    AddTrip(tripId, stopTimesFile, tripsFile, routesFile, stopsFile);
                    //StopTimeInStop stopTime = CreateStopTimeWithTripInfo(rawStopTime, stopTimesFile, tripsFile, routesFile, stopsFile);
                    //stop.Times.Add(stopTime);
                }
                // TODO: Sort routes by short name as number
                stop.Routes = routes.Values.ToList();
                stop.OtherStopsOnRoutes = stopsOnRoutes.Values.ToList();
                return stop;
            }
            catch (Exception ex)
            {
                Trace.TraceError("Error creating stop " + stopId + ": " + ex);
                return null;
            }
        }

        //private StopTimeInStop CreateStopTimeWithTripInfo(
        //    GtfsStopTime rawStopTime, StopTimesFile stopTimesFile, TripsFile tripsFile, RoutesFile routesFile, StopsFile stopsFile)
        //{
        //    string tripId = rawStopTime.TripId;
        //    AddTrip(tripId, stopTimesFile, tripsFile, routesFile, stopsFile);
        //    StopTimeInStop stopTime = new StopTimeInStop { StopTime = rawStopTime, Trip = trip };
        //    return stopTime;
        //}

        private void AddTrip(string tripId, StopTimesFile stopTimesFile, TripsFile tripsFile, RoutesFile routesFile, StopsFile stopsFile)
        {
            GtfsTrip rawTrip = tripsFile.GetItem(tripId);
            SortedList<int, GtfsStopTime> rawStopTimesInTrip = stopTimesFile.OrderedStopTimesByTrip[tripId];
            int routeId = rawTrip.RouteId;
            if (!routes.ContainsKey(routeId))
            {
                GtfsRoute rawRoute = routesFile.GetItem(routeId);
                routes[routeId] = new Route
                    {
                        RouteId = routeId,
                        RouteName = rawRoute.ShortName,
                        OrderedStopTimes = CreateStopsOnRoute(rawStopTimesInTrip, stopsFile),
                        Trips = new List<Trip>()
                    };
            }
            
            Route route = routes[routeId];
            route.Trips.Add(new Trip(TimeAtFirstStop(rawStopTimesInTrip)));
        }

        private static TimeSpan TimeAtFirstStop(SortedList<int, GtfsStopTime> rawStopTimesInTrip)
        {
            return rawStopTimesInTrip.First().Value.DepartureTimeOfDay;
        }

        private List<StopTimeInRoute> CreateStopsOnRoute(SortedList<int, GtfsStopTime> rawStopTimesInTrip, StopsFile stopsFile)
        {
            List<StopTimeInRoute> stopTimesInRoute = new List<StopTimeInRoute>();
            foreach (GtfsStopTime rawStopTime in rawStopTimesInTrip.Values)
            {
                stopTimesInRoute.Add(new StopTimeInRoute(rawStopTime, TimeAtFirstStop(rawStopTimesInTrip)));

                int stopId = rawStopTime.StopId;
                if (!stopsOnRoutes.ContainsKey(stopId))
                {
                    GtfsStop rawStop = stopsFile.GetItem(stopId);
                    stopsOnRoutes[stopId] = new StopOnRoute(rawStop);
                }
            }
            return stopTimesInRoute;
        }
    }
}
