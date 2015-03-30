using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GtfsBackend.Entities.Raw;
using System.IO;
using System.Text.RegularExpressions;

namespace GtfsBackend.GtfsFiles
{
    class StopsFile : BaseGtfsFile<int, GtfsStop>
    {
        public StopsFile(byte[] fileBytes) : base(fileBytes) { }

        protected override GtfsStop CreateItem(string[] splitLine)
        {
            string address = splitLine[3].Replace("כתובת:", "");
            string streetAddress = address;
            string town = null;
            if (address.Contains("  "))
            {
                int doubleSpaceLoc = address.IndexOf("  ");
                streetAddress = address.Substring(0, doubleSpaceLoc).Trim();
                town = address.Substring(doubleSpaceLoc).Trim();
            }
            else
            {
                Match match = Regex.Match(address, @"^(.*\d+) (.*)$");
                if (match.Success && match.Groups.Count == 3)
                {
                    // Groups[0] is the entire string
                    streetAddress = match.Groups[1].Value.Trim();
                    town = match.Groups[2].Value.Trim();
                }
            }

            return new GtfsStop
            {
                Id = int.Parse(splitLine[0]),
                Code = int.Parse(splitLine[1]),
                Name = splitLine[2],
                Address = address,
                StreetAddress = streetAddress,
                Town = town,
                Latitude = double.Parse(splitLine[4]),
                Longitude = double.Parse(splitLine[5])
            };
        }
    }
}
