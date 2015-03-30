module StopsSearchHelper
  def self.search(term)
    Stop.where("stop_code = ? or stop_desc like ? or stop_name like ?", term, "%#{term}%", "%#{term}%")
  end
end