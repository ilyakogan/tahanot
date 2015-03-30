using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities.Raw;
using System.IO;

namespace GtfsBackend.GtfsFiles
{
    class StopTimesFile
    {
        public Dictionary<int, List<GtfsStopTime>> StopTimesByStop = new Dictionary<int, List<GtfsStopTime>>();
        public Dictionary<string, SortedList<int, GtfsStopTime>> OrderedStopTimesByTrip = new Dictionary<string, SortedList<int, GtfsStopTime>>();

        public StopTimesFile(byte[] fileBytes)
        {
            int printEvery = fileBytes.Length / 10;
            int printWhen = printEvery;
            
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
                    AddItemToCollections(splitLine);

                    if (stream.Position >= printWhen)
                    {
                        Console.WriteLine("Read " + Math.Round(((double)printWhen / fileBytes.Length) * 100) + "% of stop times");
                        printWhen += printEvery;
                    }
                }
            }
        }

        private void AddItemToCollections(string[] splitLine)
        {
            TimeSpan dep;
            string depStr = splitLine[2];
            if (depStr[0] == '2' && depStr[1] >= '4')
            {
                dep = new TimeSpan(int.Parse(depStr.Substring(0, 2)) - 24, int.Parse(depStr.Substring(3, 2)), int.Parse(depStr.Substring(6, 2)));
            }
            else
            {
                dep = new TimeSpan(int.Parse(depStr.Substring(0, 2)), int.Parse(depStr.Substring(3, 2)), int.Parse(depStr.Substring(6, 2)));
            }
            int stopId = int.Parse(splitLine[3]);
            int stopSequence = int.Parse(splitLine[4]);
            string tripId = splitLine[0];

            GtfsStopTime stopTime = new GtfsStopTime
            {
                TripId = tripId,
                DepartureTimeOfDay = dep,
                StopId = stopId,
                StopSequence = stopSequence,
                IsPickup = (splitLine[6] == "0"), // 0 - yes, 1 - no
                IsDropoff = (splitLine[7] == "0") // 0 - yes, 1 - no
            };

            if (!StopTimesByStop.ContainsKey(stopId))
            {
                StopTimesByStop[stopId] = new List<GtfsStopTime>();
            }
            StopTimesByStop[stopId].Add(stopTime);

            if (!OrderedStopTimesByTrip.ContainsKey(tripId))
            {
                OrderedStopTimesByTrip[tripId] = new SortedList<int, GtfsStopTime>();
            }
            OrderedStopTimesByTrip[tripId].Add(stopTime.StopSequence, stopTime);
        }

        //public IEnumerable<KeyValuePair<int, List<GtfsStopTime>>> EnumerateTimesByStop()
        //{
        //    foreach (var pair in timesByStop)
        //    {
        //        yield return pair;
        //    }
        //}

        //public IEnumerable<KeyValuePair<string, List<GtfsStopTime>>> EnumerateTimesByTrip()
        //{
        //    foreach (var pair in timesByTrip)
        //    {
        //        yield return pair;
        //    }
        //}
    }
}
