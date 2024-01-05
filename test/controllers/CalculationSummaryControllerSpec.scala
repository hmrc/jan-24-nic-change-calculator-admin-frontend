/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import connectors.CalculationConnector
import models.CalculationSummaryData
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client.Retrieval.Username
import uk.gov.hmrc.internalauth.client.{FrontendAuthComponents, IAAction, Resource, ResourceLocation, ResourceType, Retrieval}
import uk.gov.hmrc.internalauth.client.test.{FrontendAuthComponentsStub, StubBehaviour}
import views.html.CalculationSummaryView

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CalculationSummaryControllerSpec
  extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with OptionValues
    with MockitoSugar
    with BeforeAndAfterEach {

  private val mockConnector = mock[CalculationConnector]
  private val mockStubBehaviour = mock[StubBehaviour]
  private val stubFrontendAuthComponents = FrontendAuthComponentsStub(mockStubBehaviour)(Helpers.stubControllerComponents(), implicitly)

  private val app = GuiceApplicationBuilder()
    .overrides(
      bind[FrontendAuthComponents].toInstance(stubFrontendAuthComponents),
      bind[CalculationConnector].toInstance(mockConnector)
    )
    .build()

  private implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  override protected def beforeEach(): Unit = {
    Mockito.reset(
      mockConnector,
      mockStubBehaviour
    )
    super.beforeEach()
  }

  "onPageLoad" - {

    "must return OK and the correct view" - {

      "for a get with no query string parameters" in {

        val request =
          FakeRequest(GET, routes.CalculationSummaryController.onPageLoad(None, None).url)
            .withSession("authToken" -> "Token some-token")

        val summaryData = CalculationSummaryData(None, None, 1, 2, 3, 4, 5)

        val predicate = Permission(Resource(ResourceType("jan-24-nic-change-calculator-admin-frontend"), ResourceLocation("summary")), IAAction("ADMIN"))
        when(mockStubBehaviour.stubAuth(eqTo(Some(predicate)), eqTo(Retrieval.username))).thenReturn(Future.successful(Username("username")))
        when(mockConnector.summaries(eqTo(None), eqTo(None))(any())).thenReturn(Future.successful(summaryData))

        val result = route(app, request).value
        val view = app.injector.instanceOf[CalculationSummaryView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(summaryData)(request, implicitly).toString
        verify(mockConnector, times(1)).summaries(eqTo(None), eqTo(None))(any())
      }

      "for a get with a `from` query string parameter" in {

        val from = Instant.ofEpochSecond(1)

        val request =
          FakeRequest(GET, routes.CalculationSummaryController.onPageLoad(Some(from), None).url)
            .withSession("authToken" -> "Token some-token")

        val summaryData = CalculationSummaryData(Some(from), None, 1, 2, 3, 4, 5)

        val predicate = Permission(Resource(ResourceType("jan-24-nic-change-calculator-admin-frontend"), ResourceLocation("summary")), IAAction("ADMIN"))
        when(mockStubBehaviour.stubAuth(eqTo(Some(predicate)), eqTo(Retrieval.username))).thenReturn(Future.successful(Username("username")))
        when(mockConnector.summaries(eqTo(Some(from)), eqTo(None))(any())).thenReturn(Future.successful(summaryData))

        val result = route(app, request).value
        val view = app.injector.instanceOf[CalculationSummaryView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(summaryData)(request, implicitly).toString
        verify(mockConnector, times(1)).summaries(eqTo(Some(from)), eqTo(None))(any())
      }

      "for a get with a `to` query string parameter" in {

        val to = Instant.ofEpochSecond(1)

        val request =
          FakeRequest(GET, routes.CalculationSummaryController.onPageLoad(None, Some(to)).url)
            .withSession("authToken" -> "Token some-token")

        val summaryData = CalculationSummaryData(None, Some(to), 1, 2, 3, 4, 5)

        val predicate = Permission(Resource(ResourceType("jan-24-nic-change-calculator-admin-frontend"), ResourceLocation("summary")), IAAction("ADMIN"))
        when(mockStubBehaviour.stubAuth(eqTo(Some(predicate)), eqTo(Retrieval.username))).thenReturn(Future.successful(Username("username")))
        when(mockConnector.summaries(eqTo(None), eqTo(Some(to)))(any())).thenReturn(Future.successful(summaryData))

        val result = route(app, request).value
        val view = app.injector.instanceOf[CalculationSummaryView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(summaryData)(request, implicitly).toString
        verify(mockConnector, times(1)).summaries(eqTo(None), eqTo(Some(to)))(any())
      }

      "for a get with `from` and `to` query string parameters" in {

        val from = Instant.ofEpochSecond(1)
        val to = Instant.ofEpochSecond(2)

        val request =
          FakeRequest(GET, routes.CalculationSummaryController.onPageLoad(Some(from), Some(to)).url)
            .withSession("authToken" -> "Token some-token")

        val summaryData = CalculationSummaryData(Some(from), Some(to), 1, 2, 3, 4, 5)

        val predicate = Permission(Resource(ResourceType("jan-24-nic-change-calculator-admin-frontend"), ResourceLocation("summary")), IAAction("ADMIN"))
        when(mockStubBehaviour.stubAuth(eqTo(Some(predicate)), eqTo(Retrieval.username))).thenReturn(Future.successful(Username("username")))
        when(mockConnector.summaries(eqTo(Some(from)), eqTo(Some(to)))(any())).thenReturn(Future.successful(summaryData))

        val result = route(app, request).value
        val view = app.injector.instanceOf[CalculationSummaryView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(summaryData)(request, implicitly).toString
        verify(mockConnector, times(1)).summaries(eqTo(Some(from)), eqTo(Some(to)))(any())
      }
    }

    "must redirect to login when the user is not authenticated" in {

      val request = FakeRequest(GET, routes.CalculationSummaryController.onPageLoad(None, None).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual s"/internal-auth-frontend/sign-in?continue_url=%2Fjan-24-nic-change-calculator-admin-frontend%2Fsummary"
    }

    "must fail when the user is not authorised" in {

      val predicate = Permission(Resource(ResourceType("jan-24-nic-change-calculator-admin-frontend"), ResourceLocation("summary")), IAAction("ADMIN"))
      when(mockStubBehaviour.stubAuth(eqTo(Some(predicate)), eqTo(Retrieval.username))).thenReturn(Future.failed(new Exception("foo")))

      val request =
        FakeRequest(GET, routes.CalculationSummaryController.onPageLoad(None, None).url)
          .withSession("authToken" -> "Token some-token")

      route(app, request).value.failed.futureValue
    }
  }
}
