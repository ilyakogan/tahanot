class Trip < ActiveRecord::Base
	belongs_to :route
  belongs_to :service
	has_many :stop_times
	has_many :stops, through: :stop_times
	belongs_to :calendar, foreign_key: "service_id"
  has_one :agency, through: :route
end