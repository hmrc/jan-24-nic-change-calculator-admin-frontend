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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import models.CalculationSummaryData
import org.scalatest.{EitherValues, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant

class CalculationConnectorSpec
  extends AnyFreeSpec
    with WireMockHelper
    with ScalaFutures
    with Matchers
    with IntegrationPatience
    with EitherValues
    with OptionValues
    with MockitoSugar {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.jan-24-nic-change-calculator.port" -> server.port
      )
      .build()

  private lazy val connector: CalculationConnector = app.injector.instanceOf[CalculationConnector]

  ".summaries" - {

    "must return calculation summary data when the server returns OK" - {

      "when `from` and `to` are not specified" in {

        val summaryData = CalculationSummaryData(None, None, 1, 2, 3, 4, 5, 6, 7)

        server.stubFor(
          get(urlEqualTo("/jan-24-nic-change-calculator/summary"))
            .willReturn(ok(Json.toJson(summaryData).toString))
        )

        connector.summaries(None, None).futureValue mustEqual summaryData
      }

      "when `from` is specified" in {

        val instant = Instant.ofEpochSecond(1)

        val summaryData = CalculationSummaryData(None, None, 1, 2, 3, 4, 5, 6, 7)

        server.stubFor(
          get(urlEqualTo("/jan-24-nic-change-calculator/summary?from=1970-01-01T00:00:01Z"))
            .willReturn(ok(Json.toJson(summaryData).toString))
        )

        connector.summaries(Some(instant), None).futureValue mustEqual summaryData
      }

      "when `to` is specified" in {

        val instant = Instant.ofEpochSecond(1)

        val summaryData = CalculationSummaryData(None, None, 1, 2, 3, 4, 5, 6, 7)

        server.stubFor(
          get(urlEqualTo("/jan-24-nic-change-calculator/summary?to=1970-01-01T00:00:01Z"))
            .willReturn(ok(Json.toJson(summaryData).toString))
        )

        connector.summaries(None, Some(instant)).futureValue mustEqual summaryData
      }

      "when `from` and `to` are specified" in {

        val from = Instant.ofEpochSecond(1)
        val to = Instant.ofEpochSecond(2)

        val summaryData = CalculationSummaryData(None, None, 1, 2, 3, 4, 5, 6, 7)

        server.stubFor(
          get(urlEqualTo("/jan-24-nic-change-calculator/summary?from=1970-01-01T00:00:01Z&to=1970-01-01T00:00:02Z"))
            .willReturn(ok(Json.toJson(summaryData).toString))
        )

        connector.summaries(Some(from), Some(to)).futureValue mustEqual summaryData
      }
    }

    "must return a failed future when the server responds with an error" in {

      server.stubFor(
        get(urlEqualTo("/jan-24-nic-change-calculator/summary"))
          .willReturn(serverError())
      )

      connector.summaries(None, None).failed.futureValue
    }
  }
}
