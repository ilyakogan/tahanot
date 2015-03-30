using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GtfsBackend.Entities.Raw
{
    public class GtfsTrip : BaseGtfsEntity<string>
    {
        public int RouteId;
        public string TripId;

        public override string GetId()
        {
            return TripId;
        }
    }
}
