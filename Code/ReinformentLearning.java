import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * This is the ice world solver, runs Q Learning, Sarsa, Sarsa Lambda algorithm,
 * find the best policy for the ice world.
 * 
 * @author Sihan Cheng
 * */
public class ReinformentLearning {

	public static int cols;
	public static int rows;

	// q value table, rows are states, column are actions
	public static HashMap<StateActionPair, Double> Qvalue;

	// list to store eligibility traces value for every queueunit which consist
	// of state and action pair
	public static ArrayList<QueueUnit> trace;
	public static int iceWorld[][];

	public static Action[] actions;

	// Action representation by integer number
	static final int ACTION_UP = 0;
	static final int ACTION_RIGHT = 1;
	static final int ACTION_DOWN = 2;
	static final int ACTION_LEFT = 3;

	// Use number to represent the grid-world environment
	static int START = 1;
	static int GOAL = 10;
	static int OPENSPACE = 0;
	static int ICYSURFACE = -1;
	static int HOLES = -100;

	// the output file name
	public String sarsaFile = "Sarsa";
	public String sarsaOutput = "";

	public String qLearningFile = "QLearning";
	public String qLearnigOutput = "";

	public String sarsaLFile = "SarsaL";
	public String sarsaLOutput = "";

	// List to store all states
	public static ArrayList<State> stateList;
	public static State startState;
	public static State goalState;

	// Constant initialization
	public final double GAMMA = 0.9;
	public final double ALPHA = 0.9;
	public final double EPSLION = 0.9;
	public final double LAMBDA = 0.9;

	public int episodes = 1;
	public static final int NORMAL_ITERATION = 2000;
	public static final int SLOW_ITERATION = 5000;
	public static final int RAPID_ITERATION = 800;

	// Reward for different position
	public final int R_GOAL = 0;
	public final int R_OPEN = -1; // start and open space both are -1 reward
	public final int R_HOLE = -100;

	public String path;
	public String tRewOutput = "";
	public double tRew;

	public ReinformentLearning() {

		actions = new Action[4];
		actions[0] = new Action(ACTION_UP);
		actions[1] = new Action(ACTION_RIGHT);
		actions[2] = new Action(ACTION_DOWN);
		actions[3] = new Action(ACTION_LEFT);
		trace = new ArrayList<QueueUnit>();
		path = Class.class.getClass().getResource("/").getPath();
	}

	public static void main(String args[]) {
		ReinformentLearning is = new ReinformentLearning();
		// mdpa.readFile(fileName)
		try {
			// String fileName = args[0];
			String fileName = "C:\\Users\\Administrator\\Desktop\\CS452-AI\\hw06\\iceWorld_larger.txt";
			is.readFileByLine(fileName);
			is.initalIceWorld(fileName);
			is.QLearning(0, NORMAL_ITERATION);
			// is.QLearning(1, SLOW_ITERATION);
			// is.QLearning(2, RAPID_ITERATION);
			is.reset();
			// is.Sarsa(1, SLOW_ITERATION);
			// is.Sarsa(2, RAPID_ITERATION);
			is.Sarsa(0, NORMAL_ITERATION);

			is.reset();
			is.SarsaLambda(0, NORMAL_ITERATION);
			// is.SarsaLambda(1, SLOW_ITERATION);
			// is.SarsaLambda(2, RAPID_ITERATION);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initial the Q table and the iceWorld.
	 * 
	 * @param fileName
	 *            the input file path
	 */
	public static void initalIceWorld(String fileName) {
		File file = new File(fileName);
		Reader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(file));
			int tempchar;
			// current row and col
			int trow = 0;
			int tcol = 0;
			while ((tempchar = reader.read()) != -1) {
				int charValue = 0;
				char c = (char) tempchar;
				switch (c) {
				case 'S':
					charValue = START;
					startState = new State(trow, tcol, 0);
					break;
				case 'G':
					charValue = GOAL;
					goalState = new State(trow, tcol, 0);
					break;
				case 'O':
					charValue = OPENSPACE;
					break;
				case 'I':
					charValue = ICYSURFACE;
					break;
				case 'H':
					charValue = HOLES;
					break;

				case '\r':
					continue;

				default:
					// if the char is '/n'
					trow++;
					tcol = 0;
					continue;
				}

				iceWorld[trow][tcol] = charValue;
				tcol++;
			}
			reader.close();

			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					stateList.add(i * cols + j, new State(i, j, 0));
				}
			}
			for (int i = 0; i < stateList.size(); i++) {
				for (int j = 0; j < actions.length; j++) {
					StateActionPair temp = new StateActionPair(
							stateList.get(i), actions[j]);
					Qvalue.put(temp, 0.0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Read the file by line, initial the rows and columns of the grid world
	 * */
	public void readFileByLine(String fileName) throws IOException {
		ArrayList fileContents = new ArrayList();
		String[] f;
		String inputLine = new String();

		BufferedReader bin = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(fileName))));
		while ((inputLine = bin.readLine()) != null) {
			fileContents.add(inputLine);
			rows++;
			cols = inputLine.length();
		}

		// initial the row and columns
		Qvalue = new HashMap<StateActionPair, Double>(rows * cols);
		iceWorld = new int[rows][cols];
		stateList = new ArrayList<State>(rows * cols);
	}

	/**
	 * Calculate the theta value by current episodes
	 * 
	 * @param e
	 *            current episodes value
	 */
	public double getEpsilon(int e, int type) {
		switch (type) {
		case 0:
			double temp = Math.floor(e / 10);
			if (e < 10)
				return EPSLION;
			else if (e >= 10 && e < 1000)
				return EPSLION / temp;
			else if (e >= 1000)
				return 0;
		case 1:
			temp = Math.ceil(e / 25);
			if (e < 25)
				return EPSLION;
			else if (e >= 25 && e < 2500)
				return EPSLION / Math.ceil(e / 25);
			else if (e >= 2500)
				return 0;

		case 2:
			temp = Math.ceil(e / 4);
			if (e < 4)
				return EPSLION;
			if (e >= 4 && e < 400)
				return EPSLION / Math.ceil(e / 4);
			else if (e >= 400)
				return 0;
		}
		return 0;

	}

	/**
	 * Get a random action
	 * 
	 * @return A random valid action.
	 */
	public Action getRandomAction() {
		int a = (int) (Math.random() * (4));
		return actions[a];
	}

	/**
	 * Find next state after taking the action for a particular state. It is
	 * state-transition function: P (s, a, s')
	 * 
	 * @param currentState
	 *            current state
	 * @param a
	 *            the action that states take
	 * @return the next state
	 * */
	public State getNextState(State currentState, Action a) {
		int row = currentState.row;
		int col = currentState.col;

		// on the icy surface, 0.8 possibility to move successfully,0.1 each for
		// other directions.
		if (iceWorld[row][col] != ICYSURFACE) {
			switch (a.index) {
			case ACTION_UP:
				row -= 1;
				break;
			case ACTION_LEFT:
				col -= 1;
				break;
			case ACTION_DOWN:
				row += 1;
				break;
			default:
				col += 1;
			}
		} else {
			// if now is on a ice surface, 0.8 possibility moves successfully
			double p = Math.random();
			if (p > 0.8 && p <= 0.9) {
				switch (a.index) {
				case ACTION_UP:
					row--;
					col--;
					break;
				case ACTION_LEFT:
					row++;
					col--;
					break;
				case ACTION_DOWN:
					row++;
					col++;
					break;
				default:
					row--;
					col++;
				}
			} else if (p > 0.9) {
				switch (a.index) {
				case ACTION_UP:
					row--;
					col++;
					break;
				case ACTION_LEFT:
					row--;
					col--;
					break;
				case ACTION_DOWN:
					row++;
					col--;
					break;
				default:
					row++;
					col++;
				}
			} else {
				switch (a.index) {
				case ACTION_UP:
					row -= 1;
					break;
				case ACTION_LEFT:
					col -= 1;
					break;
				case ACTION_DOWN:
					row += 1;
					break;
				default:
					col += 1;
				}
			}

		}
		State result = findState(row, col);
		if (result == null)
			return currentState;
		else
			return result;
	}

	public State findState(int row, int col) {
		if (row < 0 || row >= rows || col < 0 || col >= cols
				|| iceWorld[row][col] == HOLES)
			return null;
		else {
			// find corresponding state in the state list
			return stateList.get(row * cols + col);
		}
	}

	/**
	 * Implementation of Q Learning algorithm
	 * */
	public void QLearning(int type, int iteration) {
		System.out.println("Q learning output");

		while (episodes < iteration) {
			// print the policy result
			if (episodes % 8 == 0)
				tRew = 0;
			State curState = startState;
			int steps = 0;
			while (!curState.isEndState(goalState)) {
				if (getEpsilon(episodes, type) < 0.1
						&& steps >= 3 * stateList.size())
					break;
				// Under 1000 episodes, agent use theta-greedy policy
				Action action = gredilyAction(curState, type);
				curState.action = action;
				State nextState = getNextState(curState, action);

				int reward = reward(curState, action);
				double q = getQvalue(curState, action);
				double qPrime = maximumQ(nextState);
				double qUpdate = q + ALPHA * (reward + GAMMA * qPrime - q);
				putQvalue(curState, action, qUpdate);
				// get state index in the state list
				int aIndex = action.index;
				// calculate total reward
				tRew += reward;
				curState = nextState;
				steps++;
			}
			if (episodes % 8 == 0)
				tRewOutput += tRew + ",";
			episodes++;
		}
		setGredily();
		printPolicy();
		// try {
		//
		// outputReward(tRewOutput, path, qLearningFile);
		// outputTextfile(qLearnigOutput, path, qLearningFile);
		// } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	/**
	 * Sarsa learning algorithm
	 * */
	public void Sarsa(int type, int iteration) {
		while (episodes < iteration) {

			if (episodes % 8 == 0)
				tRew = 0;
			int steps = 0;
			State curState = startState;
			Action action = gredilyAction(curState, type);
			curState.action = action;

			while (!curState.isEndState(goalState)) {
				if (getEpsilon(episodes, type) < 0.1
						&& steps >= 3 * stateList.size())
					break;
				int reward = reward(curState, action);
				State nextState = getNextState(curState, action);

				// use theta- greedy policy to get the action prime
				Action actionPrime = gredilyAction(nextState, type);
				tRew += reward;

				// update the q value in the q table
				double q = getQvalue(curState, action);
				double qPrime = getQvalue(nextState, actionPrime);
				double qUpdate = q + ALPHA * (reward + GAMMA * qPrime - q);

				putQvalue(curState, action, qUpdate);
				action = actionPrime;
				nextState.action = actionPrime;
				curState = nextState;
				steps++;
			}
			if (episodes % 8 == 0)
				tRewOutput += tRew + ",";
			episodes++;
		}
		setGredily();
		printPolicy();
		// try {
		// outputReward(tRewOutput, path, sarsaFile);
		// outputTextfile(sarsaOutput, path, sarsaFile);
		// } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * Sarsa-lamuta learning algorithm
	 * */
	public void SarsaLambda(int type, int iteration) {
		System.out.println("SarsaLambda output");
		while (episodes < iteration) {
			if (episodes % 8 == 0)
				tRew = 0;
			int steps = 0;
			State curState = startState;
			Action action = getRandomAction();
			double max = -Double.MAX_VALUE;
			for (int i = 0; i < actions.length; i++) {
				// find the best action which has most q value
				double q = getQvalue(curState, actions[i]);
				if (q > max) {
					max = q;
					action = actions[i];
				}
			}
			while (!curState.isEndState(goalState)) {
				if (getEpsilon(episodes, type) < 0.1
						&& steps >= 3 * stateList.size())
					break;
				State nextState = getNextState(curState, action);
				Action actionPrime = gredilyAction(nextState, type);
				int reward = reward(curState, action);

				double q = getQvalue(curState, action);
				double qPrime = getQvalue(nextState, actionPrime);
				double delta = reward + GAMMA * qPrime - q;

				double newE = 0;
				int eIndex = getEIndex(new QueueUnit(curState, action, 0));
				// Add the new e value to the list, if it already exists, update
				// the value, if not, add it to the list.Initial value is 1
				if (eIndex != -1) {
					newE = trace.get(eIndex).getValue() + 1;
					trace.get(eIndex).setValue(newE);
				} else {
					newE = 1;
					trace.add(new QueueUnit(curState, action, newE));
				}

				// Store only 10 traced states.
				Collections.sort(trace);
				if (trace.size() == 11)
					trace.remove(0);

				// update q value and e value for every state action pair in the
				// trace list
				for (int i = 0; i < trace.size(); i++) {
					QueueUnit qu = trace.get(i);
					State s = qu.getState();
					Action a = qu.getAction();
					double qTemp = getQvalue(s, a);
					double updateQ = qTemp + ALPHA * delta * qu.getValue();
					putQvalue(s, a, updateQ);
					qu.setValue(qu.getValue() * GAMMA * LAMBDA);
				}

				tRew += reward;
				action = actionPrime;
				curState = nextState;
				curState.action = action;
				steps++;

			}
			if (episodes % 8 == 0)
				tRewOutput += tRew + ",";
			episodes++;
		}
		setGredily();
		printPolicy();
		// try {
		// outputReward(tRewOutput, path, sarsaLFile);
		// outputTextfile(sarsaOutput, path, sarsaFile);
		// } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * Get q value in the map Q
	 * 
	 * @param s
	 *            state of the q
	 * @param a
	 *            action of the q
	 * @return q value of state s and action a pair
	 * */
	public double getQvalue(State s, Action a) {
		StateActionPair temp = new StateActionPair(s, a);
		return Qvalue.get(temp);
	}

	/**
	 * Set q value in the map Q
	 * 
	 * @param s
	 *            state of the q
	 * @param a
	 *            action of the q
	 * @param value
	 *            value of the state action pair
	 * */
	public void putQvalue(State s, Action a, double value) {
		StateActionPair temp = new StateActionPair(s, a);
		Qvalue.put(temp, value);
	}

	// reset the value of variable for next algorithm runs
	public void reset() {
		episodes = 0;
		tRew = 0;
		tRewOutput = "";
		for (int i = 0; i < stateList.size(); i++) {
			for (int j = 0; j < actions.length; j++) {
				StateActionPair temp = new StateActionPair(stateList.get(i),
						actions[j]);
				Qvalue.put(temp, 0.0);
			}
		}
		System.out.println();
		// System.out.println("Sarsa ouput");
	}

	/**
	 * If episodes is less than 1000,than compare a random number between 0~1,
	 * if random number less than theta, then generate an random action, if it
	 * is greater than theta, then find the best action; If episodes is greater
	 * than 1000, always find the best action
	 * 
	 * @param s
	 *            the state which needs to generate an action for
	 * @return an action
	 */
	public Action gredilyAction(State s, int type) {
		Action action = getRandomAction();
		double e = Math.random();
		double theta = getEpsilon(episodes, type);
		if (e <= theta) {
			Action a = getRandomAction();
			return a;
		} else {
			// choose greedy action
			double max = -Double.MAX_VALUE;
			for (int i = 0; i < actions.length; i++) {
				// find the best action which has most q value
				double q = getQvalue(s, actions[i]);
				if (q > max) {
					max = q;
					action = actions[i];
				}
			}
			// s.action=action;
			return action;
		}
	}

	/**
	 * Find the maximum Q value of a state. Compare the q value of four actions
	 * of that state.
	 * 
	 * @param nextState
	 *            the state needs to find maximum q value of
	 * @return maximum q value
	 * */
	public double maximumQ(State nextState) {
		double maxQ = -Double.MAX_VALUE;
		for (Action a : actions) {
			double tempQ = getQvalue(nextState, a);
			if (tempQ > maxQ)
				maxQ = tempQ;
		}
		// TODO Auto-generated method stub
		return maxQ;
	}

	/**
	 * Get reward for a state and action pair
	 * 
	 * @param currentState
	 *            current state
	 * @param a
	 *            action that this state takes
	 * */
	public int reward(State currentState, Action a) {
		int row = currentState.row;
		int col = currentState.col;
		if (a != null) {
			switch (a.index) {
			case ACTION_UP:
				row -= 1;
				break;
			case ACTION_LEFT:
				col -= 1;
				break;
			case ACTION_DOWN:
				row += 1;
				break;
			case ACTION_RIGHT:
				col += 1;
			default:
				break;
			}
		}

		// if hit the wall or reach the maze's boundary or drops in the hole,
		// return state it
		// self
		if (row < 0 || row >= rows || col < 0 || col >= cols) {
			return reward(currentState, null);
		} else if (iceWorld[row][col] == HOLES) {
			return R_HOLE;
		} else if (iceWorld[row][col] == GOAL)
			return R_GOAL;
		return R_OPEN;
	}

	/**
	 * Print out the policy into text file. This method is not called in this
	 * program
	 * */
	public String printResult() {
		String data = "";
		data += "Episode: " + episodes;
		data += "\r\n";
		data += "\r\n";
		for (int i = 0; i < iceWorld.length; i++) {
			for (int j = 0; j < iceWorld[i].length; j++) {
				if (iceWorld[i][j] == HOLES)
					data += "H";
				else if (iceWorld[i][j] == GOAL)
					data += "G";
				else if (iceWorld[i][j] == START)
					data += "S";
				else {
					if (stateList.get(i * rows + j).action == null)
						data += "U";
					else
						data += stateList.get(i * rows + j).action.toString();
				}
			}
			data += "\r\n";
		}
		data += "\r\n";
		return data;
	}

	/**
	 * Print out the policy into text file. This method is not called in this
	 * program
	 * */
	public void outputTextfile(String data, String path, String fileName)
			throws FileNotFoundException

	{
		FileOutputStream fs = new FileOutputStream(new File(path + fileName
				+ ".txt"));
		PrintStream p = new PrintStream(fs);
		p.print(data);
		p.close();
	}

	/**
	 * Print out the policy into text file. This method is not called in this
	 * program
	 * */
	public void outputReward(String data, String path, String fileName)
			throws FileNotFoundException

	{
		FileOutputStream fs = new FileOutputStream(new File(path + fileName
				+ ".txt"));
		PrintStream p = new PrintStream(fs);
		p.print(data);

		p.close();
	}

	/**
	 * Print out the current policy
	 * */
	public void printPolicy() {

		System.out.println("Episode: " + episodes + "\n");
		System.out.println();

		for (int k = 0; k < stateList.size(); k++) {
			if (k != 0 && k % cols == 0)
				System.out.print("\n");
			int i = k / cols;
			int j = k % cols;
			if (iceWorld[i][j] == HOLES)
				System.out.print("H");
			else if (iceWorld[i][j] == GOAL)
				System.out.print("G");
			else if (iceWorld[i][j] == START)
				System.out.print("S");
			else {
				if (stateList.get(k).action == null)
					System.out.print("?");
				else
					System.out.print(stateList.get(k).action.toString());
			}

		}

	}

	/**
	 * Set the greedy policy for every state
	 * */
	public void setGredily() {
		for (int i = 0; i < stateList.size(); i++) {
			State s = stateList.get(i);
			double max = -Double.MAX_VALUE;
			for (int j = 0; j < actions.length; j++) {
				double q = getQvalue(s, actions[j]);
				if (q > max) {
					max = q;
					s.action = actions[j];
				}
			}
		}
	}

	/**
	 * Find index of queueUnit in the trace list.If it doesnt exist, return -1
	 * 
	 * @param q
	 *            the queueUnity that finds index for
	 * */
	public int getEIndex(QueueUnit q) {
		int index = -1;
		for (int i = 0; i < trace.size(); i++) {
			if (trace.get(i).equals(q))
				return i;
		}
		return index;
	}

}
