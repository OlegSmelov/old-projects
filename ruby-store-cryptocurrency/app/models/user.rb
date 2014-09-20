class User < ActiveRecord::Base
  has_secure_password
  
  has_many :bought_products, :class_name => "Product", :foreign_key => "buyer_id"
  has_many :reviews, :foreign_key => "reviewer_id"
  has_many :bids, :foreign_key => "bidder_id"
  has_many :inbox, :class_name => "PrivateMessage", :foreign_key => "to_id"

  validates :name, presence: true, uniqueness: true
  validates :password, presence: true, confirmation: true, on: :create
end
