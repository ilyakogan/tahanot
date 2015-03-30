using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GtfsBackend.Entities.Raw
{
    public abstract class BaseGtfsEntity<TId>
    {
        abstract public TId GetId();
    }
}
