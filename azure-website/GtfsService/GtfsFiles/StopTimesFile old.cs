//using System;
//using System.Collections.Generic;
//using System.Linq;
//using System.Text;
//using GtfsBackend.Entities.Raw;
//using GtfsBackend.Entities;
//using System.IO;

//namespace GtfsBackend.GtfsFiles
//{
//    class StopTimesFile : BaseGtfsFile<string, GtfsStopTime>
//    {
//        public StopTimesFile(byte[] fileBytes) : base(fileBytes) { }

//        protected override GtfsStopTime CreateItem(string[] splitLine)
//        {
//            TimeSpan dep;
//            string depStr = splitLine[2];
//            if (depStr[0] == '2' && depStr[1] >= '4')
//            {
//                dep = new TimeSpan(int.Parse(depStr.Substring(0, 2)) - 24, int.Parse(depStr.Substring(3, 2)), int.Parse(depStr.Substring(6, 2)));
//            }
//            else
//            {
//                dep = new TimeSpan(int.Parse(depStr.Substring(0, 2)), int.Parse(depStr.Substring(3, 2)), int.Parse(depStr.Substring(6, 2)));
//            }
//            int stopId = int.Parse(splitLine[3]);
//            int stopSequence = int.Parse(splitLine[4]);
//            string tripId = splitLine[0];

//            GtfsStopTime stopTime = new GtfsStopTime
//            {
//                TripId = tripId,
//                DepartureTimeOfDay = dep,
//                StopId = stopId,
//                StopSequence = stopSequence,
//                IsPickup = (splitLine[6] == "0"), // 0 - yes, 1 - no
//                IsDropoff = (splitLine[7] == "0") // 0 - yes, 1 - no
//            };

//            return stopTime;
//        }
//    }
//}
