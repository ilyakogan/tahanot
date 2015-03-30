class Calendar < ActiveRecord::Base
	self.table_name = "calendar"
	self.primary_key = "service_id"

	def days
		res = []
		res << 1 if sunday
		res << 2 if monday 
		res << 3 if tuesday
		res << 4 if wednesday
		res << 5 if thursday 
		res << 6 if friday
		res << 7 if saturday
		res
	end

	def self.find_by_services(service_ids)
		cals = where(service_id: service_ids)
		Calendar.new(
			sunday: (cals.map &:sunday).any?,
			monday: (cals.map &:monday).any?,
			tuesday: (cals.map &:tuesday).any?,
			wednesday: (cals.map &:wednesday).any?,
			thursday: (cals.map &:thursday).any?,
			friday: (cals.map &:friday).any?,
			saturday: (cals.map &:saturday).any?)
	end
end