module ServicesHelper
  Infinity = 1.0 / 0.0
  
  #def self.frequency_categories
  #  [OpenStruct.new(weekly_trips_range: (500...Infinity), name: 'קו תדיר מאד'),
  #   OpenStruct.new(weekly_trips_range: (350...500), name: 'קו תדיר'),
  #   OpenStruct.new(weekly_trips_range: (200...350), name: 'קו בתדירות בינונית'),
  #   OpenStruct.new(weekly_trips_range: (50...200), name: 'קו בתדירות נמוכה'),
  #   OpenStruct.new(weekly_trips_range: (0...50), name: 'קו בתדירות נמוכה מאד')]
  #end

  def self.generate_footnotes(service_ids, stop_code)
    footnotes = []
    cal = Calendar.find_by_services(service_ids)
    if cal.days == [1]
      footnotes.push('פועל ביום ראשון בלבד')
    elsif cal.days == [5]
      footnotes.push('פועל ביום חמישי בלבד')
    elsif cal.days == [6]
      footnotes.push('פועל ביום שישי בלבד')
    elsif cal.days == [7]
      footnotes.push('פועל ביום שבת בלבד')
    elsif cal.days == [6,7]
      footnotes.push('פועל בסוף השבוע בלבד')
    elsif not cal.days.include? 6 and not cal.days.include? 7
      footnotes.push('לא פועל בסוף השבוע')
    end

    start_time = StopTime.find_service_start_time service_ids, stop_code
    end_time = StopTime.find_service_end_time service_ids, stop_code
    if start_time > '18'
      footnotes.push('פועל בערב בלבד')
    elsif start_time > '12'
      footnotes.push('פועל אחר הצהריים בלבד')
    elsif end_time < '05'
      footnotes.push('פועל אחרי חצות בלבד')
    elsif end_time < '09'
      footnotes.push('פועל מוקדם בבוקר בלבד')
    elsif end_time < '12'
      footnotes.push('פועל בבוקר בלבד')
    end

    # if service.weekly_trips / cal.days.count < 20
    #   footnotes.push('קו בתדירות נמוכה')
    # elsif service.weekly_trips > 400
    #   footnotes.push('קו בתדירות גבוהה')
    # end
    
    footnotes
  end

  def self.merge_services_having_similar_details(services)
    services.group_by do |s|
        [s.route_short_name, s.agency_name, s.last_stop]
      end.
      values.map do |group|
          service = group[0]
          service.service_id = group.map &:service_id
          service.weekly_trips = (group.map &:weekly_trips).reduce :+
          service
      end.
      sort_by{|s| s.weekly_trips}.reverse
  end
end