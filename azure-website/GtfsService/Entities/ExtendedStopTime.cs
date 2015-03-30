using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities.Raw;

namespace GtfsBackend.Entities
{
    public class ExtendedStopTime
    {
        public GtfsStopTime StopTime;
        public GtfsTrip Trip;
        public GtfsRoute Route;
    }
}
