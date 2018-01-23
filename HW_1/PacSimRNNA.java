// University of Central Florida
// CAP4630 - Spring 2018
// Authors: Nhi Nguyen and Alexander DeCurnou

import java.awt.Point;
import java.util.ArrayList;
import java.util.*;
import java.util.List;
import pacsim.BFSPath;
import pacsim.PacAction;
import pacsim.PacCell;
import pacsim.PacFace;
import pacsim.PacSim;
import pacsim.PacUtils;
import pacsim.PacmanCell;

class Population
{
	public List<Point> pcPath;
	public List<Integer> cost;
	public int[][] table;
	public int total;

	public Population(int size)
	{
		pcPath = new ArrayList<Point>();
		cost = new ArrayList<Integer>();
		table = new int[size][size];
		total = 0;
	}
}

public class PacSimRNNA implements PacAction 
{
	// method variables
	private List<Point> path;
	private List<Point> food;
	private int[][] cost;
	private int simTime;
	private int lowest;

	public PacSimRNNA(String fname)
	{
		PacSim sim = new PacSim(fname);
		sim.init(this);
	}

	public static void main(String[] args)
	{
		System.out.println("\nTSP using Repetitive Nearest Neighbor Algorithm by Nhi Nguyen and Alexander DeCurnou:");
		System.out.println("\nMaze : " + args[0] + "\n");
		new PacSimRNNA(args[0]);
	}

	@Override
	public void init()
	{
		simTime = 0;
		path = new ArrayList();
		food = new ArrayList<Point>();
	}

	@Override
	public PacFace action(Object state)
	{
		long start, end, time;
		List<Point> solution, inBetween;
		PacCell[][] grid = (PacCell[][]) state;
		PacmanCell pc = PacUtils.findPacman(grid);

		if(pc == null) return null;

		if(path.isEmpty())
		{	
			// load food array with (x, y) locations of all food dots
			// in initial configuration
			for(Point pellet : PacUtils.findFood(grid))
				food.add(new Point(pellet.x, pellet.y));

			// determine size of cost table
			cost = new int[PacUtils.numFood(grid) + 1][PacUtils.numFood(grid) + 1];
			cost = makeCostTable(pc, grid, cost);

			// output contents of food array to console
			System.out.println("\nFood Array:\n");
			for(int i = 0; i < PacUtils.numFood(grid); i++)
				System.out.println(i + " : (" + food.get(i).x + ", " + food.get(i).y + ")");

			start = System.currentTimeMillis();

			// generate RNNA plan
			solution = makePopulation(pc, grid, cost, food);

			end = System.currentTimeMillis();

			time = end - start;

			System.out.println("Time to generate plan: " + time + " msec");
			System.out.println("\nSolution moves:\n");

			// form path for pacman
			// add BFS path from starting position to first pellet
			inBetween = BFSPath.getPath(grid, pc.getLoc(), solution.get(0));
			for(int i = 0; i < inBetween.size(); i++)
				path.add(inBetween.get(i));

			// generate the complete path to eat all food pellets
			for(int i = 0; i < solution.size() - 1; i++)
			{
				// generate BFS path to each point
				inBetween = BFSPath.getPath(grid, solution.get(i), solution.get(i + 1));

				// add BFS path to solution path
				for(int j = 0; j < inBetween.size(); j++)
					path.add(inBetween.get(j));
			}
		}

		// solution path is found
      	Point next = path.remove(0);
     	PacFace face = PacUtils.direction(pc.getLoc(), next);
      	System.out.printf( "%5d : From [ %2d, %2d ] go %s%n", 
            ++simTime, pc.getLoc().x, pc.getLoc().y, face );
      	return face;
	} 

	public int[][] makeCostTable(PacmanCell pc, PacCell[][] grid, int[][] cost)
	{
		// output to console
		System.out.println("Cost table:\n");

		// load cost table with UCS distances from initial position
		// to each food dot in the initial configuration
		// determined using BFSPath
		for(int i = 0; i < cost.length; i++)
		{
			System.out.print("\t");

			for(int j = 0; j < cost.length; j++)
			{
				if(i == 0)
				{
					if(j == 0)
						cost[i][j] = 0;
					else
						cost[i][j] = BFSPath.getPath(grid, pc.getLoc(), food.get(j - 1)).size();
				}

				else if(j == 0)
					cost[i][j] = BFSPath.getPath(grid, pc.getLoc(), food.get(i - 1)).size();

				else
					cost[i][j] = BFSPath.getPath(grid, food.get(i - 1), food.get(j - 1)).size();

				// output cost table to console
				System.out.print(cost[i][j] < 10 ? " " + cost[i][j] + "  " : cost[i][j] + "  ");

			}

			System.out.println();
		}

		return cost;
	}

	public List<Point> makePopulation(PacmanCell pc, PacCell[][] grid, int[][] cost, List<Point> food)
	{
		// create path class
		List<Population> population = new ArrayList<Population>();

		List<Point> temp = new ArrayList<Point>();
		List<Point> neighbors = new ArrayList<Point>();
		int pop = 0;

		// n food dot amount of steps
		for(int i = 0; i < food.size(); i++)
		{
			// output step number to console
			System.out.println("\nPopulation at step " + (i + 1) + " :");

			// in the first step, n candidate solution paths are available
			// allocate space for all n paths
			if(i == 0)
			{
				for(int j = 0; j < food.size(); j++)
				{
					population.add(new Population(food.size() + 1));

					// load each new path with relevant details
					// initialize each list of paths
					population.get(j).pcPath.add(food.get(j));

					// add cost associated with path
					population.get(j).cost.add(cost[i][j + 1]);

					temp = PacUtils.clonePointList(food);
					temp.remove(food.get(j));

					// update each path's cost table
					population.get(j).table = copy2D(cost);

					// set current total cost
					population.get(j).total = cost[i][j + 1];
				}
			}

			else
			{
				// since population size might change in this loop, make sure size is static
				pop = population.size();
				for(int j = 0; j < pop; j++)
				{
					// find nearest neighbor; if more than one exists, branch out paths
					neighbors = nearestNeighbors(pc, grid, population.get(j).table, population.get(j).pcPath, population.get(j).pcPath.get(i - 1));

					if(neighbors.size() > 1)
					{
						// duplicate original path and branch out
						for(int k = 1; k < neighbors.size(); k++)
						{
							// add new path to list of potential solution paths
							population.add(new Population(food.size() + 1));

							// duplicate duplicate
							population.get(population.size() - 1).pcPath = PacUtils.clonePointList(population.get(j).pcPath);
							population.get(population.size() - 1).cost.addAll(population.get(j).cost);
							population.get(population.size() - 1).table = copy2D(population.get(j).table);
							population.get(population.size() - 1).total = population.get(j).total;

							// update other path info
							population.get(population.size() - 1).table = clearRC(population.get(population.size() - 1).table, food.indexOf(population.get(j).pcPath.get(i - 1)) + 1);
							population.get(population.size() - 1).pcPath.add(neighbors.get(k));
							population.get(population.size() - 1).cost.add(lowest);
							population.get(population.size() - 1).total += lowest;
						}

						// add first neighbor to original path
						population.get(j).table = clearRC(population.get(j).table, food.indexOf(population.get(j).pcPath.get(i - 1)) + 1);
						population.get(j).pcPath.add(neighbors.get(0));
						// update other path info
						population.get(j).cost.add(lowest);
						population.get(j).total += lowest;
					}

					// otherwise, if only one neighbor exists, add to current path
					else
					{
						population.get(j).table = clearRC(population.get(j).table, food.indexOf(population.get(j).pcPath.get(i - 1)) + 1);
						population.get(j).pcPath.add(neighbors.get(0));

						// update other path info
						population.get(j).cost.add(lowest);
						population.get(j).total += lowest;

					}
				}
			}

			// sort the new path from lowest to greatest cost
			// create a Comparator class to sort from ascending total costs
			Collections.sort(population, new Comparator<Population>()
			{
        		public int compare(Population c1, Population c2) 
        		{
            		return c1.total < c2.total ? -1 : c1.total == c2.total ? 0 : 1;
        		}
   			});

			// output population at step i
			for(int j = 0; j < population.size(); j++)
			{
				System.out.print("\t" + j + " : cost=" + population.get(j).total + " :");
				
				for(int k = 0; k <= i; k++)
				{
					System.out.print(" [(" + population.get(j).pcPath.get(k).x + "," +
									 population.get(j).pcPath.get(k).y + ") ," +
									 population.get(j).cost.get(k) + "]");
				}

				System.out.println();
			}	
		}

		return population.get(0).pcPath;
	}

	public ArrayList<Point> nearestNeighbors(PacmanCell pc, PacCell[][] grid, int[][] table, List<Point> pcPath, Point currentLoc)
	{
		// create a list of nearest neighbors
		ArrayList<Point> neighbors = new ArrayList<Point>();
		int index = food.indexOf(currentLoc);

		// find nearest neighbors, or minimum cost paths
		lowest = min(table, index + 1);

		while(neighbors.size() == 0)
		{
			// search cost table for nearest neighbor(s)
			for(int j = 1; j < table.length; j++)
			{
				if(table[index + 1][j] == lowest && !pcPath.contains(food.get(j - 1)))
					neighbors.add(food.get(j - 1));
			}

		}

		return neighbors;
	}

	public int min(int[][] cost, int row)
	{
		int min = Integer.MAX_VALUE;

		for(int i = 1; i < cost.length; i++)
			min = cost[row][i] < min && cost[row][i] > 0? cost[row][i] : min;

		return min;
	}

	public int frequency(int[][] cost, int row, int lowest)
	{
		int frequency = -1;

		// loop through row for multiple nearest neighbors
		for(int i = 1; i < cost.length; i++)
		{
			if(cost[row][i] == lowest)
				frequency++;
		}

		return frequency;
	}

	public int[][] copy2D(int[][] cost)
	{
		int[][] temp = new int[cost.length][cost.length];

		for(int i = 0; i < temp.length; i++)
		{
			for(int j = 0; j < temp.length; j++)
				temp[i][j] = cost[i][j];
		}

		return temp;
	}

	public int[][] clearRC(int[][] table, int rc)
	{
		for(int i = 0; i < table.length; i++)
		{
			table[rc][i] = 0;
			table[i][rc] = 0;
		} 

		return table;
	}

}
