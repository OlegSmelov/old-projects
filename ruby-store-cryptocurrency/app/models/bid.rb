class Bid < ActiveRecord::Base
  belongs_to :product
  belongs_to :bidder, class_name: "User"

  validates_presence_of :price
  validates_presence_of :currency
  validates_presence_of :date
  validates_presence_of :bidder
  validates_presence_of :product

  validate :product_valid?, if: "product"

  def product_valid?
    errors.add(:product, "is not of auction selling type.") if product.selling_type != :auction
    errors.add(:base, "Product's seller and bidder can't be the same.") if product.seller && product.seller == bidder
    errors.add(:base, "You can only bid higher than the current price.") if price <= product.price
  end
end
