using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.GtfsFiles;
using GtfsBackend.Entities;

namespace GtfsBackend
{
    class GtfsDatabaseRetriever
    {
        GtfsDownloader mDownloader = new GtfsDownloader();
        private RoutesFile routesFile;
        private TripsFile tripsFile;
        private StopsFile stopsFile;

        public RoutesFile GetRoutes()
        {
            if (routesFile != null) return routesFile;
            byte[] fileBytes = mDownloader.GetFile("routes.zip", "routes.txt", GetLatestVersion());
            return routesFile = new RoutesFile(fileBytes);
        }

        public StopsFile GetStops()
        {
            if (stopsFile != null) return stopsFile;
            byte[] fileBytes = mDownloader.GetFile("stops.zip", "stops.txt", GetLatestVersion());
            return stopsFile = new StopsFile(fileBytes);
        }

        internal TripsFile GetTrips()
        {
            if (tripsFile != null) return tripsFile;
            byte[] fileBytes = mDownloader.GetFile("trips.zip", "trips.txt", GetLatestVersion());
            return tripsFile = new TripsFile(fileBytes);
        }

        internal Stop GetStop(int stopId)
        {
            byte[] stopBytes = mDownloader.GetStopFile(stopId, GetLatestVersion());
            return Serialization.Deserialize<Stop>(stopBytes);
        }

        private string GetLatestVersion()
        {
            return new GtfsVersionManager().GetCurrentVersion();
        }        
    }
}
