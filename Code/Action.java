/**
 * This class represents action for the problem. An action can have four
 * direction, up, right, down,left respectively
 * 
 * * @author Sihan Cheng
 * */
public class Action {
	int index;
	String UP = "U";
	String RIGHT = "R";
	String DOWN = "D";
	String LEFT = "L";

	public Action(int action) {
		this.index = action;
	}

	/**
	 * Convert the action's direction name to the integer number, which is for
	 * convenience
	 * 
	 * @param a
	 *            A string of action type.
	 */
	public Action(String a) {
		if (a.equals(UP))
			index = 0;
		else if (a.equals(RIGHT))
			index = 1;
		else if (a.equals(DOWN))
			index = 2;
		else if (a.equals(LEFT))
			index = 3;
		else
			throw (new IllegalArgumentException("Illegal action:" + a));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
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
		Action other = (Action) obj;
		if (index != other.index)
			return false;
		return true;
	}

	public String toString() {
		switch (index) {
		case 0:
			return "U";
		case 1:
			return "R";
		case 2:
			return "D";
		case 3:
			return "L";
		}
		return "?";
	}

}
