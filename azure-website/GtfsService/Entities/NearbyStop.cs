using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities.Raw;

namespace GtfsBackend.Entities
{
    public class NearbyStop
    {
        public GtfsStop Stop;

        /// <summary>
        /// Distance from point of interest in meters
        /// </summary>
        public double DistanceFromPoiMeters;

        public NearbyStop(GtfsStop rawStop, double distanceFromPoiMeters)
        {
            Stop = rawStop;
            DistanceFromPoiMeters = distanceFromPoiMeters;
        }
    }
}
