class Product < ActiveRecord::Base
  belongs_to :buyer, class_name: "User"
  belongs_to :seller, class_name: "User"
  has_many   :reviews
  has_many   :bids do
    def << *args
      product = proxy_association.owner
      args.each do |bid|
        if product.price < bid.price
          product.price = bid.price
        end
      end
      super
    end
  end

  validates :price, numericality: { greater_than_or_equal_to: 0 }
  validates :seller, presence: true
  validates :end_date, presence: true, if: "selling_type == :auction"

  validate :currency_is_supported

  def selling_type
    super.to_sym
  end

  def selling_type=(value)
    super(value.to_sym)
    selling_type
  end

  def self.supported_currencies
    %w(BTC LTC DOGE)
  end

  def currency_is_supported
    if not Product.supported_currencies.include? self.currency
      errors.add(:currency, "is not supported.")
    end
  end

  def rating
    return nil if !reviews

    sum = 0.0
    count = 0.0

    reviews.each do |review|
      next if !review.rating
      sum += review.rating
      count += 1
    end

    return nil if count == 0
    return sum / count
  end

  def buy(buyer)
    return false if !buyer

    self.errors.clear
    self.errors.add(:base, "Product is already sold.") if self.sold
    self.errors.add(:base, "This product is not a selling, can't buy it directly.") if self.selling_type != :selling
    return false if self.errors.any?

    self.buyer = buyer
    self.sold = true
    save
  end
end
