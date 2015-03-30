using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;
using System.IO.Compression;
using Ionic.Zip;
using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Auth;
using Microsoft.WindowsAzure.Storage.Blob;
using Microsoft.WindowsAzure;
using System.Configuration;
using AzureFramework;
using GtfsBackend.GtfsFiles;
using GtfsBackend.Entities.Raw;
using System.Xml.Serialization;
using Microsoft.WindowsAzure.ServiceRuntime;
using System.Threading.Tasks;
using System.Diagnostics;
using System.Threading;

namespace GtfsBackend
{
    class GtfsDownloader
    {
        //Uri MOT_URI = new Uri("ftp://gtfs.mot.gov.il/israel-public-transportation.zip");
        string ZIP_BLOB_NAME = "gtfs.zip";
        //public static bool HACK = false;
        
        CloudBlobContainer container;

        public byte[] UnzipAndUploadFile(byte[] zipFile, string fileName, string version, Action<string> callbackWhenDone)
        {
            CloudBlockBlob blob = GetBlob(fileName, version);            
            byte[] extracted = Unzip(zipFile, fileName);
            BlobUtils.Upload(blob, extracted, fileName, (IAsyncResult res) => callbackWhenDone((string)res.AsyncState));
            return extracted;
        } 

        public void ZipAndUploadStop(byte[] stopBytes, int stopId, string version, Action callbackWhenDone)
        {
            byte[] zipBytes = Zip(stopBytes, stopId + ".xml");
            CloudBlockBlob blob = GetBlob(GenerateStopFilename(stopId), version);
            BlobUtils.Upload(blob, zipBytes, stopId, (IAsyncResult res) => callbackWhenDone());
        }

        private static string GenerateStopFilename(int stopId)
        {
            return "stops/" + stopId + ".zip";
        }

        public byte[] GetFile(string blobName, string fileNameInsideZip, string version)
        {
            CloudBlockBlob blob = GetBlob(blobName, version);
            if (BlobUtils.ExistsAndNotEmpty(blob))
            {
                byte[] zipBytes = BlobUtils.Download(blob);
                return Unzip(zipBytes, fileNameInsideZip);
            }
            else
            {
                throw new Exception(blob + " does not exist");
            }
        }

        internal byte[] GetStopFile(int stopId, string version)
        {
            string filename = GenerateStopFilename(stopId);
            byte[] bytes = GetFile(filename, stopId + ".xml", version);
            return bytes;
        }

        public byte[] FetchAndUploadZipFile(string motUrl, string version, Action<int> OnProgressChanged)
        {
            CloudBlockBlob zipBlob = GetBlob(ZIP_BLOB_NAME, version);
            //if (!HACK)
            //{
            return DownloadFromMot(motUrl, zipBlob, OnProgressChanged);
            //}
            //else
            //{
            //    return BlobUtils.Download(zipBlob);
            //}
        }

        private byte[] DownloadFromMot(string motUrl, CloudBlockBlob zipBlob, Action<int> OnProgressChanged)
        {
            using (WebClient webClient = new WebClient())
            {
                Trace.TraceInformation("Downloading from " + motUrl);
                ManualResetEvent mre = new ManualResetEvent(false);
                byte[] fileData = null;

                webClient.DownloadProgressChanged += (s, e) =>
                {
                    OnProgressChanged((int)(e.ProgressPercentage * 0.5));
                };
                webClient.DownloadDataCompleted += (s, e) =>
                {
                    fileData = e.Result;
                    OnProgressChanged(50);
                    BlobUtils.Upload(zipBlob, e.Result, null, (IAsyncResult res) => mre.Set());
                };

                webClient.DownloadDataAsync(new Uri(motUrl));
                mre.WaitOne();
                OnProgressChanged(100);
                return fileData;
            }
        }

        private static byte[] Zip(byte[] stopBytes, string filename)
        {
            using (ZipFile zip = new ZipFile())
            using (MemoryStream zipStream = new MemoryStream())
            {
                zip.AddEntry(filename, stopBytes);
                zip.Save(zipStream);
                return zipStream.ToArray();
            }
        }

        public byte[] Unzip(byte[] zipFile, string fileNameInZip)
        {
            string extractedPath;
            //if (!HACK)
            //{
            LocalResource localResource = RoleEnvironment.GetLocalResource("ZipLocalStorage");
            extractedPath = Path.Combine(localResource.RootPath, "Unzipped_" + Guid.NewGuid().ToString());
            //}
            //else
            //{
            //    extractedPath = @"c:\temp\extracted" + Guid.NewGuid();
            //}

            try
            {
                using (ZipFile zip = ZipFile.Read(new MemoryStream(zipFile)))
                {
                    ZipEntry file = zip[fileNameInZip];

                    using (FileStream extractedStream = new FileStream(extractedPath, FileMode.Create))
                    {
                        Trace.TraceInformation("Decompressing file " + fileNameInZip);
                        file.Extract(extractedStream);
                        extractedStream.Close();
                    }
                }

                byte[] extracted = File.ReadAllBytes(extractedPath);
                return extracted;
            }
            catch (Exception ex)
            {
                Trace.TraceError("Error unzipping file " + fileNameInZip + ": " + ex);
                throw new Exception("Error unzipping file " + fileNameInZip, ex);
            }
            finally
            {
                if (File.Exists(extractedPath))
                {
                    File.Delete(extractedPath);
                }
            }
        }

        private CloudBlockBlob GetBlob(string blobName, string version)
        {
            CloudBlobContainer container = GetGtfsBlobContainer(version);
            CloudBlockBlob blob = container.GetBlockBlobReference(blobName);
            return blob;
        }

        private CloudBlobContainer GetGtfsBlobContainer(string version)
        {
            lock (this)
            {
                if (container != null) return container;
                return BlobUtils.GetContainer("gtfs-" + version);
            }
        }
    }
}
