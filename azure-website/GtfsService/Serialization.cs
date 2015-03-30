using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Xml.Serialization;

namespace GtfsBackend
{
    class Serialization
    {
        static public T Deserialize<T>(byte[] bytes)
        {
            using (MemoryStream stream = new MemoryStream(bytes))
            {
                return (T)new XmlSerializer(typeof(T)).Deserialize(stream);
            }
        }

        static public byte[] Serialize(object obj)
        {
            using (MemoryStream stream = new MemoryStream())
            {
                new XmlSerializer(obj.GetType()).Serialize(stream, obj);
                return stream.ToArray();
            }
        }
    }
}
