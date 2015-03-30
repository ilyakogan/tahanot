class StopTime < ActiveRecord::Base
	belongs_to :stop, primary_key: "stop_id"
	belongs_to :trip
	delegate :route, to: :trip

	def self.find_by_service service_ids, stop_code
		joins(:trip, :stop).where(trips: {service_id: service_ids}, stops: {stop_code: stop_code})
	end

	def self.find_service_end_time service_ids, stop_code
		find_by_service(service_ids, stop_code).maximum :departure_time
	end

	def self.find_service_start_time service_ids, stop_code
		find_by_service(service_ids, stop_code).minimum :departure_time
	end

	def self.find_by_stop_code stop_code
		includes(:trip => :calendar).includes(:trip => [:route => :agency]).joins(:stop).where(stops: {stop_code: stop_code})
	end
end