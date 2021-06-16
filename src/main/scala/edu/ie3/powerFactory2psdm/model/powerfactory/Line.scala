/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.{ConversionException, ElementConfigurationException, MissingParameterException}
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.Lines

/**
  * Electrical line
  *
  * @param id identifier
  * @param nodeAId id of connected node
  * @param nodeBId id of connected node
  */
final case class Line(
    id: String,
    nodeAId: String,
    nodeBId: String,
    typId: String,
    length: Double,
    gpsCoords: Option[(List[(Double, Double)])]
) extends EntityModel
    with Edge

object Line {
  def build(rawLine: Lines): Line = {
    val id = rawLine.id match {
      case Some(id) if EntityModel.isUniqueId(id) => id
      case Some(id) =>
        throw ElementConfigurationException(s"ID: $id is not unique")
      case None =>
        throw MissingParameterException(s"There is no id for line $rawLine")
    }
    val nodeAId = rawLine.bus1Id.getOrElse(
      throw MissingParameterException(s"Line: $id has no defined node a")
    )
    val nodeBId = rawLine.bus2Id.getOrElse(
      throw MissingParameterException(s"Line: $id has no defined node b")
    )
    val typId = rawLine.typId.getOrElse(
      throw MissingParameterException(s"Line: $id has no defined type - line conversion without defined type" +
        s" is not supported ")
    )
    val length = rawLine.dline.getOrElse(
      throw MissingParameterException(
        s"Line: $id has no defined length"
      )
    )

    val gpsCoords: Option[List[(Double, Double)]] = rawLine.GPScoords match {
      case Some(List(Some(Nil)))  => None
      case Some(coords) => Option(coords.flatten.map {
        case List(Some(lat), Some(lon)) => (lat, lon)
      })
      case None => None
    }

    Line(
      id,
      nodeAId,
      nodeBId,
      typId,
      length,
      gpsCoords
    )
  }

}
