package org.tk.marmot
package core

import org.tk.marmot.core.time.{Calendar, CalendarId}

import scala.collection.mutable
import scala.io.Source
import scala.util.Using
import upickle.default.*

import java.time.LocalDate

/**
 * A trait representing a market data identifier for a specific type T.
 *
 * @tparam T the type of the market data item
 */
trait MarketDataId[T] {
  def klass: Class[T]
}

/**
 * A trait representing a market data identifier for a specific type T.
 *
 * @tparam T the type of the market data item
 */
trait RefDataId[T] {
  /**
   * Returns the class of the ref data item.
   *
   * @return the class of the ref data item
   */
  def klass: Class[T]

  def simpleId: String

  override def toString: String = s"${this.getClass.getSimpleName}('${simpleId}')"
}


/**
 * Superstati for different types of reference data providers
 */
trait ReferenceDataProvider {
  /**
   * Retrieves the reference data item associated with the given identifier.
   *
   * @param id the identifier of the reference data item
   * @tparam T the type of the reference data item
   * @return the reference data item associated with the given identifier
   */
  def get[T](id: RefDataId[T]): T
}


class FileReferenceDataProvider extends ReferenceDataProvider {
  private val referenceData: Map[RefDataId[?], Any] = new LoaderRunner(List(new FileReferenceDataLoader)).runLoaders(List("/home/pedro/IdeaProjects/marmot/example_data/holidays_data.json"))

  override def get[T](id: RefDataId[T]): T = {
    referenceData.get(id) match {
      case Some(value) => value.asInstanceOf[T]
      case None => throw new NoSuchElementException(s"Reference data not found for id: $id")
    }
  }
}

class LoaderRunner(loaders: List[ReferenceDataLoader[String]]) {
  def runLoaders(sources: List[String]): Map[RefDataId[?], Any] = {
    val loadedData = mutable.Map[RefDataId[?], Any]()
    for (source <- sources; loader <- loaders) {
      val data = loader.load(source)
      loadedData ++= data
    }
    loadedData.toMap
  }
}

/**
 * Trait for loading reference data of type T
 */
trait ReferenceDataLoader[S] {
  def load(source: S): Map[RefDataId[?], Any]
}

class FileReferenceDataLoader extends ReferenceDataLoader[String] {
  override def load(source: String): Map[RefDataId[?], Any] = {
    // Load from json

    val jsonString = Using(Source.fromFile(source)) { source =>
      source.getLines().mkString
    }.getOrElse(throw new RuntimeException(s"Failed to read file: $source"))
    // val jsonString = scala.io.Source.fromFile(source).getLines().mkString
    val json = ujson.read(jsonString)

    val ma = json.obj.map { case (key, value) =>
      val id = CalendarId(key)
      val holidays = value.arr.map(dateStr => LocalDate.parse(dateStr.str)).toSet
      id -> new Calendar(key, holidays)
    }.toMap
    scala.collection.immutable.HashMap(ma.toSeq: _*)
  }
}
