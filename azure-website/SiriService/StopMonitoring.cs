using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SiriBackend.IsrService;
using GtfsBackend;
using GtfsBackend.Entities;
using GtfsBackend.Entities.Raw;
using System.Diagnostics;

namespace SiriBackend
{
    public class StopMonitoring
    {
        public StopMonitoringExtendedInfo GetStopInfoExtended(int monitoringRef)
        {
            ServiceDeliveryStructure serviceDelivery = GetStopInfo(new int[] { monitoringRef });

            return CreateStopExtendedInfo(serviceDelivery);
        }

        public MultipleStopMonitoringExtendedInfo GetMultipleStopInfoExtended(IEnumerable<int> monitoringRefs)
        {
            ServiceDeliveryStructure serviceDelivery = GetStopInfo(monitoringRefs);

            return CreateMiltipleStopExtendedInfo(serviceDelivery);
        }

        private ServiceDeliveryStructure GetStopInfo(IEnumerable<int> monitoringRefs)
        {
            SiriServices services = new SiriServices();

            ServiceRequestStructure serviceRequest = BuildStopMonitoringRequest(monitoringRefs);

            ServiceDeliveryStructure serviceDelivery = services.GetStopMonitoringService(serviceRequest);

            return serviceDelivery;
        }

        private static ServiceRequestStructure BuildStopMonitoringRequest(IEnumerable<int> monitoringRefs)
        {
            IEnumerable<StopMonitoringRequestStructure> stopMonitoringRequests =
                monitoringRefs
                .Select(
                    monitoringRef =>
                    new StopMonitoringRequestStructure
                    {
                        version = "IL2.6",
                        RequestTimestamp = System.DateTime.Now,
                        MessageIdentifier = new MessageQualifierStructure { Value = "2" },
                        MonitoringRef = new MonitoringRefStructure { Value = monitoringRef.ToString() }
                    });

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
                Items = stopMonitoringRequests.Cast<AbstractFunctionalServiceRequestStructure>().ToArray()
            };
            return serviceRequest;
        }

        private StopMonitoringExtendedInfo CreateStopExtendedInfo(ServiceDeliveryStructure serviceDelivery)
        {
            StopMonitoringExtendedInfo stopExtendedInfo;

            if (serviceDelivery.StopMonitoringDelivery != null)
            {
                stopExtendedInfo = ProcessInfoAboutStop<StopMonitoringExtendedInfo>(serviceDelivery.StopMonitoringDelivery[0]);
            }
            else
            {
                stopExtendedInfo = new StopMonitoringExtendedInfo();
            }

            stopExtendedInfo.ResponseTimestamp = serviceDelivery.ResponseTimestamp;

            return stopExtendedInfo;
        }

        private MultipleStopMonitoringExtendedInfo CreateMiltipleStopExtendedInfo(ServiceDeliveryStructure serviceDelivery)
        {
            MultipleStopMonitoringExtendedInfo multipleStopExtendedInfo = new MultipleStopMonitoringExtendedInfo();

            if (serviceDelivery.StopMonitoringDelivery != null)
            {
                foreach (var stopMonitoringDelivery in serviceDelivery.StopMonitoringDelivery)
                {
                    try
                    {
                        InfoAboutStop stop = ProcessInfoAboutStop<InfoAboutStop>(stopMonitoringDelivery);
                        if (stop != null)
                        {
                            multipleStopExtendedInfo.Stops.Add(stop);
                        }
                    }
                    catch (Exception ex)
                    {
                        Trace.TraceError("Error getting stop monitoring for one of the stops: " + ex);
                    }
                }
            }

            multipleStopExtendedInfo.ResponseTimestamp = serviceDelivery.ResponseTimestamp;
            multipleStopExtendedInfo.Error = GetErrorIfNeeded(serviceDelivery.ErrorCondition);

            return multipleStopExtendedInfo;
        }

        private static T ProcessInfoAboutStop<T>(StopMonitoringDeliveryStructure stopMonitoringDelivery) where T : class, IInfoAboutStop, new()
        {
            if (stopMonitoringDelivery.MonitoredStopVisit == null || !stopMonitoringDelivery.MonitoredStopVisit.Any())
            {
                return null;
            }

            T stopExtendedInfo = new T();

            stopExtendedInfo.MotiroringRef = int.Parse(stopMonitoringDelivery.MonitoredStopVisit[0].MonitoringRef.Value);
            stopExtendedInfo.Error = GetErrorIfNeeded(stopMonitoringDelivery.ErrorCondition);
            
            foreach (MonitoredStopVisitStructure item in stopMonitoringDelivery.MonitoredStopVisit)
            {
                StopVisit stopVisit = new StopVisit();

                var journey = item.MonitoredVehicleJourney;
                string lineRef = journey.LineRef.Value;

                stopVisit.LineRef = lineRef;
                stopVisit.PublishedLineName = journey.PublishedLineName.Value;
                stopVisit.DestinationRef = journey.DestinationRef.Value;

                GtfsFacade gtfsFacade = new GtfsFacade();
                GtfsRoute route = gtfsFacade.GetRoute(int.Parse(lineRef));
                if (route != null)
                {
                    stopVisit.RouteShortName = route.ShortName;
                    stopVisit.RouteLongName = route.LongName;
                }
                else
                {
                    // TODO: Error
                }

                var call = journey.MonitoredCall;

                if (call.ItemElementName == ItemChoiceType2.ExpectedArrivalTime)
                {
                    stopVisit.ExpectedArrivalTime = call.Item;
                }
                else if (call.Item1ElementName == Item1ChoiceType1.ExpectedDepartureTime)
                {
                    stopVisit.ExpectedArrivalTime = call.Item1;
                }

                stopExtendedInfo.StopVisits.Add(stopVisit);
            }
            return stopExtendedInfo;
        }

        private static string GetErrorIfNeeded(ServiceDeliveryErrorConditionStructure errorCondition)
        {
            if (errorCondition != null && errorCondition.Description != null)
            {
                return "Error " + errorCondition.Item.ErrorText + ": " + errorCondition.Description.Value;
            }
            return null;
        }

        private static string GetErrorIfNeeded(ServiceDeliveryStructureErrorCondition errorCondition)
        {
            if (errorCondition != null && errorCondition.Description != null)
            {
                return "Error " + errorCondition.Item.ErrorText + ": " + errorCondition.Description.Value;
            }
            return null;
        }


    }
}
