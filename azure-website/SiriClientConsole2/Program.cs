using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SiriClientConsole2.BusStopServiceBinding;

namespace SiriClientConsole2
{
    class Program
    {
        static void Main(string[] args)
        {
            BusStopService service = new BusStopService();

            string serviceRequest =
                @"<?xml version=""1.0"" encoding=""utf-8""?>
                    <ServiceRequest xmlns=""http://www.siri.org.uk/siri"">
                      <ServiceRequestContext>
                        <GetDataAddress>http://81.218.41.96:8081/SiriServerApp/BusStopService?wsdl</GetDataAddress>
                        <StatusResponseAddress>http://46.120.157.223</StatusResponseAddress>
                        <WgsDecimalDegrees></WgsDecimalDegrees>
                        <DataHorizon>P396DT1H1M1S</DataHorizon>
                        <RequestTimeout>P0DT0H0M10S</RequestTimeout>
                        <DeliveryMethod>direct</DeliveryMethod>
                        <MultipartDespatch>false</MultipartDespatch>
                        <ConfirmDelivery>false</ConfirmDelivery>
                        <MaximimumNumberOfSubscriptions>1</MaximimumNumberOfSubscriptions>
                        <AllowedPredictors>anyone</AllowedPredictors>
                        <PredictionFunction>PredictionFunction1</PredictionFunction>
                      </ServiceRequestContext>
                      <RequestTimestamp>" + DateTime.Now.ToString("yyyy-MM-ddTHH:mm:ss") + @"+02:00</RequestTimestamp>
                      <Address>http://81.218.41.96:8081/SiriServerApp/BusStopService?wsdl</Address>
                      <RequestorRef>IK17060</RequestorRef>
                      <MessageIdentifier>20</MessageIdentifier>
                    </ServiceRequest>";

            string stopMonitoringRequest =
                @"<?xml version=""1.0"" encoding=""utf-8""?>
                    <StopMonitoringRequest version=""1.2"" xmlns=""http://www.siri.org.uk/siri"">
                      <RequestTimestamp>" + DateTime.Now.ToString("yyyy-MM-ddTHH:mm:ss") + @"+02:00</RequestTimestamp>
                      <MessageIdentifier>21</MessageIdentifier>
                      <MonitoringRef>21644</MonitoringRef>
                      <StopVisitTypes>all</StopVisitTypes>
                      <StopMonitoringDetailLevel>normal</StopMonitoringDetailLevel>
                    </StopMonitoringRequest>";

            BusStopResponseStructure response = service.BusStopOperation(new BusStopRequestStructure
            {
                lineNumber = "51",
                requestId = "1",
                Items = new object[] { serviceRequest, stopMonitoringRequest },
            });
        }
    }
}
