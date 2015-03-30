using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AzureFramework;
using GtfsBackend;

namespace Tester
{
    class Program
    {
        static void Main(string[] args)
        {
            try
            {
                BlobUtils.HACK = true;
                GtfsDownloader.HACK = true;
                new GtfsFacade().CreateDatabase();
                //var result = new GtfsFacade().GetClosestStopsAroundPoi(32.0961579, 34.8218005, 50);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
            Console.ReadKey();
        }
    }
}
