package testUtils

import com.google.inject.AbstractModule
import views.html.homePageView

class TestModule extends AbstractModule {
  override def configure(): Unit = {
    // Bind the homePageView to itself (identity binding) for testing purposes
    bind(classOf[homePageView]).to(classOf[homePageView])
  }
}
