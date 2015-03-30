using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using SiriBackend.IsrService;
using System.Web.Mvc;
using SiriBackend;
using System.IO;
using System.Xml.Serialization;
using Segmentio;
using Segmentio.Model;
using SiriWebRole.Filters;

namespace SiriWebRole.Controllers.Api
{
    public class SiriController : Controller
    {
        [Compress]
        public ActionResult StopMonitoringExtendedJson([Label] int monitoringRef, int ver)
        {
            StopMonitoringExtendedInfo stopInfo = new StopMonitoring().GetStopInfoExtended(monitoringRef);
            
            return Json(stopInfo, JsonRequestBehavior.AllowGet);
        }

        public ActionResult MultipleStopMonitoringExtendedJson([Label] string monitoringRefs, int ver)
        {
            var stopCodes = monitoringRefs
                .Split(',')
                .Select(x => Convert.ToInt32(x))
                .Where(x => x != 0)
                .Distinct();

            MultipleStopMonitoringExtendedInfo stopInfo = new StopMonitoring().GetMultipleStopInfoExtended(stopCodes);

            return Json(stopInfo, JsonRequestBehavior.AllowGet);
        }
    }
}
