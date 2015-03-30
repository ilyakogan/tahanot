using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace GtfsBackend.Entities
{
    public class Trip
    {
        [XmlAttribute]
        public string TimeAtFirstStop;

        public Trip()
        {

        }

        public Trip(TimeSpan timeAtFirstStop)
        {
            TimeAtFirstStop = 
                Math.Floor(timeAtFirstStop.TotalHours).ToString("00") + ":" + 
                timeAtFirstStop.Minutes.ToString("00") + ":" + 
                timeAtFirstStop.Seconds.ToString("00");
        }
    }
}
