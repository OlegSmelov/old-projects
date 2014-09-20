FactoryGirl.define do
  factory :user, aliases: [:seller, :buyer, :reviewer, :bidder] do
    sequence(:name)      { |n| "user#{n}" }
    sequence(:full_name) { |n| "User #{n}" }
    email                 "john@doe.com"
    password              "t3st"
    password_confirmation "t3st"
  end
end