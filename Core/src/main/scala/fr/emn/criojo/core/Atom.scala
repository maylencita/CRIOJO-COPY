package fr.emn.criojo.core

import Creole.Substitution

/**
 * Created by IntelliJ IDEA.
 * User: mayleen
 * Date: Jun 9, 2010
 * Time: 5:47:03 PM
 * To change this template use File | Settings | File Templates.
 */

@serializable
object Atom{
  def apply(rn:String, lst:Variable*):Atom = new Atom(rn, lst.toList)
  def apply(rel:Relation, lst:Variable*):Atom = {
    val a = new Atom(rel.name, lst.toList)
    a.relation = rel
    a
  }
}

case class Atom (val relName:String, val vars: List[Variable]) {
  protected var active:Boolean = true
  @transient
  var relation:Relation = _

  def isTrue:Boolean = false
  def isFalse:Boolean = false

  def arity = vars.size

  @deprecated ("Use: Solution.inactivate")
  def inactivate{
    active = false
  }  

  def setActive(active:Boolean) { this.active = active }
  def isActive:Boolean = this.active
  
  def apply(n:Int):Variable = vars(n) 

  def applySubstitutions(subs:List[Substitution]):Atom = {
    val nuRel:Relation = subs.find(s => s._1.name == this.relName) match{
      case Some(sub) => sub match{
        case (_, rv:RelVariable) => rv.relation
        case _ => this.relation
      }
      case _ => this.relation
    }

    val nuRelName:String = subs.find(s => s._1.name == this.relName) match{
      case Some(nv) => nv._2.name
      case _ => this.relName
    }

    def replace(variable:Variable):Variable = {
      val newVar = subs.find(s => s._1.name == variable.name)
      newVar match{
        case Some(nv) => nv._2
        case _ =>
          variable match{
            case rv:RelVariable => if (rv.relation != null) rv else Undef
            case _ =>
              if (variable.name.startsWith("$"))
                variable
              else
                Undef
          }
      }
    }

    val newVars = this.vars.map {v => replace(v)}

    val atom = new Atom(/*nuRel.name*/ nuRelName, newVars)
    atom.relation = nuRel
    atom
  }

  def matches(that: Atom) : Boolean = {
    this.relName == that.relName &&
    this.arity == that.arity &&
    this.vars.zip(that.vars).forall(p => p._1.matches(p._2))
  }

  def hasVariable(v: Variable): Boolean = {
    this.vars.contains(v)
  }

  override def hashCode =
    if (relation != null && relation.isMultiRel)
      super.hashCode
    else
      toString.hashCode

  override def equals(that: Any):Boolean = that match{
    case a:Atom =>
      if (relation != null && relation.isMultiRel)
        super.equals(a)
      else
        this.relName == a.relName && this.vars.zip(a.vars).forall(p => p._1 == p._2)
    case _ => false
  }

  override def toString =
    (if (active) "" else "!") +
            (if (relation != null) relation else relName) +
            (if (vars.isEmpty) "" else vars.mkString("(",",",")"))

  override def clone:Atom = {
    val a = new Atom(relName,vars)
    a.active = this.active
    a.relation = this.relation
    a
  }
}

