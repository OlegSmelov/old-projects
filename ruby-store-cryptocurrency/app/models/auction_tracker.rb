class AuctionTracker
  def update_auctions
    now = DateTime.now
    unsold_auctions = Product.where(selling_type: :auction, sold: false).load
    unsold_auctions.each do |auction|
      next if not auction.end_date
      if auction.end_date <= now
        update_auction(auction)
        auction.save
      end
    end
  end

  private

  def update_auction(auction)
    auction.sold = true

    return if not auction.bids

    highest_bid = nil
    highest_price = -1
    auction.bids.each do |bid|
      if highest_price < bid.price
        highest_price = bid.price
        highest_bid = bid
      end
    end

    if not highest_bid
      send_message(auction.seller, "Your auction '" + auction.name +
                   "' ended, however, nobody bidded on it.")
      return
    end

    highest_bidder = highest_bid.bidder
    return if not highest_bidder

    auction.buyer = highest_bidder

    send_message(highest_bidder, "Your bid on product '" + auction.name + "' won, contact " +
                 auction.seller.email + ' for further information.')
    send_message(auction.seller, "Your auction '" + auction.name + "' ended, " + highest_bidder.name +
                 " <" + highest_bidder.email + "> won.")
  end

  def send_message(to, message)
    pm = PrivateMessage.new
    pm.from = nil # system message
    pm.to = to
    pm.message = message
    pm.save
  end
end