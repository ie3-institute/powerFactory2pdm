/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

import edu.ie3.powerFactory2psdm.config.ConversionConfig.{
  Fixed,
  GenerationMethod,
  ModelConfigs,
  NormalDistribution,
  PvConfig,
  PvModelGeneration,
  UniformDistribution
}
import edu.ie3.powerFactory2psdm.exception.io.ConversionConfigException

import scala.util.{Failure, Success, Try}

object ConfigValidator {

  /**
    * Checks the parsed [[ConversionConfig]] for general soundness.
    * @param config the parsed config
    */
  def validate(config: ConversionConfig): Unit = {
    validateModelConfigs(config.modelConfigs)
  }

  private def validateModelConfigs(modelConfigs: ModelConfigs): Unit = {
    validatePvConfig(modelConfigs.pvConfig)
  }

  private def validatePvConfig(pvConfig: PvConfig): Unit = {
    Seq(pvConfig.conversionMode) ++ pvConfig.individualConfigs
      .getOrElse(Nil)
      .map(conf => conf.conversionMode)
      .collect {
        case pvModelGeneration: PvModelGeneration => pvModelGeneration
      }
      .map(validatePvModelGenerationParams)
  }

  private def validatePvModelGenerationParams(params: PvModelGeneration): Unit = {
    validateGenerationMethod(params.albedo, 0, 1) match {
      case Success(_) =>
      case Failure(exc: Exception) =>
        throw ConversionConfigException(
          s"The albedo of the plants surrounding: ${params.albedo} isn't valid. Exception: ${exc.getMessage}"
        )
    }
    validateGenerationMethod(params.azimuth, -90, 90) match {
      case Success(_) =>
      case Failure(exc: Exception) =>
        throw ConversionConfigException(
          s"The azimuth of the plant: ${params.azimuth} isn't valid. Exception: ${exc.getMessage}"
        )
    }
    validateGenerationMethod(params.etaConv, 0, 100) match {
      case Success(_) =>
      case Failure(exc: Exception) =>
        throw ConversionConfigException(
          s"The efficiency of the plants inverter: ${params.azimuth} isn't valid. Exception: ${exc.getMessage}"
        )
    }
    validateGenerationMethod(params.kG, 0, 1) match {
      case Success(_) =>
      case Failure(exc: Exception) =>
        throw ConversionConfigException(
          s"The PV generator correction factor (kG): ${params.kG} isn't valid. Exception: ${exc.getMessage}"
        )
    }
    validateGenerationMethod(params.kT, 0, 1) match {
      case Success(_) =>
      case Failure(exc: Exception) =>
        throw ConversionConfigException(
          s"The PV temperature correction factor (kT): ${params.kT} isn't valid. Exception: ${exc.getMessage}"
        )
    }
  }

  private def validateGenerationMethod(
      genMethod: GenerationMethod,
      lowerBound: Double,
      upperBound: Double
  ): Try[Unit] =
    genMethod match {
      case Fixed(value) =>
        checkForBoundViolation(value, lowerBound, upperBound)
      case UniformDistribution(min, max) =>
        if (min > max)
          return Failure(
            ConversionConfigException(
              s"The minimum value: $min exceeds the maximum value: $max"
            )
          )
        if (min < lowerBound && max > upperBound)
          return lowerUpperBoundViolation(min, max, lowerBound, upperBound)
        else if (min < lowerBound) return lowerBoundViolation(min, lowerBound)
        else if (max > upperBound) return upperBoundViolation(max, upperBound)
        Success()
      case NormalDistribution(mean, _) =>
        checkForBoundViolation(mean, lowerBound, upperBound)
    }

  private def checkForBoundViolation(
      value: Double,
      lowerBound: Double,
      upperBound: Double
  ): Try[Unit] = {
    if (value < lowerBound) return lowerBoundViolation(value, lowerBound)
    if (value > upperBound) return upperBoundViolation(value, upperBound)
    Success()
  }

  private def lowerBoundViolation(
      value: Double,
      lowerBound: Double
  ): Failure[Unit] = Failure(
    ConversionConfigException(
      s"The parameters value: $value lies below the lower bound: $lowerBound"
    )
  )

  private def upperBoundViolation(
      value: Double,
      upperBound: Double
  ): Failure[Unit] = Failure(
    ConversionConfigException(
      s"The parameters value: $value exceeds the upper bound: $upperBound"
    )
  )

  private def lowerUpperBoundViolation(
      min: Double,
      max: Double,
      lowerBound: Double,
      upperBound: Double
  ): Failure[Unit] =
    Failure(
      ConversionConfigException(
        s"The minimum: $min and maximum: $max of the uniform distribution lie below the lower bound: $lowerBound and above the upper bound: $upperBound "
      )
    )

}
