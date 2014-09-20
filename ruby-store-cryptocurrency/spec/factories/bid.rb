FactoryGirl.define do
  factory :bid do
    price    200
    currency { Product.supported_currencies.first }
    date     { DateTime.now }
    bidder

    after(:build) do |bid, evaluator|
      if evaluator.try(:product)
        bid.product = evaluator.try(:product)
        bid.currency = evaluator.try(:currency) || bid.product.currency
      else
        bid.product = create(:auction_product, currency: bid.currency)
      end
    end
  end
end