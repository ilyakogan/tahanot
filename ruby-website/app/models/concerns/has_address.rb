module HasAddress extend ActiveSupport::Concern
  
  ADDR_REGEX = /רחוב:\s*(.*) עיר:\s*(.*) רציף:\s*(.*) קומה:\s*(.*)/i

  attr_accessor :street_addr, :town, :platform, :floor

  def fill_address
    self.street_addr, self.town, self.platform, self.floor = self.stop_desc.match(ADDR_REGEX).captures
  end
end