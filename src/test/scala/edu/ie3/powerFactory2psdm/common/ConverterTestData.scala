/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.common

import java.io.File

import edu.ie3.powerFactory2psdm.exception.io.GridParsingException
import edu.ie3.powerFactory2psdm.io.PfGridParser
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.Nodes
import edu.ie3.powerFactory2psdm.model.powerfactory.{
  PowerFactoryGrid,
  PowerFactoryGridUtils
}

trait ConverterTestData {

  val testGridFile =
    s"${new File(".").getCanonicalPath}/src/test/resources/pfGrids/14NodeExampleGrid.json"

  val testGrid: PowerFactoryGrid = PfGridParser
    .parse(testGridFile)
    .getOrElse(
      throw GridParsingException(s"Couldn't parse the grid file $testGridFile")
    )

  val nodesMap: Map[String, Nodes] = PowerFactoryGridUtils.getNodesMap(testGrid)

}
