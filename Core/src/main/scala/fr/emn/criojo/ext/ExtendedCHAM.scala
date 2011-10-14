package fr.emn.criojo.ext

import fr.emn.criojo.core._
import fr.emn.criojo.lang.{CrjAtom, Molecule, Cham}

/**
 * Created by IntelliJ IDEA.
 * User: mayleen
 * Date: Oct 15, 2010
 * Time: 11:27:15 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Extended CHAM with support for constant values, pretty-print, etc.
 */
//TODO Add other CHAM traits.. for example: with NumberCHAM, DateCHAM...
abstract class ExtendedCHAM extends Cham with IntCHAM with StrCHAM with NullCHAM{

  private val x,y = Var

  /**********************************************************************
  * VM definition:
  */
  val Print = Rel("Print")

  private val Null_print = NativeRelation("Null_print"){(a,s) => println("Null")}

  rules(
    Print(x) --> NotNul(x) ?: (Int_print(x) &: Str_print(x)),
    Print(x) --> Nul(x) ?: Null_print(x)
  )
  /***********************************************************************/

  def getValue(v:Variable):ValueVariable[_]={
    if (nullVars contains v)
      Null
    else{
      getStrValue(v) getOrElse (getIntValue(v) getOrElse null) match{
        case v2 if(v2 != null) => new Value(v2)
        case _ => NoValue
      }
    }
  }

  def getPrintVariable(v:Variable):Variable = getValue(v) match{
    case value:Value[_] => value
    case NoValue => v
  }

  def prettyPrint:String = {
    solution.filter(! _.relName.startsWith("$")).
            map(a => new Atom(a.relName, a.vars.map(getPrintVariable(_)))).mkString("<",",",">")
  }
  
  private def printAtom(a:Atom)= a match{
    case Atom("Print", vars) => println( a.vars.map(v => getPrintVariable(v)).mkString(""," ","") )
    case _ => //Nothing
  }

  def generateValAtom(value:Any , variable: Variable): Atom = value match{
    case n:Int => IntAtom(n, variable)
    case s:String => StringAtom(s, variable)
    case _ => null
  }

  override def Rel(relName:String) = {
    val r = new ApplicableRel(relName,
      (vars:List[Variable]) => {
      var newVars = List[Variable]()
      var valVars = List[(Variable,Variable)]()
      val valAtoms = vars.foldLeft(List[Atom]()){
        case (lst, variable @ Value(v)) =>
          val newVar = Var
          valVars :+= (variable, newVar)
          newVars :+= newVar
          generateValAtom(v, newVar) +: lst
        case (lst, v:Variable ) =>
          valVars :+= (v, null)
          lst
      }
      val newMol = Molecule(newVars, valAtoms) &
        new CrjAtom(relName, valVars.map{
        case (variable, null) => variable
        case (_, newVariable) => newVariable
      }.toList)
      newMol
    })
    addRelation(r)
    r
  }

  implicit def intToVar(n:Int):Variable = new Value[Int](n)
  implicit def strToVar(str:String):Variable = new Value[String](str)

}

