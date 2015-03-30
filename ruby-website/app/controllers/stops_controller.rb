class StopsController < ApplicationController
  def show
    stop_code = params[:id]
    @stop = Stop.find_by stop_code: stop_code
    @closest_stops = @stop.find_closest(1000)
    @stop_address = [@stop.street_addr, @stop.town].reject(&:blank?).join(', ')
    @external_maps = StopsHelper.external_maps(@stop)
  end

  def index
    if params[:around_stop_code]
      around_stop_code = params[:around_stop_code]
      max_dist = params[:max_dist] || "1000m"
      max_dist_meters = max_dist.match(/(.*)m/i).captures[0]
      @stops = Stop.find_by(stop_code: around_stop_code).find_closest(Float(max_dist_meters))
    elsif params[:search]
      @stops = StopsSearchHelper.search params[:search]
    else
      @stops = Stop.all
    end

    respond_to do |format|
      format.html
      format.json { render json: @stops}
    end
  end
end
