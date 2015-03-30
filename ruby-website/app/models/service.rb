class Service < ActiveRecord::Base
	belongs_to :route
	has_many :trips
  has_many :stop_times, primary_key: "some_trip_id", foreign_key: "trip_id"
  has_many :stops, through: :stop_times
  has_one :calendar
  has_one :agency, through: :route

  attr_accessor :last_stop

  def self.find_by_stop_code(stop_code)
    joins(:stops).
      where(stops: {stop_code: stop_code}).
      order(:weekly_trips).reverse_order
  end
end