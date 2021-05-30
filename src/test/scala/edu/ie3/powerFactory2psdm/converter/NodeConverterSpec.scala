/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel
import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.exception.pf.TestException
import edu.ie3.powerFactory2psdm.model.Subnet
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import tech.units.indriya.quantity.Quantities

import scala.util.{Failure, Success}

class NodeConverterSpec
    extends Matchers
    with ConverterTestData
    with AnyWordSpecLike {

  "The node converter" should {
    val voltageLevel: VoltageLevel = new VoltageLevel(
      "Hochspannung",
      Quantities.getQuantity(132, StandardUnits.RATED_VOLTAGE_MAGNITUDE)
    )
    val bus3UUID = pfGridMaps.nodeId2Uuid(bus3Id)
    val bus4UUID = pfGridMaps.nodeId2Uuid(bus4Id)
    val pfXnetBus = pfGridMaps.uuid2Node(pfGridMaps.nodeId2Uuid(bus1Id))
    val nodeUUIDs = Set(bus3UUID, bus4UUID)
    val testSubnet = Subnet(2, nodeUUIDs, voltageLevel)

    "convert a pf node to a correctly configured PSDM Node" in {
      val convertedNode = NodeConverter.convertNode(
        pfGridMaps.nodeId2Uuid(bus3Id),
        pfGridMaps.uuid2Node,
        testSubnet
      )
      convertedNode.getUuid shouldBe bus3UUID
      convertedNode.getId shouldBe bus3Id
      convertedNode.getVoltLvl shouldBe voltageLevel
      convertedNode.getSubnet shouldBe testSubnet.id
      convertedNode.isSlack shouldBe false
    }

    "correctly identify that a node connected to an external grid is a slack node" in {
      NodeConverter.isSlack(pfXnetBus.conElms) shouldBe Success(true)
    }

    "should throw a failure checking for a slack node if the list of connected elements is empty" in {
      NodeConverter.isSlack(pfGridMaps.uuid2Node(bus3UUID).conElms) shouldBe Success(
        false
      )
    }

    "should throw a failure checking for a slack node if the connected elements are None" in {
      val withEmptyConElms = pfXnetBus.copy(conElms = None)
      NodeConverter.isSlack(withEmptyConElms.conElms) match {
        case Success(true) =>
          throw TestException("This should not be a slack node!")
        case Success(false) =>
          throw TestException("This should have returned a Failure!")
        case Failure(ex) =>
          ex.getMessage shouldBe "The optional connected elements attribute is None."
      }
    }
  }
}
