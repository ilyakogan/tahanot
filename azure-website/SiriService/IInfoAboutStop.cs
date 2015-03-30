using System;
using System.Collections.Generic;
namespace SiriBackend
{
    interface IInfoAboutStop
    {
        string Error { get; set; }
        int MotiroringRef { get; set; }
        List<StopVisit> StopVisits { get; set; }
    }
}
