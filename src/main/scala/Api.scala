import akka.actor.Actor

import scala.collection.parallel.ParSeq

trait PriceCalculator {
  def calculate(cart: Cart): Priceable
}

/**
  * get the cart with the lowest price after being bundled
  *
  * @param bundlers bundlers to apply at the cart
  */
class DefaultBestPriceCalculator(val bundlers: Seq[Bundler]) extends PriceCalculator {

  val calculators = bundlers.permutations.toSeq.par.map(new DefaultPriceCalculator(_))

  override def calculate(cart: Cart): Priceable = {
    val bundledCarts = calculators.map(_.calculate(cart))(collection.breakOut)
    bundledCarts.minBy(_.price)
  }


}

class DefaultPriceCalculator(val bundlers: Seq[Bundler]) extends PriceCalculator {
  override def calculate(cart: Cart): Priceable = {
    bundlers.foldLeft(cart)((c, bundler) => bundler.bundle(c))
  }
}

object DefaultPriceCalculator {
  def apply(bundlers: Seq[Bundler]) = new DefaultPriceCalculator(bundlers)
}

