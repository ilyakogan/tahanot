class Route < ActiveRecord::Base
	belongs_to :agency
	has_many :trips

  def night?
    self.route_color == '9933FF'
  end

  def school?
    self.route_color == 'FF9933'
  end
end