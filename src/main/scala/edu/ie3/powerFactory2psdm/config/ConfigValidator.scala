/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

import edu.ie3.powerFactory2psdm.config.ConversionConfig.Model
import edu.ie3.powerFactory2psdm.config.ConversionConfig.Model.DefaultParams.Pv
import edu.ie3.powerFactory2psdm.exception.io.ConversionConfigException

/**
  * Provides functionality, to validate if a given [[ConversionConfig]] has valid content or not.
  * Most parts of the checks (if all needed fields are apparent or not) is automatically done by
  * the typesafe framework while parsing the input data
  */
case object ConfigValidator {

  /**
    * Checks the validity of the given [[ConversionConfig]].
    *
    * @param config The config to check
    */
  def checkValidity(config: ConversionConfig): Unit = {
    checkValidity(config.model)
  }

  /**
    * Checks the validity of the given [[ConversionConfig.Model]] config.
    *
    * @param model The model to check
    */
  def checkValidity(model: Model): Unit = {
    checkPvValidity(model.defaultParams.pv)
  }

  /**
    * Checks the validity of the given [[ConversionConfig]]. If any content is not valid, a
    * [[ConversionConfigException]] is thrown.
    *
    * @param model The model to check
    */
  def checkPvValidity(params: Pv): Unit = {
    params.albedo match {
      case x if (0 <= x && x <= 1) =>
      case _ =>
        throw ConversionConfigException(
          "Faulty config: The albedo factor should be between 0 and 1"
        )
    }
  }

}
