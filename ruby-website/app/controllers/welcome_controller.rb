class WelcomeController < ApplicationController
    def test_siri
        @client = Savon.client(wsdl: root_url+"/siri/siri_wsConsumer.wsdl")

        @client.call(:GetStopMonitoringService, message: 
            [{
                version: "IL2.6"
            }])
    end

    def test_gtfs
        stop_id = 13938

        stop = Stop.find(stop_id)
        stop_times = stop.stop_times
        @stop_name = stop.stop_name
        @stop_times_per_day = (1..7).
            map { |day|
                    { :day => day,
                      :times => stop_times.
                        select { |st| st.days.include? day}.
                        map { |st| 
                                {
                                    :agency => st.route.agency.agency_name,
                                    :route => st.route.route_short_name, 
                                    :time => st.arrival_time[0..4]
                                }
                            }.
                        sort_by { |x| x[:time]}
                    }
                }
    end

end
