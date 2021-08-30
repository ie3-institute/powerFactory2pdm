/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.{
  ModelConversionMode,
  QCharacteristic
}
import edu.ie3.powerFactory2psdm.config.model.WecConfig.{
  IndividualWecConfig,
  WecModelConversionMode
}
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod

final case class WecConfig(
    conversionMode: WecModelConversionMode,
    individualConfigs: Option[List[IndividualWecConfig]]
) extends DefaultModelConfig

object WecConfig {

  final case class IndividualWecConfig(
      ids: Set[String],
      conversionMode: WecModelConversionMode
  ) extends IndividualModelConfig

  /** Trait to group different methods for generating a value for a model
    * parameter
    */
  sealed trait WecModelConversionMode extends ModelConversionMode

  case object WecFixedFeedIn extends WecModelConversionMode

  case class WecModelGeneration(
      capex: ParameterSamplingMethod,
      opex: ParameterSamplingMethod,
      cpCharacteristics: String,
      hubHeight: ParameterSamplingMethod,
      rotorArea: ParameterSamplingMethod,
      etaConv: ParameterSamplingMethod,
      qCharacteristic: QCharacteristic
  ) extends WecModelConversionMode

}
