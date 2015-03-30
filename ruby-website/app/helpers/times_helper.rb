module TimesHelper
  def self.day_to_human(day)
    case day
    when 1
      'יום ראשון'
    when 2
      'יום שני'
    when 3
      'יום שלישי'
    when 4
      'יום רביעי'
    when 5
      'יום חמישי'
    when 6
      'יום שישי'
    when 7
      'שבת'
    end
  end
end
