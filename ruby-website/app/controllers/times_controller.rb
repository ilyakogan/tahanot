class TimesController < ApplicationController
  def index
    stop_code = params[:stop_id]
    stop_times = StopTime.find_by_stop_code stop_code
    @stop_times_per_day = (1..7).
        map do |day|
          { :day => TimesHelper.day_to_human(day),
            :times => stop_times.
              select { |st| st.trip.calendar.days.include? day }.
              sort_by(&:departure_time)
          }
        end
    render partial: "index"
	end
end