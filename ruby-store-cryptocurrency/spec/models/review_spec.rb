require "spec_helper"

describe Review do
  subject(:review) { Review.new }

  it "has unspecified rating by default" do
    expect(review.rating).to be_nil
  end

  it "can't be published" do
    expect(review.save).to be_false
  end

  context "when populated with data" do
    subject(:review) { build(:review) }

    it "can be published" do
      expect(review.save).to be_true
    end

    it "can't be published anonymously" do
      review.reviewer = nil
      expect(review.save).to be_false
    end

    it "can't be published without any text" do
      review.text = nil
      expect(review.save).to be_false
    end

    it "can't be published with empty text" do
      review.text = ''
      expect(review.save).to be_false
    end

    it "can't be published without a product" do
      review.product = nil
      expect(review.save).to be_false
    end

    it "can be published without a rating" do
      review.rating = nil
      expect(review.save).to be_true
    end

    it "can't be published by the seller" do
      # aka seller can't publish reviews for his/her own products
      review.product.seller = review.reviewer
      expect(review.save).to be_false
    end

    it "adds itself to product's reviews after publishing" do
      review.save
      expect(review.product.reviews).to include review
    end
  end
end