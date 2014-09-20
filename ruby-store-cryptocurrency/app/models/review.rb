class Review < ActiveRecord::Base
  belongs_to :reviewer, :class_name => "User"
  belongs_to :product

  validates_presence_of :reviewer, :text, :product

  validate :reviewer_is_not_product_seller

  def reviewer_is_not_product_seller
    return if !self.reviewer || !self.product || !self.product.seller
    if self.reviewer == self.product.seller
      errors.add(:base, "Product seller can't review his/her own products.")
    end
  end
end
