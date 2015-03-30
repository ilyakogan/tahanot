﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.WindowsAzure.Diagnostics;
using Microsoft.WindowsAzure;

namespace SiriWebRole
{
    /// <summary>
    /// This class handles diagnostics and stores the logs in the Azure table and blog storage.
    /// Note: Basically all logs are turned on here, this can be expensive and you may want to change several settings here before going live
    /// </summary>
    public class AzureDiagnostics
    {
        //public void ConfigDiagnostics()
        //{
        //    DiagnosticMonitorConfiguration config =
        //        DiagnosticMonitor.GetDefaultInitialConfiguration();
        //    config.ConfigurationChangePollInterval = TimeSpan.FromMinutes(1d);
        //    config.Logs.BufferQuotaInMB = 500;
        //    config.Logs.ScheduledTransferLogLevelFilter = LogLevel.Information;
        //    config.Logs.ScheduledTransferPeriod = TimeSpan.FromMinutes(1d);

        //    DiagnosticMonitor.Start(
        //        "Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString",
        //        config);
        //}

        static string CONNECTION_STRING = "Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString";

        public void EnableAzureDiagnostics()
        {
            //var diagnosticMonitorConfiguration = DiagnosticMonitor.GetDefaultInitialConfiguration();

            //SetDiagnositcManagerScheduledTransferPeriods(diagnosticMonitorConfiguration);

            //AddFullCrashDumps();
            ////AddPerformanceCounterMonitoring(diagnosticMonitorConfiguration);
            //AddEventLoggingFromWindowsEventLog(diagnosticMonitorConfiguration);

            ////diagnosticMonitorConfiguration.OverallQuotaInMB = 1024;
            ////diagnosticMonitorConfiguration.WindowsEventLog.BufferQuotaInMB = 256;

            //StartDiagnosticManager(diagnosticMonitorConfiguration);
        }

        /// <summary>
        /// Sets how often diagnostics data is transferred to the Azure table storage or blob storage
        /// Note: Change to a period that fits your need, commenting out one of these lines disables it
        /// </summary>
        /// <param name="diagnosticMonitorConfiguration"></param>
        void SetDiagnositcManagerScheduledTransferPeriods(DiagnosticMonitorConfiguration diagnosticMonitorConfiguration)
        {
            diagnosticMonitorConfiguration.Directories.ScheduledTransferPeriod = TimeSpan.FromMinutes(1);
            diagnosticMonitorConfiguration.Logs.ScheduledTransferPeriod = TimeSpan.FromMinutes(1);
            diagnosticMonitorConfiguration.WindowsEventLog.ScheduledTransferPeriod = TimeSpan.FromMinutes(1);
            //diagnosticMonitorConfiguration.DiagnosticInfrastructureLogs.ScheduledTransferPeriod = TimeSpan.FromMinutes(1);
            //diagnosticMonitorConfiguration.PerformanceCounters.ScheduledTransferPeriod = TimeSpan.FromMinutes(1);
        }

        /// <summary>
        /// Will add a full crashdump. 
        /// Note: Full crashdumps are not available in asp.net roles
        /// </summary>
        void AddFullCrashDumps()
        {
            CrashDumps.EnableCollection(true);
        }

        /// <summary>
        /// Enables performance counters
        /// Note: PerformanceCounterConfiguration.CounterSpecifier is language specific and depends on your OS language.
        /// Note: For a complete list of possible PerformanceCounterConfiguration.CounterSpecifier values run "typeperf.exe /Q"
        /// </summary>
        /// <param name="diagnosticMonitorConfiguration"></param>
        void AddPerformanceCounterMonitoring(DiagnosticMonitorConfiguration diagnosticMonitorConfiguration)
        {
            var performanceCounterConfiguration =
                new PerformanceCounterConfiguration
                {
                    CounterSpecifier = @"\Processor(*)\% Processor Time",
                    SampleRate = TimeSpan.FromSeconds(15)
                };

            diagnosticMonitorConfiguration.PerformanceCounters.DataSources.Add(performanceCounterConfiguration);
        }

        /// <summary>
        /// By default all Windows events to the Application and System logs are stored in the Azure table storage
        /// Note: Decide here what Windows event logs you are interested in seeing, you can also filter out events
        /// </summary>
        /// <param name="diagnosticMonitorConfiguration"></param>
        void AddEventLoggingFromWindowsEventLog(DiagnosticMonitorConfiguration diagnosticMonitorConfiguration)
        {
            // Syntax: <channel>!XPath Query
            // See: http://msdn.microsoft.com/en-us/library/dd996910(VS.85).aspx
            diagnosticMonitorConfiguration.WindowsEventLog.DataSources.Add("Application!*");
            diagnosticMonitorConfiguration.WindowsEventLog.DataSources.Add("System!*");
        }

        void StartDiagnosticManager(DiagnosticMonitorConfiguration diagnosticMonitorConfiguration)
        {
            DiagnosticMonitor.Start(CONNECTION_STRING, diagnosticMonitorConfiguration);
        }
    }
}