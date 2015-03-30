using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using GtfsBackend;
using GtfsBackend.Entities;
using System.IO;
using System.Xml.Serialization;
using System.Text;
using System.IO.Compression;
using Segmentio;
using Segmentio.Model;
using SiriWebRole.Filters;
using System.Threading;

namespace SiriWebRole.Controllers.Api
{
    [Compress]
    public class GtfsController : Controller
    {
        public ActionResult NearbyStopsJson(double lat, double lon, int maxStops, int ver)
        {
            List<NearbyStop> stopInfo = new GtfsFacade().GetClosestStopsAroundPoi(lat, lon, maxStops);
            return Json(stopInfo, JsonRequestBehavior.AllowGet);
        }

        public ActionResult NearbyStopSignsJson(double lat, double lon, int maxStops, int ver)
        {
            NearbyStopsResponse stopInfo = new GtfsFacade().GetClosestStopSignsAroundPoi(lat, lon, maxStops);
            return Json(stopInfo, JsonRequestBehavior.AllowGet);
        }

        public ActionResult NearbyStopSignsXml(double lat, double lon, int maxStops, int? ignoreFirstStops, int ver)
        {
            NearbyStopsResponse stopInfo = new GtfsFacade().GetClosestStopSignsAroundPoi(lat, lon, maxStops);
            if (ignoreFirstStops != null)
            {
                stopInfo.RemoveFirstStops(ignoreFirstStops.Value);
            }
            return XmlContent(stopInfo, true);
        }

        public ActionResult StopSignXml([Label] int stopId, int ver)
        {
            StopSign stopSign = new GtfsFacade().GetStopSignById(stopId);
            return XmlContent(stopSign, false);
        }

        public ActionResult StopSignByStopCodeXml([Label] int stopCode, int ver)
        {
            StopSign stopSign = new GtfsFacade().GetStopSignByStopCode(stopCode);
            return XmlContent(stopSign, false);
        }

        [HttpPost]
        public ActionResult UpdateGtfs(string sourceUrl = "ftp://gtfs.mot.gov.il/israel-public-transportation.zip")
        {
            new GtfsDatabaseBuilder().CreateDatabaseWithAllFiles(sourceUrl);
            return Json(true);
        }

        private ActionResult XmlContent<T>(T stopInfo, bool compress)
        {
            MemoryStream xmlStream = new MemoryStream();
            new XmlSerializer(typeof(T)).Serialize(xmlStream, stopInfo);
            xmlStream.Position = 0;
            string xml = new StreamReader(xmlStream).ReadToEnd();

            if (compress)
            {
                string base64 = CompressToBase64(xml);
                return Content(base64, "text/plain");
            }
            else
            {
                return Content(xml, "text/xml");
            }
        }

        private static string CompressToBase64(string text)
        {
            byte[] buffer = Encoding.UTF8.GetBytes(text);
            MemoryStream ms = new MemoryStream();
            using (GZipStream zip = new GZipStream(ms, CompressionMode.Compress, true))
            {
                zip.Write(buffer, 0, buffer.Length);
            }

            ms.Position = 0;
            MemoryStream outStream = new MemoryStream();

            byte[] compressed = new byte[ms.Length];
            ms.Read(compressed, 0, compressed.Length);

            byte[] gzBuffer = new byte[compressed.Length + 4];
            System.Buffer.BlockCopy(compressed, 0, gzBuffer, 4, compressed.Length);
            System.Buffer.BlockCopy(BitConverter.GetBytes(buffer.Length), 0, gzBuffer, 0, 4);
            return Convert.ToBase64String(gzBuffer);
        }
    }
}
