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
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.internalauth.client.{FrontendAuthComponents, IAAction, Resource, ResourceLocation, ResourceType, Retrieval}
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CalculationSummaryView

import java.time.Instant
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CalculationSummaryController @Inject()(
                                              val controllerComponents: MessagesControllerComponents,
                                              auth: FrontendAuthComponents,
                                              view: CalculationSummaryView,
                                              connector: CalculationConnector
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def authorised(from: Option[Instant], to: Option[Instant]) = auth.authorizedAction(
    continueUrl = routes.CalculationSummaryController.onPageLoad(from, to),
    predicate = Permission(
      Resource(
        ResourceType("jan-24-nic-change-calculator-admin-frontend"),
        ResourceLocation("summary")
      ),
      IAAction("ADMIN")
    ),
    retrieval = Retrieval.username
  )

  def onPageLoad(from: Option[Instant], to: Option[Instant]): Action[AnyContent] =
    authorised(from, to).async { implicit request =>
      connector.summaries(from, to).map {
        summaryData =>
          Ok(view(summaryData))
      }
    }
}
