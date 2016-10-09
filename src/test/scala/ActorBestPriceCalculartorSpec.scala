import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.pattern.ask
import akka.testkit.{TestActorRef, TestProbe}
import akka.util.Timeout
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import org.mockito.Matchers._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class ActorBestPriceCalculartorSpec extends FlatSpec with Matchers with MockitoSugar {


  implicit val system = ActorSystem("testActorSystem")
  implicit val timeout = Timeout(1 seconds)


  private val MINIMUM: Int = 1
  case class PriceProbe(probe: TestProbe, result: Priceable)

  val calculators = (MINIMUM to 1000).map(id => {
    val priceable = mock[Priceable]
    when(priceable.price).thenReturn(id)
    val calculator = mock[PriceCalculator]
    when(calculator.calculate(any[Cart])).thenReturn(priceable)
    calculator
  })

  class MockActor(result : Priceable) extends Actor {
    def receive = {
      case _ => sender ! result
    }
  }

  val bestPriceActorRef = TestActorRef(ActorBestPriceCalculator.props(calculators))

  it should "return the best price based on the other actors result" in {
    val inputCart = Cart(Seq.empty[CartItem])
    val result = (bestPriceActorRef ? inputCart).mapTo[Priceable]

    assert(result.isReadyWithin(5 seconds))

    whenReady(result) { calculatedCart =>
      calculatedCart.price shouldBe MINIMUM
    }
  }



}
