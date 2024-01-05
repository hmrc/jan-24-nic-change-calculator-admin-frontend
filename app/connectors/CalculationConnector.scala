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

import config.Service
import models.CalculationSummaryData
import play.api.Configuration
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculationConnector @Inject()(config: Configuration, httpClient: HttpClientV2)
                                    (implicit ec: ExecutionContext) {

  private val dateFormat = DateTimeFormatter.ISO_INSTANT
  private val baseUrl = config.get[Service]("microservice.services.jan-24-nic-change-calculator")
  private def fullUrl(from: Option[Instant], to: Option[Instant]) = url"$baseUrl/jan-24-nic-change-calculator/summary?from=${from.map(dateFormat.format)}&to=${to.map(dateFormat.format)}"

  def summaries(from: Option[Instant], to: Option[Instant])(implicit hc: HeaderCarrier): Future[CalculationSummaryData] =
    httpClient
      .get(fullUrl(from, to))
      .execute[CalculationSummaryData]
}
