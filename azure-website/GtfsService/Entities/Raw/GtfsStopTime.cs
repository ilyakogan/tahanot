using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace GtfsBackend.Entities.Raw
{
    public class GtfsStopTime : BaseGtfsEntity<string>
    {
        [XmlIgnore]
        public string TripId;
        [XmlIgnore]
        public int StopSequence;

        public string Key { get { return TripId + "+" + StopSequence; } }
        
        [XmlIgnore]
        public TimeSpan DepartureTimeOfDay;
        [XmlAttribute]
        public string Time
        {
            get { return Math.Floor(DepartureTimeOfDay.TotalHours) + ":" + DepartureTimeOfDay.ToString("mm"); }
            set { DepartureTimeOfDay = new TimeSpan(int.Parse(value.Substring(0,2)), int.Parse(value.Substring(3,2)), 0); }
        }

        [XmlAttribute]
        public int StopId;
        
        [XmlIgnore]
        public bool IsPickup;
        [XmlIgnore]
        public bool IsDropoff;
        [XmlAttribute]
        public string Type
        {
            get
            {
                if (IsPickup) return (IsDropoff ? "PD" : "P");
                return (IsDropoff ? "D" : "0");
            }
        }

        public override string GetId()
        {
            return TripId + "+" + StopSequence;
        }
    }
}
