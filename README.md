# Reinforcement Learning experiments 
Compared the performance of SARSA and Q-learning, and SARSA-λ 

#Domain
(iceWorld.txt)this file describes a grid-world environment consisting of: 
A starting state, marked by an S. • A goal state, marked by a G. • A set of states consisting of open space, marked by an O. 
A set of holes, marked by an H. 
An agent can move in one of four directions—up, down, left, right—by a single grid-square.
If the agent is in either the start state or in open space, it can move in any of the four directions deterministically,
except that it cannot leave the grid. Any move that attempts to move off the grid will cause the agent to stay exactly where
it is. The goal location functions as an absorbing state, so that once the agent has entered that state, any movement has no
effect, and they remain at the goal.

An episode for learning is defined as follows:
If the randomness parameter ε ≥ 0.1, then an episode is any number of moves that take the agent from the start state to the 
goal.
If ε < 0.1, then an episode terminates when the agent either reaches the goal, or after the agent has taken 3 |S| steps, 
where |S| is the size of the state-space. (This will allow agents to escape non-optimal looping policies, but also allow 
for some longer paths involving slips on the ice or falling into holes.)


#Effects of reducing the randomness factor ε at various rates: 
Normal reduction schedule: Each algorithm will do 2, 000 episodes of learning, 	updating ε every 10 episodes as
before: if E is the number of episodes already past, 	then for all values E >= 10, we set ε = 0.9/⌊E/10⌋. This means 
that after 1,000 	episodes, ε = 0.009; at this point, it is set to 0, and the agent acts greedily for the last 	1, 000 
episodes, while still updating values.  

Slow reduction schedule: Each algorithm does 5,000 episodes of learning, reducing randomness every 25 episodes, 
so that we reach ε = 0.0009 after 2, 500 episodes, and then set it to 0 and proceed greedily for the last 2, 500 episodes.  
	
Rapid reduction schedule: Each algorithm does 800 episodes of learning, reducing randomness every 4 episodes, 
so that we reach ε = 0.0009 after 400 episodes, and then set it to 0 and proceed greedily for the last 400 episodes. 

#Documentation
Results from the experiment, including graph and table explain it explicitly. 
