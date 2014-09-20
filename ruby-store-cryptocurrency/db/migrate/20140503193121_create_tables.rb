class CreateTables < ActiveRecord::Migration
  def change
    create_table :users do |t|
      t.string :name
      t.string :full_name
      t.string :email
      t.string :password_digest
      
      t.timestamps
    end

    create_table :products do |t|
      t.string   :name
      t.float    :price
      t.string   :currency
      t.string   :selling_type, :default => 'auction'
      t.boolean  :sold
      t.integer  :buyer_id, :default => nil, :null => true
      t.integer  :seller_id
      t.datetime :end_date, :default => nil, :null => true

      t.timestamps
    end

    create_table :reviews do |t|
      t.float    :rating
      t.text     :text
      t.integer  :reviewer_id
      t.integer  :product_id

      t.timestamps
    end
    
    create_table :private_messages do |t|
      t.integer  :from_id, :default => nil, :null => true
      t.integer  :to_id
      t.string   :message

      t.timestamps
    end

    create_table :bids do |t|
      t.float    :price
      t.string   :currency
      t.datetime :date
      t.integer  :product_id
      t.integer  :bidder_id

      t.timestamps
    end
  end
end
