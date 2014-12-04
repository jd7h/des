package robots.tasks.generator

import robots.tasks.rDSL.State
import robots.tasks.rDSL.Automata
import robots.tasks.rDSL.Arrow
import robots.tasks.rDSL.Action
import org.eclipse.emf.ecore.resource.Resource
import java.util.List
import java.util.ArrayList
import robots.tasks.rDSL.Priority

class Auxiliary {
	def static Automata getAutomata(Resource resource) {
		return resource.allContents.head as Automata 
 		// OR: resource.allContents.toIterable.filter(typeof(Planning)).head
 	}
 	
 	def static String getAutomataName(){
 		return Automata.name
 	}

	def static List<State> getStates(Resource resource) {
		return getAutomata(resource).statelist 
 	}

	//returns name of s capitalized (as used in the enums)
	def static String getStateName(State s){
		return s.name
	}

	def static String getStateItem(State s){
		return Str.toUpperCase(s.name)
	}

	//returns the start state
	def static State getStartState(Resource resource) {
		return getAutomata(resource).startstate
	}

	def static List<Arrow> getArrows(Resource resource) {
		return getAutomata(resource).arrowlist 
 	}
 	
 	def static List<Arrow> getInArrows (Resource resource, State s){
 		var List<Arrow> arrowlist = new ArrayList<Arrow>() 
 		for (Arrow a : getArrows(resource)){ 
 			if(a.to == s){
 				arrowlist.add(a)
 			}
 		}
 		return arrowlist; 
 		
 	}
 	
 	def static List<Action> getActionList(State s) {
 				return s.actionlist; 
 	}
 }
