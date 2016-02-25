/**
 * This class represents state and action pair and a double value in the
 * problem.it is distinguished by both state and action.
 * 
 * @author Sihan Cheng
 * */

public class QueueUnit implements Comparable<QueueUnit> {
	private State state;
	private Action action;
	private double value;

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

	public QueueUnit(State s, Action a, double v) {
		this.state = s;
		this.action = a;
		this.value = v;
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
		QueueUnit other = (QueueUnit) obj;
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

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public int compareTo(QueueUnit arg0) {
		// TODO Auto-generated method stub
		return (int) (this.getValue() - arg0.getValue());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return state + action.toString() + " " + value;
	}

}
