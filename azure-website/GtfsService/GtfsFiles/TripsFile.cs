using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities.Raw;

namespace GtfsBackend.GtfsFiles
{
    class TripsFile : BaseGtfsFile<string, GtfsTrip>
    {
        public TripsFile(byte[] fileBytes) : base(fileBytes) { }

        protected override GtfsTrip CreateItem(string[] splitLine)
        {
            string tripId = splitLine[2];
            GtfsTrip routeInfo = new GtfsTrip
            {
                TripId = tripId,
                RouteId = int.Parse(splitLine[0])
            };
            return routeInfo;
        }
    }
}
