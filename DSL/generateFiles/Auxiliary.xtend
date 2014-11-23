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

	//stub
	//returns name of s
	def static String getStateName(State s){
		return s.name
	}

	//return capitalized name of s (used for class names)
	def static String getBehaviorName(State s){
		return s.name.substring(0,1).toUpperCase() + s.name.substring(1)
	}

	//stub
	//returns a unique int-id for state s
	def static int getStateNumber(State s){
		return Integer.parseInt(s.name)
	}
	
	//stub
	//returns the start state
	def static State getStartState(Resource resource) {
		return getAutomata(resource).startstate
	}

	//stub
	//return a list of states, sorted on their priority
	//we need to change something in the grammar for this
	def static List<State> getSortedStates(Resource resource){
		var List<State> statelist = new ArrayList<State>() 
		var List<State> sortedlist = new ArrayList<State>() 
		statelist = getStates(resource)
		for( State s : statelist){
			if(s.priority == Priority.HIGH)
			{
				sortedlist.add(s)
			}
		}
		for( State s : statelist){
			if(s.priority == Priority.MIDDLE)
			{
				sortedlist.add(s)
			}
		}
		for( State s : statelist){
			if(s.priority == Priority.LOW)
			{
				sortedlist.add(s)
			}
		}
		return sortedlist
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
