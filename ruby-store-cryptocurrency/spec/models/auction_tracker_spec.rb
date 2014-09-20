require "spec_helper"

describe AuctionTracker do
  subject(:auction_tracker) { AuctionTracker.new }

  context "when there is an auction" do
    let!(:product) { create(:auction_product, sold: false, price: 100, end_date: DateTime.now - 5) }

    it "updates product sold status" do
      auction_tracker.update_auctions
      product.reload
      expect(product.sold).to be_true
    end

    it "doesn't update sold status if end date is in the future" do
      allow(DateTime).to receive(:now).and_return(DateTime.now - 100)
      auction_tracker.update_auctions
      product.reload
      expect(product.sold).to be_false
    end

    it "notifies seller that auction ended" do
      auction_tracker.update_auctions
      product.seller.inbox.reload
      expect(product.seller.inbox).to_not be_empty
    end

    context "when there are two bids" do
      let!(:bid_one) { create(:bid, product: product, price: 102) }
      let!(:bid_two) { create(:bid, product: product, price: 101) }

      it "notifies highest bidder" do
        auction_tracker.update_auctions
        bid_one.bidder.inbox.reload
        expect(bid_one.bidder.inbox).to_not be_empty
      end

      it "sells the product to highest bidder" do
        auction_tracker.update_auctions
        bid_one.bidder.bought_products.reload
        expect(bid_one.bidder.bought_products).to include product
      end
    end
  end
end