# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20140228232855) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"
  enable_extension "adminpack"

  create_table "agency", force: true do |t|
    t.string "agency_id"
    t.string "agency_name",                null: false
    t.string "agency_url",                 null: false
    t.string "agency_timezone", limit: 50, null: false
    t.string "agency_lang",     limit: 10
    t.string "agency_phone",    limit: 50
    t.string "agency_fare_url"
  end

  add_index "agency", ["agency_id"], name: "ix_agency_agency_id", unique: true, using: :btree

  create_table "calendar", id: false, force: true do |t|
    t.string  "service_id", null: false
    t.boolean "monday",     null: false
    t.boolean "tuesday",    null: false
    t.boolean "wednesday",  null: false
    t.boolean "thursday",   null: false
    t.boolean "friday",     null: false
    t.boolean "saturday",   null: false
    t.boolean "sunday",     null: false
    t.date    "start_date", null: false
    t.date    "end_date",   null: false
  end

  add_index "calendar", ["start_date", "end_date"], name: "calendar_ix1", using: :btree

  create_table "calendar_dates", id: false, force: true do |t|
    t.string  "service_id",     null: false
    t.date    "date",           null: false
    t.integer "exception_type", null: false
  end

  add_index "calendar_dates", ["date"], name: "ix_calendar_dates_date", using: :btree

  create_table "fare_attributes", id: false, force: true do |t|
    t.string  "fare_id",                                    null: false
    t.decimal "price",             precision: 10, scale: 2, null: false
    t.string  "currency_type",                              null: false
    t.integer "payment_method",                             null: false
    t.integer "transfers"
    t.integer "transfer_duration"
    t.string  "agency_id"
  end

  create_table "fare_rules", force: true do |t|
    t.string "fare_id",        null: false
    t.string "route_id"
    t.string "origin_id"
    t.string "destination_id"
    t.string "contains_id"
    t.string "service_id"
  end

  add_index "fare_rules", ["fare_id"], name: "ix_fare_rules_fare_id", using: :btree

  create_table "feed_info", id: false, force: true do |t|
    t.string "feed_publisher_name", null: false
    t.string "feed_publisher_url",  null: false
    t.string "feed_lang",           null: false
    t.date   "feed_start_date"
    t.date   "feed_end_date"
    t.string "feed_version"
    t.string "feed_license"
  end

  create_table "frequencies", id: false, force: true do |t|
    t.string  "trip_id",                null: false
    t.string  "start_time",   limit: 8, null: false
    t.string  "end_time",     limit: 8
    t.integer "headway_secs"
    t.integer "exact_times"
  end

  create_table "patterns", id: false, force: true do |t|
    t.string  "shape_id",                               null: false
    t.decimal "pattern_dist", precision: 20, scale: 10
  end

  create_table "route_type", id: false, force: true do |t|
    t.integer "route_type",      null: false
    t.string  "route_type_name"
    t.string  "route_type_desc"
  end

  create_table "routes", id: false, force: true do |t|
    t.string  "route_id",                     null: false
    t.string  "agency_id",        limit: nil
    t.string  "route_short_name"
    t.string  "route_long_name"
    t.string  "route_desc"
    t.integer "route_type",                   null: false
    t.string  "route_url"
    t.string  "route_color",      limit: 6
    t.string  "route_text_color", limit: 6
  end

  add_index "routes", ["agency_id"], name: "ix_routes_agency_id", using: :btree
  add_index "routes", ["route_type"], name: "ix_routes_route_type", using: :btree

  create_table "shapes", id: false, force: true do |t|
    t.string  "shape_id",                                                                                                    null: false
    t.decimal "shape_pt_lat",        precision: 12, scale: 9
    t.decimal "shape_pt_lon",        precision: 12, scale: 9
    t.integer "shape_pt_sequence",                             default: "nextval('shapes_shape_pt_sequence_seq'::regclass)", null: false
    t.decimal "shape_dist_traveled", precision: 20, scale: 10
  end

  create_table "stop_feature_type", id: false, force: true do |t|
    t.string "feature_type", limit: 50, null: false
    t.string "feature_name"
  end

  create_table "stop_features", force: true do |t|
    t.string "stop_id",                 null: false
    t.string "feature_type", limit: 50, null: false
  end

  add_index "stop_features", ["feature_type"], name: "ix_stop_features_feature_type", using: :btree
  add_index "stop_features", ["stop_id"], name: "ix_stop_features_stop_id", using: :btree

  create_table "stop_times", id: false, force: true do |t|
    t.string  "trip_id",                                                                                                               null: false
    t.string  "arrival_time",        limit: 8
    t.string  "departure_time",      limit: 8
    t.string  "stop_id",                                                                                                               null: false
    t.integer "stop_sequence",                                           default: "nextval('stop_times_stop_sequence_seq'::regclass)", null: false
    t.string  "stop_headsign"
    t.integer "pickup_type"
    t.integer "drop_off_type"
    t.decimal "shape_dist_traveled",           precision: 20, scale: 10
    t.boolean "timepoint"
  end

  add_index "stop_times", ["stop_id"], name: "ix_stop_times_stop_id", using: :btree
  add_index "stop_times", ["timepoint"], name: "ix_stop_times_timepoint", using: :btree

  create_table "stops", id: false, force: true do |t|
    t.string  "stop_id",                                                 null: false
    t.string  "stop_code",           limit: 50
    t.string  "stop_name",                                               null: false
    t.string  "stop_desc"
    t.decimal "stop_lat",                       precision: 12, scale: 9, null: false
    t.decimal "stop_lon",                       precision: 12, scale: 9, null: false
    t.string  "zone_id",             limit: 50
    t.string  "stop_url"
    t.integer "location_type"
    t.string  "parent_station"
    t.string  "stop_timezone",       limit: 50
    t.integer "wheelchair_boarding"
    t.string  "platform_code",       limit: 50
  end

  add_index "stops", ["location_type"], name: "ix_stops_location_type", using: :btree

  create_table "transfers", force: true do |t|
    t.string  "from_stop_id"
    t.string  "to_stop_id"
    t.integer "transfer_type"
    t.integer "min_transfer_time"
  end

  add_index "transfers", ["transfer_type"], name: "ix_transfers_transfer_type", using: :btree

  create_table "trips", id: false, force: true do |t|
    t.string  "route_id",              null: false
    t.string  "service_id",            null: false
    t.string  "trip_id",               null: false
    t.string  "trip_headsign"
    t.string  "trip_short_name"
    t.integer "direction_id"
    t.string  "block_id"
    t.string  "shape_id"
    t.string  "trip_type"
    t.integer "trip_bikes_allowed"
    t.integer "wheelchair_accessible"
  end

  create_table "universal_calendar", id: false, force: true do |t|
    t.string "service_id", null: false
    t.date   "date",       null: false
  end

  add_index "universal_calendar", ["date"], name: "ix_universal_calendar_date", using: :btree

end
