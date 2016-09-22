import org.scalatest._

class TwoApplesForOneBundlerSpec extends FlatSpec with Matchers {

  val apple = SingleItem("red apple", ItemCategory.APPLE, 10D)
  val orange = SingleItem("yellow orange", ItemCategory.ORANGE, 5D)

  val bundler = new TwoApplesForOneBundler

  val twoApplesBundled = CartItem(Bundle(Seq(CartItem(apple, 2)), apple.price), 1)


  it should "bundle two apple for the same price of one" in {
    val inputCart = Cart(Seq(CartItem(apple, 2)))
    val expectedCart = twoApplesBundled

    bundler.bundle(inputCart).items should contain (twoApplesBundled)
  }

  it should "bundle odd number of apples" in {
    val inputCart = Cart(Seq(CartItem(apple, 3)))

    val bundledCart = bundler.bundle(inputCart)

    bundledCart.items should contain allOf (twoApplesBundled, CartItem(apple, 1))
    bundledCart.price shouldBe 20
  }
  it should "bundle a big odd number of apples" in {
    val inputCart = Cart(Seq(CartItem(apple, 9)))

    val bundledCart: Cart = bundler.bundle(inputCart)

    bundledCart.items should contain allOf (twoApplesBundled.copy(quantity = 4), CartItem(apple, 1))
    bundledCart.price shouldBe 50
  }


  it should "not bundle one apple" in {
    val inputCart = Cart(Seq(CartItem(apple, 1)))

    val bundledCart = bundler.bundle(inputCart)

    bundledCart shouldBe inputCart
    bundledCart.price shouldBe 10
  }

  it should "not bundle the oranges" in {
    val inputCart = Cart(Seq(CartItem(orange, 3)))

    val bundledCart = bundler.bundle(inputCart)

    bundledCart shouldBe inputCart
    bundledCart.price shouldBe 15
  }

}
