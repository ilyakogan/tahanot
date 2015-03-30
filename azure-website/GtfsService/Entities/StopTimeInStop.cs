using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities.Raw;

namespace GtfsBackend.Entities
{
    public class StopTimeInStop
    {
        public GtfsStopTime StopTime;
        public Trip Trip;
    }
}
