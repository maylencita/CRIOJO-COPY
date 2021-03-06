package fr.emn.criojo.ext

/**
 * Created by IntelliJ IDEA.
 * User: mayleen
 * Date: Sep 23, 2010
 * Time: 2:08:51 PM
 * To change this template use File | Settings | File Templates.
 */

import fr.emn.criojo.core._
import fr.emn.criojo.core.Criojo._
import fr.emn.criojo.lang.CrjAtom

@deprecated
abstract class TypedAtom[T](relName:String, value:T, val variable:Term)
        extends CrjAtom(relName, (if (value != null) List(Variable(value.toString),variable) else List(variable))){
  def unapply(ta:TypedAtom[_]) = ta match{
    case StringAtom(sval, strVar) => Option((sval, strVar))
    case IntAtom(num, intVar) => Option((num, intVar))
    case NullAtom(v) => Option((null, v))
    case _ => None
  }
}

@deprecated
case class StringAtom(sval:String, strVar:Term) extends
TypedAtom("$Str", sval, strVar){  

  def str:String = this.sval

  override def clone:Atom = {
    val a = new StringAtom(str,variable)
    a.active = this.active
    a.relation = this.relation
    a
  }

  override def applySubstitutions(subs:List[Substitution]):Atom = {
    def replaceVar(variable:Term):Term = variable match{
      case vl:Value[Int] => vl
      case _ => find(variable.name)
    }
    def find(name:String):Variable = subs.find(s => s._1.name == name) match{
        case Some((v1:Variable,v2:Variable)) => v2
        case _ => Undef
    }

    new StringAtom(sval, replaceVar(strVar))
  }
}

@deprecated
case class IntAtom(num:Int, intVar:Term) extends TypedAtom("$Int", num, intVar){
  def number:Int = this.num

  override def clone:Atom = {
    val a = new IntAtom(number,variable)
    a.active = this.active
    a.relation = this.relation
    a
  }

  override def applySubstitutions(subs:List[Substitution]):Atom = {
    def replaceVar(variable:Term):Term = subs.find(_._1.name == variable.name) match{
      case Some((v1:Variable,v2:Term)) => v2
      case _ => Undef
    }
    new IntAtom(num, replaceVar(intVar))
  }
}

@deprecated
case class NullAtom(v:Term) extends TypedAtom("Null", null, v){
  override def clone:Atom = {
    val a = new NullAtom(v)
    a.active = this.active
    a.relation = this.relation
    a
  }

  override def applySubstitutions(subs:List[Substitution]):Atom = this
}