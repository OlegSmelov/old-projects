require "spec_helper"

describe Bid do
  it "doesn't have a product assigned by default" do
    bid = Bid.new
    expect(bid.product).to be_nil
  end

  context "when populated with data" do
    subject(:bid) { create(:bid) }

    it "can be published" do
      expect(bid.save).to be_true
    end

    it "can't be published when product is not specified" do
      bid.product = nil
      expect(bid.save).to be_false
    end

    it "can't be published when bidder is not specified" do
      bid.bidder = nil
      expect(bid.save).to be_false
    end

    it "adds itself to product's bid list" do
      bid.save
      expect(bid.product.bids).to include bid
    end
  end
end