using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using SiriBackend;
using SiriBackend.IsrService;
using System.Xml.Serialization;
using System.IO;
using Microsoft.WindowsAzure.Diagnostics;
using System.Diagnostics;

namespace SiriWebRole.Controllers
{
    public class HomeController : Controller
    {
        public ActionResult Index()
        {
            ViewBag.Message = "Modify this template to jump-start your ASP.NET MVC application.";

            return View();
        }

        public ActionResult EnableDiagnostics()
        {
            new AzureDiagnostics().EnableAzureDiagnostics();
            return Json(false, JsonRequestBehavior.AllowGet);
        }

        public ActionResult LogSomeErrors()
        {
            Trace.WriteLine("Dolorosa", "Trace write line");
            Trace.TraceError("Dolorosa Tracing an error");
            Trace.TraceInformation("Dolorosa Tracing information");
            EventLog.WriteEntry("Dolorosa", "Entry");
            EventLog.WriteEntry("Dolorosa", "Error", EventLogEntryType.Error, 251, 2, new byte[] { 1, 2, 123 });

            return Json(true, JsonRequestBehavior.AllowGet);
        }        

        //public ActionResult About()
        //{
        //    ViewBag.Message = "Your app description page.";

        //    return View();
        //}

        //public ActionResult Contact()
        //{
        //    ViewBag.Message = "Your contact page.";

        //    return View();
        //}
    }
}
