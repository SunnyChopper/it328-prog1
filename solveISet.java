/*
 * solveClique.java
 * IT328 Project 1
 * Student A - Katie Sherman
 * Student B - Sunny Singh
*/

// Import libraries
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class solveISet {
	// Graph properites
	public static int graphCount;
	public static int graphSize;
	public static int totalEdges;
	public static int[][] edges;
	public static int[] degree;
	public static int[] maxClique;
	public static int maxK;

	// CPU time
	public static long cpuTime;
	public static long timeLimit = -1;

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
		int temp;

		// Loop to read graphs from file
		while (mainScanner.hasNext()) {
			// Reset graph variables
			temp = 0;
			totalEdges = 0;

			// Get the full line and store graph size
			readLine = mainScanner.nextLine();
			graphSize = Integer.parseInt(readLine);

			// Check if graph size is zero, which means the end
			if (graphSize == 0) {
				System.out.println("***");
				return;
			}

			// Increment the number of graphs we have
			graphCount++;

			// Reset graph property variables to match graph size
			edges = new int[graphSize][graphSize];
			degree = new int[graphSize];
			maxClique = new int[graphSize];
			maxK = 0;

			// Read line into 2D array
			for (int i = 0; i < graphSize; i++) {
				// Get next line
				readLine = mainScanner.nextLine();
				readTokens = new Scanner(readLine);

				for (int j = 0; j < graphSize; j++) {
					// Get the next integer in the line
					temp = readTokens.nextInt();

					// Inverse the number to get complementary graph
					if (temp == 1) {
						temp = 0;
					} else {
						temp = 1;
					}

          			// Ignore self-connected edges
					if (i != j)
					{
						edges[i][j] = temp;
						totalEdges += temp;
						degree[i] += temp;
					}
				}

				// Close the Scanner
				readTokens.close();
			}

			// Account for double-counted (symmetrical) edges
			totalEdges = totalEdges / 2;

			// Find the max clique for this graph
			findMaxClique();

			// Print the max clique for this specific graph
			printMaxClique();
		}

		// Close the main scanner
		mainScanner.close();
	}

	/*
	 *
	 *	Method from solveClique.java
	 *
	 */
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

	/*
	 *
	 *	Method from solveClique.java
	 *
	 */
	public static void increaseClique(ArrayList<Integer> currentClique, ArrayList<Integer> toCheck) {
    	// Begin at end of toCheck vertices
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
			if (newToCheck.isEmpty() && currentClique.size() > maxK) {
				saveSolution(currentClique);
			}

      		// If the new to check list has more connected edges,
			// recursively check to see if any more could join the current clique
			if (!newToCheck.isEmpty()) {
				increaseClique(currentClique, newToCheck);
			}

      		// Remove the value you were checking back out of the clique again
			currentClique.remove(currentClique.size()-1);
			toCheck.remove(i);
		}
	}

	/*
	 *
	 *	Method from solveClique.java
	 *
	 */
	public static void saveSolution(ArrayList<Integer> currentClique) {
		for(int i=0; i<maxClique.length; i++)
		{
			maxClique[i] = 0;
		}
		for (int i : currentClique)
		{
			maxClique[i] = 1;
			maxK = currentClique.size();
		}
	}

	/*
	 *
	 *	Method from solveClique.java
	 *
	 */
	public static void printMaxClique() {
		System.out.print("G" + graphCount + " (" + graphSize + ", " + totalEdges + ") ");

    	// Output found clique data
		System.out.print("{");
		int counter = 0;

    	// Print vertices from list contained in clique
		for (int i=0; i < graphSize ; i++) {
			if (maxClique[i] == 1) {
				System.out.print(i);

        		// Output commas after all but final value
				if (counter < (maxK - 1)) {
					System.out.print(", ");
					counter++;
				}
			}
		}
		System.out.print("} (size=" + maxK + ", ");
		System.out.println((System.currentTimeMillis() - cpuTime) + " ms)");
	}

	public static void printHeader(String inputFile) {
		System.out.println("* Max Independent Sets in graphs in " + inputFile + " : (reduced to K-Clique) *");
		System.out.println("   (|V|,|E|) Independent Set (size, ms used)");
	}

}