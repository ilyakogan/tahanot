using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GtfsBackend.Entities.Raw
{
    public class GtfsRoute : BaseGtfsEntity<int>
    {
        public int Id;
        public string ShortName;
        public string LongName;

        public override int GetId()
        {
            return Id;
        }
    }
}
