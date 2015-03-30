using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace GtfsBackend.Entities.Raw
{
    public class GtfsStop : BaseGtfsEntity<int>
    {
        [XmlAttribute]
        public int Id;
        [XmlAttribute]
        public int Code;
        [XmlAttribute]
        public double Latitude;
        [XmlAttribute]
        public double Longitude;

        [XmlAttribute]
        public string Name;
        [XmlAttribute]
        public string Address;
        [XmlAttribute]
        public string StreetAddress;
        [XmlAttribute]
        public string Town;

        public override int GetId()
        {
            return Id;
        }
    }
}
