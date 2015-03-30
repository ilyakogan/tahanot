using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.IO.Compression;

namespace GtfsService
{
    class GtfsDatabase
    {
        GtfsDownloader mDownloader = new GtfsDownloader();

        public RoutesFile GetRoutes()
        {
            using (Stream routesFile = mDownloader.GetFile("routes.txt"))
            {
                routesFile.Position = 0;
                return new RoutesFile(new StreamReader(routesFile));
            }
        }
    }
}
