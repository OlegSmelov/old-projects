FactoryGirl.define do
  factory :product do
    sequence(:name) { |n| "Product #{n}" }
    price         100.0
    currency      { Product.supported_currencies.first }
    sold          false
    seller
    selling_type  :selling
  end

  factory :auction_product, parent: :product do
    sequence(:name) { |n| "Auction product #{n}" }
    selling_type  :auction
    end_date      { DateTime.now + 5 } # days
  end
end