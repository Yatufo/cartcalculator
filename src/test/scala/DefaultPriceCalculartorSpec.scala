import org.scalatest._
import org.scalatest.mockito.MockitoSugar

class DefaultPriceCalculartorSpec extends FlatSpec with Matchers with MockitoSugar {


  val bundlers: Seq[Bundler] = Seq(new TwoApplesForOneBundler, new OneAppleOneOrangeBundler)

  val apple = SingleItem("red apple", ItemCategory.APPLE, 10D)

  it should "bundle apples together when orange prices are low" in {
    val priceCalculator = new DefaultPriceCalculator(bundlers)
    val orange = SingleItem("big orange", ItemCategory.ORANGE, 5D)

    val inputCart = Cart(Seq(CartItem(apple, 2),CartItem(orange, 1)))

    priceCalculator.calculate(inputCart).price shouldBe 15
  }


  it should "bundle one apple and one orange together when orange prices are high" in {
    val priceCalculator = new DefaultPriceCalculator(bundlers.reverse)
    val orange = SingleItem("big orange", ItemCategory.ORANGE, 50D)

    val inputCart = Cart(Seq(CartItem(apple, 2),CartItem(orange, 1)))

    priceCalculator.calculate(inputCart).price shouldBe 58
  }


}
