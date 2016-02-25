/**
 * This class represents state and action pair in the problem.it is
 * distinguished by both state and action.
 * 
 * @author Sihan Cheng
 * */

public class StateActionPair {
	private State state;
	private Action action;

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	public StateActionPair(State s, Action a) {
		this.state = s;
		this.action = a;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateActionPair other = (StateActionPair) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

}
