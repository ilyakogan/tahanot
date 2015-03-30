class Stop < ActiveRecord::Base
	include HasAddress
  self.primary_key = "stop_code"
  has_many :stop_times, primary_key: "stop_id"
	has_many :trips, through: :stop_times

  after_find { |stop| stop.fill_address }

  def self.find_last_in_trip(trip_id)
    joins(:trips).where(stop_times: {trip_id: trip_id}).order("stop_sequence").last
  end

  def find_closest(max_dist_meters)
    d_lon = max_dist_meters / 111320.0 / Math.cos(self.stop_lat / 180 * Math::PI)
    d_lat = max_dist_meters / 110540.0
    from_lon, to_lon = self.stop_lon - d_lon, self.stop_lon + d_lon
    from_lat, to_lat = self.stop_lat - d_lat, self.stop_lat + d_lat
    Stop.where(stop_lon: Float(from_lon)..Float(to_lon), 
               stop_lat: Float(from_lat)..Float(to_lat)).where.not(stop_id: self.stop_id)
  end
end