using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.WindowsAzure.Storage.Blob;
using AzureFramework;
using System.Diagnostics;

namespace GtfsBackend
{
    class GtfsVersionManager
    {
        public string GetCurrentVersion()
        {
            return "3";

            /*

            try
            {
                CloudBlobContainer container = BlobUtils.GetContainer("gtfs-current-version");
                CloudBlockBlob blob = container.GetBlockBlobReference("version");
                byte[] bytes = BlobUtils.Download(blob);
                if (bytes.Length == 0) throw new Exception("Could not get current version of GTFS files: version file is empty");
                return Encoding.ASCII.GetString(bytes);
            }
            catch (Exception ex)
            {
                throw new Exception("Could not get current version of GTFS files", ex);
            }
             */
        }

        internal void SetCurrentVersion(string version)
        {
            try
            {
                CloudBlobContainer container = BlobUtils.GetContainer("gtfs-current-version");
                CloudBlockBlob blob = container.GetBlockBlobReference("version");
                BlobUtils.Upload(blob, Encoding.ASCII.GetBytes(version), null, null);
            }
            catch (Exception ex)
            {
                throw new Exception("Could not set current version of GTFS files", ex);
            }
        }

        internal void UpdateProgress(string version, int progress)
        {
            try
            {
                CloudBlobContainer container = BlobUtils.GetContainer("gtfs-current-version");
                CloudBlockBlob blob = container.GetBlockBlobReference("progress-" + version);
                BlobUtils.Upload(blob, Encoding.ASCII.GetBytes(progress.ToString()), null, null);
            }
            catch (Exception ex)
            {
                Trace.TraceWarning("Error updating GTFS upload progress");
            }
        }
    }
}
