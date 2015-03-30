using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities.Raw;
using GtfsBackend.GtfsFiles;
using GtfsBackend.Entities;
using System.Threading.Tasks;

namespace GtfsBackend
{
    public class GtfsFacade
    {
        public GtfsRoute GetRoute(int routeId)
        {
            RoutesFile routesFile = new GtfsDatabaseRetriever().GetRoutes();
            return routesFile.GetItem(routeId);
        }

        public List<NearbyStop> GetClosestStopsAroundPoi(double poiLat, double poiLon, int stopsCount)
        {
            var db = new GtfsDatabaseRetriever();
            TripsFile tripsFile = db.GetTrips();
            RoutesFile routesFile = db.GetRoutes();
            List<NearbyStop> stopsWithProximity = new StopsRetriever(db).FindNStopsAroundPoi(poiLat, poiLon, stopsCount);
            return stopsWithProximity;
        }

        public NearbyStopsResponse GetClosestStopSignsAroundPoi(double poiLat, double poiLon, int stopsCount)
        {
            var db = new GtfsDatabaseRetriever();
            TripsFile tripsFile;
            RoutesFile routesFile;
            Task.WaitAll(
                Task.Factory.StartNew(() => tripsFile = db.GetTrips()),
                Task.Factory.StartNew(() => routesFile = db.GetRoutes()));
            NearbyStopsResponse stopSigns = new StopsRetriever(db).FindNStopSignsAroundPoi(poiLat, poiLon, stopsCount);
            return stopSigns;
        }

        public StopSign GetStopSignById(int stopId)
        {
            var db = new GtfsDatabaseRetriever();
            StopSign stopSign = new StopSignGenerator(db).CreateStopSign(stopId);
            return stopSign;
        }

        public StopSign GetStopSignByStopCode(int stopCode)
        {
            var db = new GtfsDatabaseRetriever();
            StopSign stopSign = new StopSignGenerator(db).CreateStopSignByStopCode(stopCode);
            return stopSign;
        }
    }
}
