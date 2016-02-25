import java.util.ArrayList;

/**
 * This class represents state in the problem.States are distinguished by row,
 * column and the list of target state it carries
 * 
 * @author Sihan Cheng
 * */
public class State {
	/**
	 * the coordinates can only be initialized. They can't be changed.
	 */
	final int row, col;
	double utility;
	Action action;
	State nextState;

	public State(int row, int col, double utility) {
		this.row = row;
		this.col = col;
		this.utility = utility;
		this.action = null;
	}

	/**
	 * Check whether current position is the target position
	 * 
	 * @param targetList
	 *            the target list.
	 * */
	public boolean isEndState(State goal) {
		return this.row == goal.row && this.col == goal.col;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
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
		State other = (State) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

	public String toString() {
		return "(" + row + "," + col + ") ";
	}

}