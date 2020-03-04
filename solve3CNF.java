/*
 * solve3CNF.java
 * IT328 Project 1
 * Student A - Katie Sherman
 * Student B - Sunny Singh
*/

// Import libraries
import java.io.*;
import java.lang.Math;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class solve3CNF {
	// Graph properites
	public static int graphCount;
	public static int graphSize;
	public static int[][] edges;
	public static int[] maxClique;
	public static int maxK;
	public static int numLiterals;
	public static ArrayList<int[]> clusters;

	// CPU time
	public static long cpuTime;
	public static long timeLimit = -1;
	public static boolean foundSolution = false;

	public static void main(String[] args) {
		// Gather input from args
		String inputFile = args[0];

		// Create a Scanner
		Scanner mainScanner = null;

		// Try to open the file
		try {
			// Open the file
			mainScanner = new Scanner(new File(inputFile));
		} catch (FileNotFoundException e) {
			// Feedback and exit
			System.out.println("Unable to locate or open the file with name: " + inputFile);
			System.exit(0);
		}

		// Print the header
		printHeader(inputFile);

		// Initialize graph properties
		graphSize = 0;
		graphCount = 0;

		// Initialize variables to read file
		String readLine = "";
		Scanner readTokens;
		boolean firstFlag = false;

		// Go through each line
		while (mainScanner.hasNext()) {
			// Get the full line
			readLine = mainScanner.nextLine();

			// Variables for number of literals in 3-SAT
			numLiterals = 0;
			boolean numLiteralFlag = false;

			// Variables for k-clusters
			int kClusters = 0;
			int clusterCounter = 1;
			int[] cluster = new int[3];
			clusters = new ArrayList<int[]>();

			// Get integers
			readTokens = new Scanner(readLine);

			// Read all integers
			while(readTokens.hasNext()) {
				// Get integer
				int integerRead = readTokens.nextInt();

				// Set number of literals in the 3-SAT
				if (numLiteralFlag == false) {
					if (integerRead == 0) {
						System.out.println("***");
						return;
					}
					numLiterals = integerRead;
					numLiteralFlag = true;
				} else {
					// Check if new cluster
					if (clusterCounter % 4 == 0) {
						// Increment 'k'
						kClusters++;

						// Add current cluster to cluster
						clusters.add(cluster);

						// Reset cluster and cluster counter
						cluster = new int[3];
						clusterCounter = 1;
					}

					// Add to cluster
					cluster[clusterCounter - 1] = integerRead;

					// Increment cluster counter
					clusterCounter++;
				}
			}

			// Close the Scanner
			readTokens.close();

			// Add final cluster to clusters
			kClusters++;
			clusters.add(cluster);

			// Add up nodes
			int nodes = kClusters * 3;

			// Construct graph for clique problem
			edges = new int[nodes][nodes];
			for (int i = 0; i < nodes; i++) {
				for (int j = 0; j < nodes; j++) {
					// Check to see if within same cluster group
					int iGroup = (int) Math.floor(i / 3);
					int jGroup = (int) Math.floor(j / 3);
					if (iGroup != jGroup) {
						// Next, check to see if they're not negations
						int iNode = clusters.get(iGroup)[i % 3];
						int jNode = clusters.get(jGroup)[j % 3];
						if (iNode + jNode != 0) {
							edges[i][j] = 1;
						} else {
							edges[i][j] = 0;
						}
					} else {
						edges[i][j] = 0;
					}
				}
			}

			// Update graph count
			graphCount++;

			// Set maxK to 'k'
			maxK = kClusters;
			maxClique = new int[nodes];
			graphSize = nodes;

			// Check to see if something bigger than 'k'
			findMaxClique();

			// Print the max clique for this specific graph
			printMaxClique();
		}

		// Close scanner
		mainScanner.close();
	}

	public static void findMaxClique() {
		// Begin timer for CPU
		cpuTime = System.currentTimeMillis();

		// Create two lists for vertices
		ArrayList<Integer> currentClique = new ArrayList<Integer>();
		ArrayList<Integer> toCheck = new ArrayList<Integer>(graphSize);

		// Add all vertices to unchecked list
		for (int i = 0; i < graphSize; i++) {
			toCheck.add(i);
		}

    	// Recursively find largest clique
		increaseClique(currentClique, toCheck);
	}

	public static void increaseClique(ArrayList<Integer> currentClique, ArrayList<Integer> toCheck) {
    	// Go through each node
		for (int i = toCheck.size() - 1; i >= 0; i--) {
      		// If there are not enough vertices left to create
      		// a clique larger than the current one, then exit method call
			if (currentClique.size() + toCheck.size() <= maxK)
			{
				return;
			}

      		// Apply next vertex to check to current clique list
			int vertex = toCheck.get(i);
			currentClique.add(vertex);

      		// Create a new list of next connected edges
			ArrayList<Integer> newToCheck = new ArrayList<Integer>(i);
			for (int j = 0; j <= i; j++) {
				// Get next vertex
				int nextVertex = toCheck.get(j);

				// Check if there is an edge between
				if (edges[vertex][nextVertex] == 1) {
					newToCheck.add(nextVertex);
				}
			}

			// When all vertices are checked and the current clique
			// is larger than the pervious maxK, save vertex list
			if (newToCheck.isEmpty() && currentClique.size() >= maxK) {
				foundSolution = true;
				saveSolution(currentClique);
				return;
			}

      		// If the new to check list has more connected edges,
			// recursively check to see if any more could join the current clique
			if (!newToCheck.isEmpty()) {
				increaseClique(currentClique, newToCheck);
			}

      		// Remove the value you were checking back out of the clique again
      		if (foundSolution != true) {
				currentClique.remove(currentClique.size()-1);
				toCheck.remove(i);
			} else {
				break;
			}
		}
	}

	public static void saveSolution(ArrayList<Integer> currentClique) {
		// Reset max clique
		for(int i=0; i<maxClique.length; i++)
		{
			maxClique[i] = 0;
		}

		for (int i : currentClique)
		{
			maxClique[i] = 1;
		}
	}

	public static void printMaxClique() {
		System.out.print("3CNF No." + graphCount + ": [n=" + numLiterals + " k=" + maxK + "] ");

		Map<Integer, Integer> literalMap = new HashMap<Integer, Integer>();

    	// Output found clique data
    	if (foundSolution == true) {
	    	// Get which literals need to be true or false
			for (int i = 0; i < graphSize ; i++) {
				// Get group
				int clusterGroup = (int) Math.floor(i / 3);

				// Get literal
				int literal = clusters.get(clusterGroup)[i % 3];
				

				// Check if it was in clique
				if (maxClique[i] == 1) {
					// Check if literal was positive or negative
					if (literal > 0) {
						literalMap.put(literal, 1);
					} else {
						literalMap.put(-literal, 0);
					}
				}
			}

			// Print out whether T, F or X for each literal
			for (int i = 1; i <= numLiterals; i++) {
				if (literalMap.containsKey(i)) {
					int val = literalMap.get(i);
					if (val == 1) {
						if (i != numLiterals) {
							System.out.print("T, ");
						} else {
							System.out.print("T ");
						}
					} else {
						if (i != numLiterals) {
							System.out.print("F, ");
						} else {
							System.out.print("F ");
						}
					}
				} else {
					if (i != numLiterals) {
						System.out.print("X, ");
					} else {
						System.out.print("X ");
					}
				}
			}

			System.out.println("(" + (System.currentTimeMillis() - cpuTime) + " ms)");
		} else {
			System.out.print("No " + maxK + "-clique; no solution ");
			System.out.println("(" + (System.currentTimeMillis() - cpuTime) + " ms)");
		}

		// Reset
		foundSolution = false;
	}

	public static void printHeader(String inputFile) {
		System.out.println("* Solve 3CNF in " + inputFile + ": (reduced to K-Clique) *");
		System.out.println("   x: can be either T or F");
	}
}