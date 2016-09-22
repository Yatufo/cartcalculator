trait Bundler {
  def bundle(cart: Cart): Cart

  def getByCategory(category: ItemCategory.Value): (CartItem) => Boolean = {
    case CartItem(item: SingleItem, _) => item.category == category
    case _ => false
  }
}


/**
  * Offers two apples for the price of one
  */
class TwoApplesForOneBundler extends Bundler {

  override def bundle(cart: Cart): Cart = {
    val bundledItems = cart.items.flatMap(cartItem => cartItem match {
      case CartItem(item: SingleItem, _) if item.category == ItemCategory.APPLE && cartItem.quantity > 1 =>
        bundleAsPairs(item, cartItem.quantity)
      case _ => Seq(cartItem)
    })

    Cart(bundledItems)
  }


  def bundleAsPairs(item: SingleItem, quantity: Int): Seq[CartItem] = {

    val isOdd = quantity % 2 == 1
    val bundleQuantity: Int = quantity / 2

    val bundle = CartItem(Bundle(Seq(CartItem(item, 2)), item.price), bundleQuantity)

    (if (isOdd) Seq(CartItem(item, 1)) else Seq.empty[CartItem]) ++ Seq(bundle)

  }
}

/**
  * Offers 20% off when buying an Apple and an Orange togueter
  */
class OneAppleOneOrangeBundler extends Bundler {
  override def bundle(cart: Cart): Cart = {

    val bundledCart = for {
      apples <- cart.items.find(getByCategory(ItemCategory.APPLE))
      oranges <- cart.items.find(getByCategory(ItemCategory.ORANGE))
    } yield {
      val difference = apples.quantity - oranges.quantity
      val moreApplesThanOranges = difference > 0
      val excess = math.abs(difference)

      val (limited, exceeded) = if (moreApplesThanOranges) (oranges, apples) else (apples, oranges)
      val elementsToRemove = Seq(apples, oranges)
      var elementsToAdd = if (excess > 0) Seq(CartItem(exceeded.item, excess)) else Seq.empty[CartItem]

      val discountedPrice = (apples.item.price + oranges.item.price) * 0.80
      val singleBundle = Bundle(Seq(CartItem(apples.item, 1), CartItem(oranges.item, 1)), discountedPrice)

      elementsToAdd ++= Seq(CartItem(singleBundle, limited.quantity))

      val filtered = cart.items.filterNot(elementsToRemove.contains(_))
      cart.copy(items = filtered ++ elementsToAdd)
    }

    bundledCart.getOrElse(cart)
  }


}
