import org.scalatest._

class OneAppleOneOrangeBundlerSpec extends FlatSpec with Matchers {

  val apple = SingleItem("red apple", ItemCategory.APPLE, 10D)
  val orange = SingleItem("big orange", ItemCategory.ORANGE, 5D)
  val bread = SingleItem("loaf of bread", ItemCategory.BREAD, 5D)

  val oneAppleOneOrange = Seq(CartItem(apple, 1), CartItem(orange, 1))
  val oneAppleOneOrangeBundle = Bundle(oneAppleOneOrange, 12D)

  val bundler = new OneAppleOneOrangeBundler


  it should "bundle one apple and one oranges with 20% discount" in {
    val inputCart = Cart(oneAppleOneOrange)

    bundler.bundle(inputCart).items should contain (CartItem(oneAppleOneOrangeBundle, 1))
  }

  it should "bundle one apple and two oranges" in {
    val oneAppleTwoOranges = Seq(CartItem(apple, 1), CartItem(orange, 2))
    val inputCart = Cart(oneAppleTwoOranges)

    bundler.bundle(inputCart).items should contain allOf (CartItem(oneAppleOneOrangeBundle, 1), CartItem(orange, 1))
  }

  it should "not bundle anything" in {
    val oneAppleOneBread = Seq(CartItem(bread, 1), CartItem(orange, 2))
    val inputCart = Cart(oneAppleOneBread)

    bundler.bundle(inputCart).items shouldBe inputCart.items
  }


}
