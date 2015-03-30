using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.WindowsAzure.Storage.Blob;
using System.IO;
using System.Diagnostics;
using Microsoft.WindowsAzure;
using System.Configuration;
using Microsoft.WindowsAzure.Storage;

namespace AzureFramework
{
    public static class BlobUtils
    {
        public static bool HACK = false;

        public static CloudBlobContainer GetContainer(string containerName)
        {
            string setting = CloudConfigurationManager.GetSetting("StorageConnectionString");
            if (setting == null)
            {
                setting = ConfigurationManager.ConnectionStrings["StorageConnectionString"].ConnectionString;
            }
            CloudStorageAccount storageAccount = CloudStorageAccount.Parse(setting);
            CloudBlobClient blobClient = storageAccount.CreateCloudBlobClient();
            CloudBlobContainer container = blobClient.GetContainerReference(containerName);
            container.CreateIfNotExists();
            return container;
        }

        public static byte[] Download(CloudBlockBlob blob)
        {
            // Hack
            if (HACK)
            {
                if (blob.Uri.AbsoluteUri.ToLower().EndsWith("zip"))
                {
                    return File.ReadAllBytes(@"D:\Ilya\HaTahanaHaBaa\gtfs-files\gtfs.zip");
                }
            }

            using (MemoryStream stream = new MemoryStream())
            {
                blob.DownloadToStream(stream);
                return stream.ToArray();
            }
        }

        public static void Upload(CloudBlockBlob blob, byte[] fileData, object state, AsyncCallback callbackWhenDone)
        {
            using (MemoryStream fileStream = new MemoryStream(fileData))
            {
                Trace.TraceInformation("Writing to " + blob.Uri);
                //blob.BeginUploadFromStream(fileStream, callbackWhenDone, state);
                blob.UploadFromStream(fileStream);
                if (callbackWhenDone != null) callbackWhenDone(new FakeAsyncResult { AsyncState = state });
            }
        }

        public static bool ExistsAndNotEmpty(CloudBlockBlob blob)
        {
            if (blob.Exists())
            {
                blob.FetchAttributes();
                if (blob.Properties.Length > 0)
                {
                    return true;
                }
            }
            return false;
        }

        class FakeAsyncResult : IAsyncResult
        {
            public object AsyncState { get; set; }

            public System.Threading.WaitHandle AsyncWaitHandle { get; set; }

            public bool CompletedSynchronously { get; set; }

            public bool IsCompleted { get; set; }
        }
    }
}
