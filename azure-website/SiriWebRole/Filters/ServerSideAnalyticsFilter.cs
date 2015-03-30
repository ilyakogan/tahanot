using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Segmentio;
using Segmentio.Model;
using System.Collections.Specialized;
using System.Globalization;

namespace SiriWebRole.Filters
{
    public class ServerSideAnalyticsFilter : IActionFilter
    {
        public void OnActionExecuted(ActionExecutedContext filterContext)
        {
            
        }

        public void OnActionExecuting(ActionExecutingContext filterContext)
        {
            var queryString = filterContext.HttpContext.Request.QueryString;
            string installationId = GetValueOrNull(queryString, "installationId");
            string installationIdCreatedAt = GetValueOrNull(queryString, "installationIdCreatedAt");
            string deviceId = GetValueOrNull(queryString, "deviceId");
            string widgetId = GetValueOrNull(queryString, "widgetId");
            string deviceName = GetValueOrNull(queryString, "deviceName");
            string appVersion = GetValueOrNull(queryString, "appVersion");
            string actionName = filterContext.ActionDescriptor.ActionName;

            string userId = installationId ?? "Unidentified user";
            DateTime createdAtDate;
            DateTime.TryParseExact(installationIdCreatedAt, "yyyy-MM-dd-HH-mm-ss", CultureInfo.InvariantCulture, DateTimeStyles.None, out createdAtDate);

            var labelParameter = filterContext.ActionDescriptor
                .GetParameters()
                .FirstOrDefault(param => param.GetCustomAttributes(typeof(LabelAttribute), false).Any());
            string label = labelParameter != null ? GetValueOrNull(queryString, labelParameter.ParameterName) : null;

            Analytics.Client.Identify(userId, new Traits() {
                { "created", createdAtDate },
                { "deviceId", deviceId },
                { "deviceName", deviceName }
            });

            Analytics.Client.Track(userId, actionName, new Properties() {
                { "Category", "Request" },    
                { "label", label },
                { "appVersion", appVersion },
                { "widgetId", widgetId },
            });
        }

        private string GetValueOrNull(NameValueCollection queryString, string key)
        {
            Dictionary<string, object> queryParams = new Dictionary<string,object>();
            queryString.CopyTo(queryParams);
            object value;
            queryParams.TryGetValue(key, out value);
            return value != null ? value.ToString() : null;
        }
    }
}