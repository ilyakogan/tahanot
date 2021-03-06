﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SiriBackend.IsrService;
using GtfsBackend;

namespace SiriBackend
{
    public class StopMonitoringExtendedInfo : IInfoAboutStop
    {
        public StopMonitoringExtendedInfo()
        {
            StopVisits = new List<StopVisit>();
        }

        public int MotiroringRef { get; set; }
        public string Error { get; set; }
        public List<StopVisit> StopVisits { get; set; }
        public System.DateTime ResponseTimestamp { get; set; }
    }
}
