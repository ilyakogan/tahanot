module StopsHelper
  def self.push(array, link, label, image)
    array.push(OpenStruct.new(link: link, label: label, image: image))
  end

  def self.external_maps(stop)
    maps = []
    lat = stop.stop_lat
    lon = stop.stop_lon
    if lat.between?(31.950, 32.129) and lon < 34.904 # South of Rishon - Lod rwy sta - Adanim jct - Glilot
      push maps, 'http://telaviv.busmappa.com/p/blog-page.html', 'מפת קווי האוטובוס והרכבת של גוש דן', 'alan-gush-dan-map.png'
      push maps, 'http://telaviv.busmappa.com/p/blog-page_26.html', '"מטרו גוש דן" - מפת הקווים התדירים ביותר', 'alan-gush-dan-metro.png'
      push maps, 'http://telaviv.busmappa.com/p/bus-map.html', 'Tel Aviv & Dan Region Route Map', 'alan-gush-dan-map-english.png'
    end

    if lat.between?(32.749, 32.875) and lon < 35.140 # South of Tirat Carmel - Hilf - North of Kiryat Bialik i.z.
      push maps, 'http://telaviv.busmappa.com/p/blog-page_2.html', 'מפת קווי האוטובוס והרכבת של חיפה והקריות', 'alan-haifa-map.png'
    end
    
    if lat.between?(31.2, 31.3) and lon.between(34.75 ,34.85)
      push maps, 'http://telaviv.busmappa.com/p/blog-page_03.html', 'מפת קווי האוטובוס של באר שבע', 'alan-beer-sheva-map.png'
    end

    if stop.town == 'ירושלים'
      push maps, 'http://jet.gov.il/Web/He/Lines/Map/Default.aspx', 'מפת קווי האוטובוס והרכבת הקלה של ירושלים', 'jet-jerusalem-map.png'
    end

    maps
  end
end