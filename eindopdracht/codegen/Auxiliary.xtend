package robots.tasks.generator

import robots.tasks.rDSL.State
import robots.tasks.rDSL.Automata
import robots.tasks.rDSL.Arrow
import robots.tasks.rDSL.Action
import org.eclipse.emf.ecore.resource.Resource
import java.util.List
import java.util.ArrayList

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

	//returns state name as unformatted string
	def static String getStateName(State s){
		return s.name
	}

	//returns state name with first letter capitalized
	def static String getStateMethod(State s){
		return s.name.substring(0,1).toUpperCase() + s.name.substring(1)
	}

	//returns name of s in all caps (as used in the enums)
	def static String getStateItem(State s){
		return s.name.toUpperCase()
	}

	//returns the start state
	def static State getStartState(Resource resource) {
		return getAutomata(resource).startstate
	}

	//stub
	//returns the list of final states
	def static List<State> getEndStates(Resource resource){
		return getAutomata(resource).endstates
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

	def static List<Arrow> getOutArrows (Resource resource, State s){
		var List<Arrow> arrowlist = new ArrayList<Arrow>()
		for (Arrow a : getArrows(resource)){
			if (a.from == s){
				arrowlist.add(a)
			}
		}
		return arrowlist;
	}
 	
 	def static List<Action> getActionList(State s) {
 				return s.actionlist; 
 	}
 }
