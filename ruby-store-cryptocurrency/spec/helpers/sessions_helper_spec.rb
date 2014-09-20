require 'spec_helper'

# Specs in this file have access to a helper object that includes
# the SessionsHelper. For example:
#
# describe SessionsHelper do
#   describe "string concat" do
#     it "concats two strings with spaces" do
#       expect(helper.concat_strings("this","that")).to eq("this that")
#     end
#   end
# end
describe SessionsHelper do
  let!(:user) { create(:user) }

  it "signs users in" do
    helper.sign_in(user)
    expect(helper.current_user).to eq(user)
  end

  it "signs users in #2" do
    helper.sign_in(user)
    expect(helper.signed_in?).to be_true
  end

  it "signs users out" do
    helper.sign_in(user)
    helper.sign_out
    expect(helper.current_user).to eq(nil)
  end

  it "signs users out #2" do
    helper.sign_in(user)
    helper.sign_out
    expect(helper.signed_in?).to be_false
  end
end
