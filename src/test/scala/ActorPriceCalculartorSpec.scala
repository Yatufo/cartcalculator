import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.TestActorRef
import akka.util.Timeout
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._

class ActorPriceCalculartorSpec extends FlatSpec with Matchers with MockitoSugar {

  implicit val system = ActorSystem("testActorSystem")
  implicit val timeout = Timeout(1 seconds)

  val expected = mock[Priceable]
  val calculator = mock[PriceCalculator]

  when(calculator.calculate(any[Cart])).thenReturn(expected)
  val priceActor = TestActorRef(ActorPriceCalculator.props(calculator))

  it should "properly get a result for a specific cart" in {
    val result = priceActor ? Cart(Seq.empty[CartItem])

    assert(result.isReadyWithin(1 seconds))

    whenReady(result) { calculatedCart =>
      calculatedCart shouldBe expected
    }
  }


}
