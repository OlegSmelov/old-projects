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

ActiveRecord::Schema.define(version: 20140503193121) do

  create_table "bids", force: true do |t|
    t.float    "price"
    t.string   "currency"
    t.datetime "date"
    t.integer  "product_id"
    t.integer  "bidder_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "private_messages", force: true do |t|
    t.integer  "from_id"
    t.integer  "to_id"
    t.string   "message"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "products", force: true do |t|
    t.string   "name"
    t.float    "price"
    t.string   "currency"
    t.string   "selling_type", default: "auction"
    t.boolean  "sold"
    t.integer  "buyer_id"
    t.integer  "seller_id"
    t.datetime "end_date"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "reviews", force: true do |t|
    t.float    "rating"
    t.text     "text"
    t.integer  "reviewer_id"
    t.integer  "product_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "users", force: true do |t|
    t.string   "name"
    t.string   "full_name"
    t.string   "email"
    t.string   "password_digest"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
