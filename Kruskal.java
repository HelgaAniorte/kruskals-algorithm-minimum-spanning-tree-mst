import java.util.*;
import java.io.*;

public class Kruskal {
	private int nodeCount;	//how many nodes. NODE COUNT MUST BE ENTERED MANUALLY. No error handling between nodeCount and graphEdges
	private ArrayList<Edge> graphEdges;		//edge list, not adjacency list

	public static void main(String[] args) {
		Kruskal graph = new Kruskal();
		graph.kruskalMST();				//run Kruskal's algorithm to find a MST
	}

	public Kruskal(){
		graphEdges=new ArrayList<Edge>();
		graphEdges.add(new Edge(0, 0, 0));		//dummy edge to ignore 0th position in ArrayList
		graphEdges.add(new Edge(3, 5, 2));
		graphEdges.add(new Edge(6, 7, 5));
		graphEdges.add(new Edge(3, 4, 6));
		graphEdges.add(new Edge(4, 8, 7));
		graphEdges.add(new Edge(1, 2, 9));
		graphEdges.add(new Edge(4, 5, 11));
		graphEdges.add(new Edge(1, 6, 14));
		graphEdges.add(new Edge(1, 7, 15));
		graphEdges.add(new Edge(5, 8, 16));
		graphEdges.add(new Edge(3, 6, 18));
		graphEdges.add(new Edge(3, 8, 19));
		graphEdges.add(new Edge(7, 5, 20));
		graphEdges.add(new Edge(2, 3, 24));
		graphEdges.add(new Edge(7, 8, 44));		//I create these in the constructor in "almost sorted order". (Just the final 2 edges should be switched). This is more for ease of input, but I call Collections.sort() on my edge list before the algorithm begins so it doesn't matter
		graphEdges.add(new Edge(6, 5, 30));

		nodeCount=8;		//CAREFUL: nodeCount must be correct. No error checking between nodeCount & graphEdges to see how many nodes actually exist
	}

	public void kruskalMST(){
		String outputMessage="";	//hold output for the user to know algorithm's progress

		Collections.sort(graphEdges);		//sort edges with smallest weight 1st
		ArrayList<Edge> mstEdges = new ArrayList<Edge>();	//list of edges included in the Minimum spanning tree (initially empty)

		DisjointSet nodeSet = new DisjointSet(nodeCount+1);		//Initialize singleton sets for each node in graph. (nodeCount +1) to make the array 1-indexed & ignore the 0th position

		for(int i=1; i<graphEdges.size() && mstEdges.size()<(nodeCount-1); i++){		//loop over all edges. Start @ 1 (ignore 0th as placeholder). Also early termination when number of edges=(number of nodes -1)
			Edge currentEdge = graphEdges.get(i);
			int root1 = nodeSet.find(currentEdge.getVertex1());
			int root2 = nodeSet.find(currentEdge.getVertex2());
			outputMessage+="find("+currentEdge.getVertex1()+") returns "+root1+", find("+currentEdge.getVertex2()+") returns "+root2;		//just print, keep on same line for union message
			String unionMessage=",\tNo union performed\n";		//assume no union is to be performed, changed later if a union DOES happen
			if(root1 != root2){			//if roots are in different sets
				mstEdges.add(currentEdge);		//add the edge to the graph
				nodeSet.union(root1, root2);	//merge the sets
				unionMessage=",\tUnion("+root1+", "+root2+") done\n";		//change what's printed if a union IS performed
			}
			outputMessage+=unionMessage;
		}

		outputMessage+="\nFinal Minimum Spanning Tree ("+mstEdges.size()+" edges)\n";
		int mstTotalEdgeWeight=0;		//keeps track of total weight of all edges in the MST
		for(Edge edge: mstEdges){
			outputMessage+=edge +"\n";		//print each edge
			mstTotalEdgeWeight += edge.getWeight();
		}
		outputMessage+="\nTotal weight of all edges in MST: "+mstTotalEdgeWeight;
		System.out.println(outputMessage);

		try (PrintWriter outputFile = new PrintWriter( new File("06outputMST.txt") ); ){
			outputFile.println(outputMessage);
			System.out.println("\nOpen \"06outputMST.txt\" for backup copy of answers");
		} catch (FileNotFoundException e) {
			System.out.println("Error! Couldn't create file");
		}
	}
}


class Edge implements Comparable<Edge>{
	private int vertex1;	//an edge has 2 vertices & a weight
	private int vertex2;
	private int weight;

	public Edge(int vertex1, int vertex2, int weight){
		this.vertex1=vertex1;
		this.vertex2=vertex2;
		this.weight=weight;
	}

	public int getVertex1(){
		return vertex1;
	}

	public int getVertex2(){
		return vertex2;
	}

	public int getWeight(){
		return weight;
	}

	@Override
	public int compareTo(Edge otherEdge) {				//Compare based on edge weight (for sorting)
		return this.getWeight() - otherEdge.getWeight();
	}

	@Override
	public String toString() {
		return "("+getVertex1()+", "+getVertex2()+") weight: "+getWeight();
	}
}


// DisjointSet class
//
// CONSTRUCTION: with int representing initial number of sets
//
// ******************PUBLIC OPERATIONS*********************
// void union( root1, root2 ) --> Merge two sets
// int find( x )              --> Return set containing x
// ******************ERRORS********************************
// No error checking is performed
// http://users.cis.fiu.edu/~weiss/dsaajava3/code/DisjSets.java

/**
 * Disjoint set class, using union by rank and path compression.
 * Elements in the set are numbered starting at 0.
 * @author Mark Allen Weiss
 */
class DisjointSet{
	private int [] s;		//the set field


	public int[] getSet(){		//mostly debugging method to print array
		return s;
	}

	/**
	 * Construct the disjoint sets object.
	 * @param numElements the initial number of disjoint sets.
	 */
	public DisjointSet( int numElements ) {		//constructor creates singleton sets
		s = new int [ numElements ];
		for( int i = 0; i < s.length; i++ )		//initialize to -1 so the trees have nothing in them
			s[ i ] = -1;
	}

	/**
	 * Union two disjoint sets using the height heuristic.
	 * For simplicity, we assume root1 and root2 are distinct
	 * and represent set names.
	 * @param root1 the root of set 1.
	 * @param root2 the root of set 2.
	 */
	public void union( int root1, int root2 ) {
		if( s[ root2 ] < s[ root1 ] )  // root2 is deeper
			s[ root1 ] = root2;        // Make root2 new root
		else {
			if( s[ root1 ] == s[ root2 ] )
				s[ root1 ]--;          // Update height if same
			s[ root2 ] = root1;        // Make root1 new root
		}
	}

	/**
	 * Perform a find with path compression.
	 * Error checks omitted again for simplicity.
	 * @param x the element being searched for.
	 * @return the set containing x.
	 */
	public int find( int x ) {
		if( s[ x ] < 0 )	//if tree has no elements, then it is its own root
			return x;
		else
			return s[ x ] = find( s[ x ] );
	}
}