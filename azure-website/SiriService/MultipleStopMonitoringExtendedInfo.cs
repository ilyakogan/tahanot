using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SiriBackend;

namespace SiriBackend
{
    public class MultipleStopMonitoringExtendedInfo
    {
        public MultipleStopMonitoringExtendedInfo()
        {
            Stops = new List<InfoAboutStop>();
        }

        public List<InfoAboutStop> Stops { get; set; }
        public string Error { get; set; }
        public System.DateTime ResponseTimestamp { get; set; }
    }
}
