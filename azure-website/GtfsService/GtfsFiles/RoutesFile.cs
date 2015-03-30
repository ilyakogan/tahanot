using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using GtfsBackend.Entities.Raw;

namespace GtfsBackend.GtfsFiles
{
    class RoutesFile : BaseGtfsFile<int, GtfsRoute>
    {
        public RoutesFile(byte[] fileBytes) : base(fileBytes) { }

        protected override GtfsRoute CreateItem(string[] splitLine)
        {
            int routeId = int.Parse(splitLine[0]);
            GtfsRoute routeInfo = new GtfsRoute
            {
                Id = routeId,
                ShortName = splitLine[2],
                LongName = splitLine[3]
            };
            return routeInfo;
        }
    }
}
