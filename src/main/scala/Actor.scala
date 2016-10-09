import akka.actor.ActorSystem
import akka.pattern.ask
import akka.actor._
import akka.event.Logging
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import akka.pattern.{ask, pipe}

class ActorPriceCalculator(priceCalculator: PriceCalculator) extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case cart: Cart => sender ! priceCalculator.calculate(cart)
    case _ => throw InvalidMessageException("This actor only works with carts")
  }

}

object ActorPriceCalculator {
  def props(priceCalculator: PriceCalculator): Props = Props(new ActorPriceCalculator(priceCalculator))
}


/**
  * get the cart with the lowest price after being bundled.
  * the underlying implementation uses actors.
  *
  * @param calculators child actors
  */
class ActorBestPriceCalculator(val calculators: Seq[PriceCalculator])(implicit val ec: ExecutionContext) extends Actor {

  implicit val timeout = Timeout(5 seconds)
  val childrenCalculators = calculators.map(calculator => context.actorOf(ActorPriceCalculator.props(calculator)))

  def receive = {

    case cart: Cart => {
      val futurePriceables = childrenCalculators.map(calculator => (calculator ? cart).mapTo[Priceable])
      val futureBestPrice = Future.sequence(futurePriceables).map(priceables => priceables.minBy(_.price))

      futureBestPrice pipeTo sender
    }

    case _ => throw InvalidMessageException("This actor only works with carts")
  }

}


object ActorBestPriceCalculator {

  def props(calculators: Seq[PriceCalculator])(implicit ex: ExecutionContext): Props = {
    Props(new ActorBestPriceCalculator(calculators))
  }

}