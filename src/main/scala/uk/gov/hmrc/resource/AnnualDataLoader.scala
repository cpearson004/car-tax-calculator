package uk.gov.hmrc.resource

import play.api.Logger
import uk.gov.hmrc.model.yeardata.{TaxYear, TaxYearData, YearDataRepo}

import scala.io.{Codec, Source}

case class YearDataConstants(annualConstants: String = "annual-constants.csv",
                     basicPercentages: String = "basic-percentage.csv",
                     carBenefitPercentagePre98: String = "car-benefit-percentage-pre98.csv",
                     carBenefitPercentagePost98: String = "car-benefit-percentage-post98.csv",
                     carBenefitPercentage: String = "car-benefit-percentage-ZE.csv",
                     supportedTaxYears: List[TaxYear] = List(TaxYear.TaxYear2013, TaxYear.TaxYear2014, TaxYear.TaxYear2015))

trait AnnualDataLoader {

  def init(yearDataConstants: YearDataConstants = YearDataConstants()): Unit = {
    val pairs = yearDataConstants.supportedTaxYears.map { taxYear =>
      loadTaxYearData(taxYear, yearDataConstants) match {
          //TODO remove play Logger dependency
        case Some(tyd) => Logger.info(s"Loaded tax year data for ${taxYear.year}"); taxYear -> tyd
        case None => throw new RuntimeException(s"Failed to load Tax Year Data for $taxYear. See log messages for further details")
      }
    }
    YearDataRepo.init(Map(pairs: _*))
  }

  private def loadTaxYearData(taxYear: TaxYear, yearDataConstants: YearDataConstants): Option[TaxYearData] = {
    for {
      ac <- loadData(s"/annualConstants/year${taxYear.year}/${yearDataConstants.annualConstants}", AnnualConstantsLoader.loadData)
      bp <- loadData(s"/annualConstants/year${taxYear.year}/${yearDataConstants.basicPercentages}", BasicPercentagesLoader.loadData)
      pre98 <- loadData(s"/annualConstants/year${taxYear.year}/${yearDataConstants.carBenefitPercentagePre98}", CarBenefitsPre98Loader.loadData)
      post98 <- loadData(s"/annualConstants/year${taxYear.year}/${yearDataConstants.carBenefitPercentagePost98}", CarBenefitsPost98Loader.loadData)
      ze <- loadData(s"/annualConstants/year${taxYear.year}/${yearDataConstants.carBenefitPercentage}", CarBenefitsZELoader.loadData)
    } yield TaxYearData(ac, bp, pre98, post98, ze)
  }

  private def loadData[T](fileName: String, loadFn: Source => Either[List[String], T]): Option[T] = {
    val is = this.getClass.getResourceAsStream(fileName)
    val source = Source.fromInputStream(is)(Codec.UTF8)

    val data = loadFn(source)
    data.left.foreach(errs => errs.foreach(err => Logger.error(s"$fileName: $err")))

    data.right.toOption
  }
}

