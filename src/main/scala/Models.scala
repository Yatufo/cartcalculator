object ItemCategory extends Enumeration {
  type ItemType = Value
  val APPLE, ORANGE, BREAD, MILK = Value
}


sealed trait Priceable {
  def price: Double
}

sealed trait Item extends Priceable

case class SingleItem(name: String, category: ItemCategory.Value, price: Double) extends Item

case class Bundle(items: Seq[CartItem], price: Double) extends Item

case class CartItem(item: Item, quantity: Int)

case class Cart(items: Seq[CartItem]) extends Priceable {
  def price: Double = {
    items.map(cartItem => cartItem.quantity * cartItem.item.price).sum
  }
}

