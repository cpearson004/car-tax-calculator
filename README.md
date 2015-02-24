Car-tax-calculator
==================

Car tax calculation library

Based on annual constants provided by policy, this library calculates company car tax for current year and forecast year (CY+1).
There are also classes for calculation for fuel benefit and employee payments.

#### Add car-tax-calculator

In your project/Build.scala:

```scala
libraryDependencies ++= Seq(
  "uk.gov.hmrc" %% "car-tax-calculator" % "2.1.1"
)
```
#### Initialise the repo data ###

This is to be done before calling methods from the library.

This can be done by extending `DataInitializer` or by importing `DataInitializer`.


```scala

object InitialisedTestData extends DataInitializer {

  val cy = new LocalDate().getYear
    val benefitData = new NewBenefitCalculationData(false,
      FuelTypes.DIESEL,
      Some(125),
      Some(1400),
      Some(2000),
      15000,
      new LocalDate(cy - 1, 5, 1),
      new LocalDate(cy, 4, 5),
      None,
      None,
      Some(20),
      Some(EmployeePaymentsFrequency.WEEKLY),
      Some(new LocalDate(cy, 4, 5))
    )
  }

```

```scala
  class CarBenefitCalculatorSpec extends WordSpecLike with Matchers with OptionValues{

      InitialisedTestData.initialiseAnnualTestData()

      ...
      }

```

==================
This repo uses git-flow
