using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SiriClientConsole3.WebReference;
using System.Web.Services.Protocols;
using GtfsService;

namespace SiriClientConsole3
{
    class Program
    {
        static void Main(string[] args)
        {
            //new GtfsFacade().GetRoute(123);
            
            GetStopInfo();

            Console.WriteLine("Done");
            Console.ReadLine();
        }

        private static void GetStopInfo()
        {
            SiriServices services = new SiriServices();

            StopMonitoringRequestStructure stopMonitoringRequest = new StopMonitoringRequestStructure
            {
                version = "IL2.6",
                RequestTimestamp = System.DateTime.Now,
                MessageIdentifier = new MessageQualifierStructure { Value = "2" },
                MonitoringRef = new MonitoringRefStructure { Value = "21644" }
            };

            var items = new AbstractFunctionalServiceRequestStructure[] { stopMonitoringRequest };

            ServiceRequestStructure serviceRequest = new ServiceRequestStructure
            {
                ServiceRequestContext =
                new ServiceRequestContextStructure
                {
                    GetDataAddress = "http://81.218.41.96:8081/SiriServerApp/BusStopService",
                    StatusResponseAddress = "http://46.120.157.223",
                    DataHorizon = "P396DT1H1M1S",
                    RequestTimeout = "P0DT0H0M10S",
                    DeliveryMethod = DeliveryMethodEnumeration.direct,
                    MultipartDespatch = false,
                    ConfirmDelivery = false,
                    AllowedPredictors = PredictorsEnumeration.anyone,
                },
                RequestTimestamp = System.DateTime.Now,
                RequestorRef = new ParticipantRefStructure { Value = REQUESTOR_REF },
                MessageIdentifier = new MessageQualifierStructure { Value = "1" },
                Items = items
            };

            ServiceDeliveryStructure serviceDelivery = services.GetStopMonitoringService(serviceRequest);

            PrintServiceDelivery(serviceDelivery);
        }

        private static void PrintServiceDelivery(ServiceDeliveryStructure serviceDelivery)
        {
            PrintErrorIfNeeded(serviceDelivery.ErrorCondition);
            PrintErrorIfNeeded(serviceDelivery.StopMonitoringDelivery[0].ErrorCondition);

            foreach (var item in serviceDelivery.StopMonitoringDelivery[0].MonitoredStopVisit)
            {
                var journey = item.MonitoredVehicleJourney;
                string lineRef = journey.LineRef.Value;
                Console.WriteLine("Line ref: " + lineRef);

                GtfsFacade gtfsFacade = new GtfsFacade();
                RouteInfo route = gtfsFacade.GetRoute(int.Parse(lineRef));
                if (route != null)
                {
                    Console.WriteLine("Line short name: " + route.RouteShortName);
                    Console.WriteLine("Line long name: " + route.RouteLongName);
                }

                var call = journey.MonitoredCall;
                Console.WriteLine(call.ItemElementName + ": " + call.Item);
                Console.WriteLine(call.Item1ElementName + ": " + call.Item1);
                Console.WriteLine("Vehicle at stop: " + call.VehicleAtStop);
                Console.WriteLine("ArrivalStatus: " + call.ArrivalStatus);
                Console.WriteLine();
            }
        }

        private static void PrintErrorIfNeeded(ServiceDeliveryErrorConditionStructure errorCondition)
        {
            if (errorCondition != null && errorCondition.Description != null)
            {
                Console.WriteLine("Error " + errorCondition.Item.ErrorText + ": " + errorCondition.Description.Value);
            }
        }

        private static void PrintErrorIfNeeded(ServiceDeliveryStructureErrorCondition errorCondition)
        {
            if (errorCondition != null && errorCondition.Description != null)
            {
                Console.WriteLine("Error " + errorCondition.Item.ErrorText + ": " + errorCondition.Description.Value);
            }
        }
    }
}
