
trait PriceCalculator{
  def calculate(cart : Cart): Priceable
}

/**
  * get the cart with the lowest price after being bundled
  * @param bundlers bundlers to apply at the cart
  */
class BestPriceCalculator(val bundlers: Seq[Bundler]) extends PriceCalculator{

  val permutations = bundlers.permutations.toSeq.par

  override def calculate(cart: Cart): Priceable = {
    val bundledCarts = permutations.map(permutation => {
      permutation.foldLeft(cart)((c,bundler) => bundler.bundle(c))
    })
    val minPrice = bundledCarts.map(_.price).min

    bundledCarts.find(_.price == minPrice).getOrElse(cart)
  }
}
