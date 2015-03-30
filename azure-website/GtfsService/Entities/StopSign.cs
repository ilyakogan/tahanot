using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities.Raw;

namespace GtfsBackend.Entities
{
    public class StopSign
    {
        public GtfsStop RawStop;
        public List<StopSignRoute> Routes { get; set; }

        public StopSign()
        {

        }

        public StopSign(GtfsStop rawStop)
        {
            this.RawStop = rawStop;
        }
        
    }
}
