/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.reduce

import com.opencsv.CSVWriter
import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.csv.CsvGraphicSource
import edu.ie3.datamodel.io.source.csv.CsvRawGridSource
import edu.ie3.datamodel.io.source.csv.CsvTypeSource
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.container.GraphicElements
import edu.ie3.datamodel.models.input.container.JointGridContainer
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SystemParticipants
import edu.ie3.datamodel.models.input.system._
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import edu.ie3.datamodel.io.sink.CsvFileSink

import java.io.{BufferedWriter, File, FileWriter}
import scala.jdk.CollectionConverters._
import java.util.UUID
import scala.util.{Failure, Try}

object GridModelReducer {

  def main(args: Array[String]): Unit = {

    // 1. read in grid model without
    val gridName = "exampleGrid"
    val reducedGridName = "reduced" + gridName
    val csvSep = ","
    val folderPath = "./exampleGrid"
    val namingStrategy = new FileNamingStrategy() // Default naming strategy
    val (rawGridElements, graphicElements) =
      readGridModel(csvSep, folderPath, namingStrategy)

    // 3. create a system participant for each node
    val fixedFeedIns = createFixedFeedIns(rawGridElements)
    val systemParticipants = new SystemParticipants(
      Set.empty[BmInput].asJava,
      Set.empty[ChpInput].asJava,
      Set.empty[EvcsInput].asJava,
      Set.empty[EvInput].asJava,
      fixedFeedIns.asJava,
      Set.empty[HpInput].asJava,
      Set.empty[LoadInput].asJava,
      Set.empty[PvInput].asJava,
      Set.empty[StorageInput].asJava,
      Set.empty[WecInput].asJava
    )
    val reducedGrid = new JointGridContainer(
      reducedGridName,
      rawGridElements,
      systemParticipants,
      graphicElements
    )

    // 4. write out grid
    val resultFolderPath = "./exampleGrid"
    val initEmptyFiles = false
    val sink =
      new CsvFileSink(resultFolderPath, namingStrategy, initEmptyFiles, csvSep)
    sink.persistJointGrid(reducedGrid)

    // 5. write out mapping from node to system participant in csv file
    val mappingFileName = new File(".").getCanonicalPath + ""
    writeMapping(mappingFileName, fixedFeedIns)

  }

  def readGridModel(
      csvSep: String,
      folderPath: String,
      namingStrategy: FileNamingStrategy
  ): (RawGridElements, GraphicElements) = {

    /* Instantiating sources */
    val typeSource = new CsvTypeSource(csvSep, folderPath, namingStrategy)
    val rawGridSource =
      new CsvRawGridSource(csvSep, folderPath, namingStrategy, typeSource)
    val graphicsSource = new CsvGraphicSource(
      csvSep,
      folderPath,
      namingStrategy,
      typeSource,
      rawGridSource
    )

    /* Loading models */
    val rawGridElements = rawGridSource.getGridData.orElseThrow(() =>
      new SourceException("Error during reading of raw grid data.")
    )
    val graphicElements = graphicsSource.getGraphicElements.orElseThrow(() =>
      new SourceException("Error during reading of graphic elements.")
    )

    (rawGridElements, graphicElements)
  }

  def createFixedFeedIns(
      gridElements: RawGridElements
  ): Set[FixedFeedInInput] = {
    val nodes = gridElements.getNodes
    nodes.asScala.map(createFixedFeedIn).toSet
  }

  def createFixedFeedIn(node: NodeInput): FixedFeedInInput = {
    new FixedFeedInInput(
      UUID.randomUUID(),
      s"Participant-Node-${node.getUuid}",
      node,
      ReactivePowerCharacteristic.parse(
        s"cosPhiFixed:{(0.0, 0.95)}"
      ),
      1.0.asKiloWatt,
      0.95
    )
  }

  def writeMapping[T <: SystemParticipantInput](
      fileName: String,
      participants: Set[T]
  ): Unit = {
    val header = List("node", "participant")
    val rows = participants
      .map(participant =>
        List(participant.getNode.getUuid.toString, participant.getUuid.toString)
      )
      .toList
    writeCsvFile(fileName, header, rows)
  }

  def writeCsvFile(
      fileName: String,
      header: List[String],
      rows: List[List[String]]
  ): Try[Unit] =
    Try(new CSVWriter(new BufferedWriter(new FileWriter(fileName)))).flatMap(
      (csvWriter: CSVWriter) =>
        Try {
          csvWriter.writeAll(
            (header +: rows).map(_.toArray).asJava
          )
          csvWriter.close()
        } match {
          case f @ Failure(_) =>
            // Always return the original failure.  In production code we might
            // define a new exception which wraps both exceptions in the case
            // they both fail, but that is omitted here.
            Try(csvWriter.close()).recoverWith { case _ =>
              f
            }
          case success =>
            success
        }
    )
}
