/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.powerFactory2psdm.config.ConversionConfig.QCharacteristic
import edu.ie3.powerFactory2psdm.converter.ConversionHelper.{
  determineCosPhiRated,
  determineReactivePowerCharacteristic
}
import edu.ie3.powerFactory2psdm.model.powerfactory.StaticGenerator
import edu.ie3.util.quantities.PowerSystemUnits.MEGAVOLTAMPERE
import tech.units.indriya.quantity.Quantities

import java.util.{Locale, UUID}

object FixedFeedInConverter {

  /**
    * Converts a static generator to a [[FixedFeedInInput]]
    *
    * @param input generator to convert
    * @param node node the static generator is connected to
    * @return a fixed feed-in
    */
  def convert(
      input: StaticGenerator,
      node: NodeInput,
      qCharacteristic: QCharacteristic
  ): FixedFeedInInput = {

    val cosPhiRated = determineCosPhiRated(input)
    val reactivePowerCharacteristic =
      determineReactivePowerCharacteristic(qCharacteristic, cosPhiRated)
    val s = Quantities.getQuantity(input.sRated, MEGAVOLTAMPERE)

    new FixedFeedInInput(
      UUID.randomUUID(),
      input.id,
      node,
      reactivePowerCharacteristic,
      s,
      cosPhiRated
    )
  }

}
