require "spec_helper"

describe User do
  subject(:user) { create(:user) }

  it "has a name" do
    user.name = "test"
    expect(user.name).to eql("test")
  end

  it "has an email" do
    user.email = "test@test.com"
    expect(user.email).to eql("test@test.com")
  end

  it "has an empty inbox" do
    expect(user.inbox).to be_empty
  end

  let(:pm) { create(:private_message) }

  it "can receive private messages" do
    expect { user.inbox << pm }.not_to raise_error
  end

  it "adds private messages to inbox" do
    user.inbox << pm
    expect(user.inbox).to include pm
  end

  context "when filled with data" do
    before :each do
      user.name = "admin"
      user.password = "qwerty"
      user.password_confirmation = "qwerty"
      user.full_name = "Administrator"
    end

    it "can be saved" do
      expect(user.save).to be_true
    end

    it "checks if password confirmation matches" do
      user.password = '123'
      user.password_confirmation = '456'
      expect(user.save).to be_false
    end

    it "accepts correct password" do
      expect(user.password).to eq("qwerty")
    end

    it "rejects wrong password" do
      expect(user.password).to_not eq("123456")
    end
  end
end
