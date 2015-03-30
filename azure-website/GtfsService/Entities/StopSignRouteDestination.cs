using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace GtfsBackend.Entities
{
    public class StopSignRouteDestination
    {
        [XmlAttribute]
        public string Text1;

        [XmlAttribute]
        public string Text2;

        [XmlIgnore]
        public int StopId;
    }
}
