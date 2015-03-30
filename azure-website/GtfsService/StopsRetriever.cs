using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities;
using GtfsBackend.Entities.Raw;
using System.Threading.Tasks;
using System.Collections.Concurrent;

namespace GtfsBackend
{
    class StopsRetriever
    {
        private GtfsDatabaseRetriever db;

        public StopsRetriever(GtfsDatabaseRetriever db)
        {
            this.db = db;
        }

        public List<NearbyStop> FindNStopsAroundPoi(double poiLat, double poiLon, int n)
        {
            IEnumerable<GtfsStop> rawStops = GetDb().GetStops().GetItems();

            var nFirstStops = rawStops.Take(n);
            List<NearbyStop> runningListOfClosestStops = CreateStopsWithProximity(nFirstStops, poiLat, poiLon);
            foreach (GtfsStop newStop in rawStops.Skip(n))
            {
                double distanceFromPoi = GetDisatnceFromPoiMeters(poiLat, poiLon, newStop.Latitude, newStop.Longitude);
                NearbyStop mostDistantStop = GetMostDistantStop(runningListOfClosestStops);
                
                if (distanceFromPoi < mostDistantStop.DistanceFromPoiMeters)
                {
                    runningListOfClosestStops.Remove(mostDistantStop);
                    runningListOfClosestStops.Add(new NearbyStop(newStop, distanceFromPoi));
                }
            }

            return runningListOfClosestStops.OrderBy(s => s.DistanceFromPoiMeters).ToList();
        }

        public NearbyStopsResponse FindNStopSignsAroundPoi(double poiLat, double poiLon, int n)
        {
            List<NearbyStop> stops = FindNStopsAroundPoi(poiLat, poiLon, n);
            var stopSigns = new ConcurrentDictionary<NearbyStop, StopSign>();

            Parallel.ForEach<NearbyStop>(
                stops,
                stop => stopSigns[stop] = new StopSignGenerator(GetDb()).CreateStopSign(stop.Stop.Id));

            Dictionary<double, StopSign> StopsByDistance = stops.ToDictionary(s => s.DistanceFromPoiMeters, s => stopSigns[s]);
            return new NearbyStopsResponse(StopsByDistance);
        }

        private static NearbyStop GetMostDistantStop(List<NearbyStop> runningListOfClosestStops)
        {
            return runningListOfClosestStops
                                .First(s1 => s1.DistanceFromPoiMeters == runningListOfClosestStops.Max(s2 => s2.DistanceFromPoiMeters));
        }

        private static NearbyStop FindFirstMoreDistantStop(List<NearbyStop> runningListOfClosestStops, double distanceFromPoi)
        {
            NearbyStop stopToRemove = null;

            foreach (NearbyStop stop in runningListOfClosestStops)
            {
                if (distanceFromPoi < stop.DistanceFromPoiMeters)
                {
                    stopToRemove = stop;
                    break;
                }
            }
            return stopToRemove;
        }

        private List<NearbyStop> CreateStopsWithProximity(IEnumerable<GtfsStop> rawStops, double poiLat, double poiLon)
        {
            List<NearbyStop> firstNStops =
                rawStops
                .Select(s => new NearbyStop(s, GetDisatnceFromPoiMeters(poiLat, poiLon, s.Latitude, s.Longitude))).ToList();
            return firstNStops;
        }

        private double GetDisatnceFromPoiMeters(double lat1, double lon1, double lat2, double lon2)
        {
            // Earth radius in meters
            const int R = 6371 * 1000;
            const double DEG_TO_RAD = Math.PI / 180.0;

            double dLat = (lat2 - lat1) * DEG_TO_RAD;
            double dLon = (lon2 - lon1) * DEG_TO_RAD;

            double a = Math.Sin(dLat / 2) * Math.Sin(dLat / 2) +
                    Math.Sin(dLon / 2) * Math.Sin(dLon / 2) * Math.Cos(lat1 * DEG_TO_RAD) * Math.Cos(lat2 * DEG_TO_RAD);
            double distanceInRad = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            return distanceInRad * R;
        }

        private GtfsDatabaseRetriever GetDb()
        {
            lock (this)
            {
                if (db != null) return db;
                return db = new GtfsDatabaseRetriever();
            }
        }
    }
}
