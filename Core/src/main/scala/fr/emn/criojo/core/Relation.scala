package fr.emn.criojo.core

/**
 * Created by IntelliJ IDEA.
 * User: mayleen
 * Date: Jun 11, 2010
 * Time: 2:16:47 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * The Relation trait
 * @define THIS Relation
 */
trait Relation{

  def name:String
  def public:Boolean
  def isMultiRel:Boolean
  def addObserver(observer:RelationObserver)
  def notifyObservers(a: Atom)
  def copy(sol:Solution):Relation

  override def toString = name
}

