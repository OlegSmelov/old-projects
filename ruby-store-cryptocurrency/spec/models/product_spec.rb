require "spec_helper"

describe Product do
  subject(:product) { Product.new }

  it "is of an auction type by default" do
    expect(product.selling_type).to eql(:auction)
  end

  it "has at least one supported currency" do
    expect(Product.supported_currencies).to have_at_least(1).item
  end

  it "has an empty list of reviews" do
    expect(product.reviews).to be_empty
  end

  it "doesn't have a rating if there are no reviews" do
    expect(product.rating).to be_nil
  end

  let(:review) { build(:review, product: nil) }

  it "has a list of reviews" do
    product.reviews << review
    expect(product.reviews).to include review
  end

  it "has a rating when there is a review" do
    review.rating = 10.0
    product.reviews << review
    expect(product.rating).to eql(10.0)
  end

  let(:review_one) { build(:review, product: nil, rating: 3.0) }
  let(:review_two) { build(:review, product: nil, rating: 5.0) }

  it "can calculate average rating of all reviews" do
    product.reviews << review_one
    product.reviews << review_two
    expect(product.rating).to eql(4.0)
  end

  it "skips reviews with no rating" do
    review_one.rating = nil
    review_two.rating = 3.5
    product.reviews << review_one
    product.reviews << review_two
    expect(product.rating).to eql(3.5)
  end

  it "has no rating if all reviews have no rating" do
    review_one.rating = nil
    review_two.rating = nil
    product.reviews << review_one
    product.reviews << review_two
    expect(product.rating).to be_nil
  end

  it "has no rating if there are no reviews" do
    expect(product.rating).to be_nil
  end

  context "when populated with data" do
    before :each do
      allow(Product).to receive(:supported_currencies).
        and_return(%w(BTC LTC DOGE))
    end

    subject(:product) { create(:product) }

    let(:buyer) { build(:buyer) }

    it "is valid" do
      expect(product).to be_valid
    end

    it "is invalid witout a seller" do
      product.seller = nil
      expect(product).to_not be_valid
    end

    it "accepts non-negative prices" do
      product.price = 1.0
      expect(product).to be_valid
    end

    it "rejects negative prices" do
      product.price = -1.0
      expect(product).to_not be_valid
    end

    it "rejects misspelled currencies" do
      product.currency = "btc"
      expect(product).to_not be_valid
    end

    it "rejects unsupported currencies" do
      product.currency = "notacurrency"
      expect(product).to_not be_valid
    end

    let(:bid) { build(:bid, product: nil) }

    it "has a list of bids" do
      product.bids << bid
      expect(product.bids).to include bid
    end

    it "associates with a bid after adding it" do
      bid.product = nil
      product.bids << bid
      expect(bid.product).to eq(product)
    end

    it "doesn't change the price if the bid was lower" do
      product.price = 100.0
      bid.price = 50.0
      product.bids << bid
      expect(product.price).to eql(100.0)
    end

    it "updates the price if the bid was higher" do
      product.price = 100.0
      bid.price = 200.0
      product.bids << bid
      expect(product.price).to eql(200.0)
    end

    context "when not sold" do
      before :each do
        product.selling_type = :selling
        product.sold = false
      end

      it "can be bought" do
        expect(product.buy(buyer)).to be_true
      end

      it "saves when bought" do
        expect(product).to receive(:save).and_call_original
        product.buy(buyer)
      end

      it "changes status to sold after being bought" do
        product.buy(buyer)
        expect(product.sold).to be_true
      end

      it "can't be bought if buyer is not specified" do
        expect(product.buy(nil)).to be_false
      end

      it "doesn't save if bought unsuccessfully" do
        expect(product).not_to receive(:save)
        product.buy(nil)
      end

      it "adds itself to the list of buyer's bought products" do
        product.buy(buyer)
        expect(buyer.bought_products.first).to eq(product)
      end
    end

    context "when sold" do
      before(:each) do
        product.buyer = buyer
        product.sold = true
      end

      it "cannot be bought" do
        expect(product.buy(buyer)).to be_false
      end
    end
  end
end