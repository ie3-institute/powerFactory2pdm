/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.common

import java.io.File
import edu.ie3.powerFactory2psdm.exception.io.GridParsingException
import edu.ie3.powerFactory2psdm.io.PfGridParser
import edu.ie3.powerFactory2psdm.model.powerfactory.{
  PowerFactoryGrid,
  PowerFactoryGridMaps
}

import java.util.UUID

trait ConverterTestData {

  val testGridFile =
    s"${new File(".").getCanonicalPath}/src/test/resources/pfGrids/exampleGrid.json"

  val testGrid: PowerFactoryGrid = PfGridParser
    .parse(testGridFile)
    .getOrElse(
      throw GridParsingException(s"Couldn't parse the grid file $testGridFile")
    )

  val pfGridMaps = new PowerFactoryGridMaps(testGrid)

  val subnet1UUIDs: Set[UUID] = pfGridMaps.nodeIdsToUUIDs(
    Set(
      "Grid.ElmNet\\Bus_0002.ElmTerm",
      "Grid.ElmNet\\Bus_0001.ElmTerm",
      "Grid.ElmNet\\Bus_0003.ElmTerm",
      "Grid.ElmNet\\Bus_0004.ElmTerm",
      "Grid.ElmNet\\Bus_0005.ElmTerm"
    )
  )
  val subnet2UUIDs: Set[UUID] =
    pfGridMaps.nodeIdsToUUIDs(Set("Grid.ElmNet\\Bus_0007.ElmTerm"))

  val subnet3UUIDs: Set[UUID] =
    pfGridMaps.nodeIdsToUUIDs(Set("Grid.ElmNet\\Bus_0008.ElmTerm"))

  val subnet4UUIDs: Set[UUID] = pfGridMaps.nodeIdsToUUIDs(
    Set(
      "Grid.ElmNet\\Bus_0006.ElmTerm",
      "Grid.ElmNet\\Bus_0009.ElmTerm",
      "Grid.ElmNet\\Bus_0011.ElmTerm",
      "Grid.ElmNet\\Bus_0010.ElmTerm",
      "Grid.ElmNet\\Bus_0012.ElmTerm",
      "Grid.ElmNet\\Bus_0013.ElmTerm",
      "Grid.ElmNet\\Bus_0014.ElmTerm",
      "Grid.ElmNet\\Bus_0015.ElmTerm",
      "Grid.ElmNet\\Ortsnetzstation.ElmTrfstat\\1.ElmTerm",
      "Grid.ElmNet\\Ortsnetzstation.ElmTrfstat\\2.ElmTerm",
      "Grid.ElmNet\\Ortsnetzstation.ElmTrfstat\\ON_Station_Lower.ElmTerm"
    )
  )

}
