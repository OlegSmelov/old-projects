require "spec_helper"

describe PrivateMessage do
  subject(:pm) { PrivateMessage.new }

  it "doesn't have a message by default" do
    expect(pm.message).to be_nil
  end

  it "cannot be sent out" do
    expect(pm.save).to be_false
  end

  it "has a message field" do
    pm.message = "Hi!"
    expect(pm.message).to eql("Hi!")
  end

  context "when populated with data" do
    let(:sender)   { create(:user) }
    let(:receiver) { create(:user) }
    subject(:pm) { build(:private_message, from: sender, to: receiver) }

    it "can be sent out" do
      expect(pm).to run(:save).successfully
    end

    it "can be sent out anonymously" do
      pm.from = nil
      expect(pm).to run(:save).successfully
    end

    it "can't be sent out without a message" do
      pm.message = nil
      expect(pm).to run(:save).unsuccessfully
    end

    it "is not sent out to receiver's inbox when sending fails" do
      pm.message = nil
      pm.save
      expect(receiver).not_to receive_message(pm)
    end

    it "is sent out to receiver's inbox" do
      pm.save
      expect(receiver).to receive_message(pm)
    end
  end
end