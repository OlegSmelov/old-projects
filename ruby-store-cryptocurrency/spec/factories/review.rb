FactoryGirl.define do
  factory :review do
    text "Very good product."
    reviewer
    product
  end
end