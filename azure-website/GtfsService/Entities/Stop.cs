using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities.Raw;
using System.Xml.Serialization;
using System.IO;

namespace GtfsBackend.Entities
{
    public class Stop
    {
        public GtfsStop RawStop;
        public List<Route> Routes;
        public List<StopOnRoute> OtherStopsOnRoutes;
    }
}
