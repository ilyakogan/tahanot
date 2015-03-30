using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace GtfsService
{
    class RoutesFile
    {
        private Dictionary<int, RouteInfo> routes = new Dictionary<int,RouteInfo>();
        private string routesFile;

        public RoutesFile(StreamReader routesFileReader)
        {
            string fileLine;

            routesFileReader.BaseStream.Position = 0;

            // Header
            routesFileReader.ReadLine();
            
            while ((fileLine = routesFileReader.ReadLine()) != null)
            {
                string[] split = fileLine.Split(',');
                int routeId = int.Parse(split[0]);
                RouteInfo routeInfo = new RouteInfo
                {
                    RouteId = routeId,
                    RouteShortName = split[2],
                    RouteLongName = split[3]
                };
                routes.Add(routeId, routeInfo);
            }
        }

        public RouteInfo GetRoute(int routeId)
        {
            RouteInfo routeInfo;
            if (routes.TryGetValue(routeId, out routeInfo))
            {
                return routeInfo;
            }
            return null;
        }
    }
}
