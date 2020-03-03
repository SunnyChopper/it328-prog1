/*
 * solveClique.java
 * IT328 Project 1
 * Student A - Katie Sherman
 * Student B - Sunny Singh
*/

//import statements
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

public class solveClique
{
  static int[] degree;
  static int[][] edges;
  static int graphSize;
  static long cpuTime;
  static long timeLimit = -1;
  static int maxK;
  static int[] maxClique;

  public static void main(String[] args)
  {
    //Gather input from args
    String inputFile = args[0];

    Scanner scan = null;
    try
    {
      scan = new Scanner(new File(inputFile));
    }
    catch (FileNotFoundException e)
    {
      System.out.println("Unable to open file " + inputFile);
    }

    System.out.println("* Max Cliques in " + inputFile);
    System.out.println("   (|V|,|E|) Cliques (size, ms used)");

    graphSize = 0;
    int graphCount = 0;

    String readLine = "";
    Scanner readTokens;
    int temp;
    int totalEdges;

    //Loop to read graphs from file
    while (scan.hasNext())
    {
      //reset values for new graph
      temp = 0;
      totalEdges = 0;
      //retreive first line, contining graph size
      readLine = scan.nextLine();
      graphSize = Integer.parseInt(readLine);
      //file ends with a graph size of 0
      if (graphSize == 0)
      {
        System.out.println("***");
        return;
      }
      //update graph number for printing
      graphCount++;
      //resize and reset arrays
      edges = new int[graphSize][graphSize];
      degree = new int[graphSize];
      maxClique = new int[graphSize];
      maxK = 0;

      //read text file into 'edges' 2D array
      for (int i=0; i < graphSize; i++)
      {
        readLine = scan.nextLine();
        readTokens = new Scanner(readLine);

        for (int j=0; j<graphSize; j++)
        {
          temp = readTokens.nextInt();
          //ignore self-connected edges
          if (i != j)
          {
            edges[i][j] = temp;
            totalEdges += temp;
            degree[i] += temp;
          }
        }
        readTokens.close();
      }

      //account for double-counted (symetrical) edges
      totalEdges = totalEdges / 2;

      System.out.print("G" + graphCount
        + " (" + graphSize + ", " + totalEdges + ") ");

      //begin timer
      cpuTime = System.currentTimeMillis();
      //Create two lists for vertices
      ArrayList<Integer> currentClique = new ArrayList<Integer>();
      ArrayList<Integer> toCheck = new ArrayList<Integer>(graphSize);
      //add all vertices to unchecked list
      for (int i=0; i<graphSize; i++)
      {
        toCheck.add(i);
      }
      //recursively find largest clique
      increaseClique(currentClique,toCheck);

      //Output found clique data
      System.out.print("{");
      int counter = 0;
      //Print vertices from list contained in clique
      for (int i=0; i < graphSize; i++)
      {
        if (maxClique[i] == 1)
        {
          System.out.print(i);
          //Output commas after all but final value
          if (counter < (maxK - 1))
          {
            System.out.print(", ");
            counter++;
          }
        }
      }
      System.out.print("} (size=" + maxK + ", ");
      System.out.println((System.currentTimeMillis() - cpuTime) + " ms)");

    }
    //Close scanner
    scan.close();
  }

  public static void increaseClique(ArrayList<Integer> currentClique, ArrayList<Integer> toCheck)
  {
    //begin at end of toCheck vertices
    for (int i=toCheck.size()-1; i >= 0; i--)
    {
      //if there are not enough vertices left to create
      // a clique larger than the current one, then exit method call
      if (currentClique.size() + toCheck.size() <= maxK)
      {
        return;
      }
      //apply next vertex to check to current clique list
      int vertex = toCheck.get(i);
      currentClique.add(vertex);

      ArrayList<Integer> newToCheck = new ArrayList<Integer>(i);
      for (int j=0; j<=i; j++)
      {
        int nextVertex = toCheck.get(j);
        if (edges[vertex][nextVertex] == 1)
        {
          newToCheck.add(nextVertex);
        }
      }
      if (newToCheck.isEmpty() && currentClique.size() > maxK)
      {
        saveSolution(currentClique);
      }
      if (!newToCheck.isEmpty())
      {
        increaseClique(currentClique, newToCheck);
      }
      currentClique.remove(currentClique.size()-1);
      toCheck.remove(i);
    }
  }

  public static void saveSolution(ArrayList<Integer> currentClique)
  {
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

}
