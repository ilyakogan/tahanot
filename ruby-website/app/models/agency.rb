class Agency < ActiveRecord::Base
  self.table_name = "agency"
  self.primary_key = "agency_id"
	has_many :routes
end