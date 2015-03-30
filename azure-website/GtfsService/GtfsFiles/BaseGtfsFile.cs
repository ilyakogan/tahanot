using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using GtfsBackend.Entities.Raw;
using System.Diagnostics;

namespace GtfsBackend.GtfsFiles
{
    abstract class BaseGtfsFile<TKey, TItem> where TItem : BaseGtfsEntity<TKey>
    {
        protected Dictionary<TKey, TItem> items = new Dictionary<TKey, TItem>();

        protected BaseGtfsFile(byte[] fileBytes)
        {
            Trace.TraceInformation("Parsing file " + this.GetType() + "...");

            using (MemoryStream stream = new MemoryStream(fileBytes))
            using (var fileReader = new StreamReader(stream))
            {
                string fileLine;
                fileReader.BaseStream.Position = 0;

                // Header
                fileReader.ReadLine();

                while ((fileLine = fileReader.ReadLine()) != null)
                {
                    string[] splitLine = fileLine.Split(',');
                    AddItemToCollection(splitLine);
                }
            }
        }

        protected virtual void AddItemToCollection(string[] splitLine)
        {
            TItem item = CreateItem(splitLine);
            TKey id = item.GetId();
            items.Add(id, item);
        }

        abstract protected TItem CreateItem(string[] splitLine);

        public TItem GetItem(TKey id)
        {
            TItem item;
            if (items.TryGetValue(id, out item))
            {
                return item;
            }
            return null;
        }

        public IEnumerable<TItem> GetItems()
        {
            return items.Values;
        }

        public int Count()
        {
            return items.Count;
        }
    }
}
