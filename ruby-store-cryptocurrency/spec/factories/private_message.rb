FactoryGirl.define do
  factory :private_message do
    to      { create(:user) }
    message "Hi!"
  end
end