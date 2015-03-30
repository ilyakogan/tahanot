using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using GtfsBackend.Entities.Raw;

namespace GtfsBackend.Entities
{
    public class StopOnRoute
    {
        [XmlAttribute]
        public int Id;
        [XmlAttribute]
        public int Code;
        [XmlAttribute]
        public string Name;
        [XmlAttribute("Address")]
        public string StreetAddress;
        [XmlAttribute]
        public string Town;

        public StopOnRoute()
        {

        }

        public StopOnRoute(GtfsStop rawStop)
        {
            Id = rawStop.Id;
            Code = rawStop.Code;
            Name = rawStop.Name;
            StreetAddress = rawStop.StreetAddress;
            Town = rawStop.Town;
        }
    }
}
