using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SiriClientConsole.BusStopServiceBinding;

namespace SiriClientConsole
{
    class Program
    {
        static void Main(string[] args)
        {
            UseServiceReference();
        }

        private static void UseServiceReference()
        {
            BusStopPortTypeClient client = new BusStopPortTypeClient();
            //client.Open();

            //BusStopOperationRequest request = new BusStopOperationRequest
            //{
            //    request = new BusStopRequestStructure
            //    {
            //        user = REQUESTOR_REF,
            //        requestId = Guid.NewGuid().ToString(),
            //        lineNumber = "42",
            //        Items = new object[] { "36222" }
            //    }
            //};

            //BusStopOperationResponse response = client.BusStopOperation(request);

        }
    }
}
