class ServicesController < ApplicationController
  def index
    @stop_code = params[:stop_id]
    raw_services = Service.find_by_stop_code @stop_code
    raw_services.each do |s|
      s.last_stop = Stop.find_last_in_trip s.some_trip_id
    end

    merged_services = ServicesHelper.merge_services_having_similar_details raw_services

    @services = merged_services#.map do |s|
    #   [s, ServicesHelper.generate_footnotes(s, @stop_id)]
    # end
    render partial: "index"
  end

  def footnotes
    stop_code = params[:stop_id]
    service_ids = params[:service_ids].split(/,/)
    @footnotes = ServicesHelper.generate_footnotes service_ids, stop_code
    
    render partial: "footnotes"
  end
end