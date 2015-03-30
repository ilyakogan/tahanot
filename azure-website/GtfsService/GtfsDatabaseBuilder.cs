using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.IO.Compression;
using GtfsBackend.GtfsFiles;
using GtfsBackend.Entities.Raw;
using System.Xml.Serialization;
using GtfsBackend.Entities;
using System.Threading.Tasks;
using System.Diagnostics;
using System.Threading;

namespace GtfsBackend
{
    public class GtfsDatabaseBuilder
    {
        GtfsDownloader mDownloader = new GtfsDownloader();
        int totalStops;
        int stopsUploaded;
        Semaphore semaphore;
        Stopwatch sw;
        string version;

        public GtfsDatabaseBuilder()
        {
            this.version = DateTime.UtcNow.ToString("yyyyMMdd-HHmmss");
        }

        public void CreateDatabaseWithAllFiles(string motUrl)
        {
            StopsFile stopsFile;
            StopTimesFile stopTimesFile;
            RoutesFile routesFile;
            TripsFile tripsFile;
            UpdateProgress(1);
            DownloadAndParseFiles(motUrl, out stopsFile, out stopTimesFile, out routesFile, out tripsFile);

            totalStops = stopsFile.Count();
            stopsUploaded = 0;
            sw = Stopwatch.StartNew();

            Parallel.ForEach(stopTimesFile.StopTimesByStop, new ParallelOptions { MaxDegreeOfParallelism = 5 }, timesForStop =>
            {
                Stop stop = new StopCreator().CreateStopWithFullInfo(timesForStop.Key, timesForStop.Value, stopTimesFile, tripsFile, routesFile, stopsFile);
                UploadStop(stop);
            });

            new GtfsVersionManager().SetCurrentVersion(version);
        }

        private void DownloadAndParseFiles(string motUrl, out StopsFile stopsFile, out StopTimesFile stopTimesFile, out RoutesFile routesFile, out TripsFile tripsFile)
        {
            byte[] zipFileBytes = mDownloader.FetchAndUploadZipFile(motUrl, version, p => UpdateProgress((int)(p * 0.1)));
            UpdateProgress(10);
            byte[] routesFileBytes = mDownloader.Unzip(zipFileBytes, "routes.txt");
            UpdateProgress(11);
            byte[] stopsFileBytes = mDownloader.Unzip(zipFileBytes, "stops.txt");
            UpdateProgress(12);
            byte[] tripsFileBytes = mDownloader.Unzip(zipFileBytes, "trips.txt");
            UpdateProgress(13);
            byte[] stopTimesFileBytes = mDownloader.Unzip(zipFileBytes, "stop_times.txt");
            UpdateProgress(14);

            stopsFile = new StopsFile(stopsFileBytes);
            stopTimesFile = new StopTimesFile(stopTimesFileBytes);
            routesFile = new RoutesFile(routesFileBytes);
            tripsFile = new TripsFile(tripsFileBytes);
        }

        private void UploadStop(Stop stop)
        {
            mDownloader.ZipAndUploadStop(Serialization.Serialize(stop), stop.RawStop.Id, version, OnStopUploadDone);
        }

        public void OnStopUploadDone()
        {
            int stopNum = Interlocked.Increment(ref stopsUploaded);
            if (stopNum % 100 == 0)
            {
                Trace.TraceInformation("Uploaded " + stopNum + " out of " + totalStops + " stops in time " + sw.Elapsed);
                UpdateProgress((int)(15 + ((float)stopNum / totalStops) * 85));
            }
        }

        private void UpdateProgress(int progress)
        {
            ThreadPool.QueueUserWorkItem(x => new GtfsVersionManager().UpdateProgress(version, progress));
        }

        //private IEnumerable<Stop> CreateStopsWithFullInfo(StopTimesFile stopTimesFile, TripsFile tripsFile, RoutesFile routesFile, StopsFile stopsFile)
        //{
        //    foreach (var timesForStop in stopTimesFile.StopTimesByStop)
        //    {
        //        int stopId = timesForStop.Key;
        //        var stopTimes = timesForStop.Value;
        //        Stop stop = new StopCreator().CreateStopWithFullInfo(stopId, stopTimes, stopTimesFile, tripsFile, routesFile, stopsFile);
        //        if (stop != null) yield return stop;
        //    }
        //}
    }    
}
