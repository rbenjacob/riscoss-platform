package eu.riscoss.fbk.lp;

import java.util.LinkedList;

public class Relation
{
	private Solver satSolver;
	private Solver denSolver;

	private Node				target;				// target node for this relation
	private LinkedList<Node>	sources;			// source nodes for this relation
	private float				weight		= 1;
	private String				mnemonic	= "";
	
	
	public Relation()
	{
		this( null );
	}

	public Relation( Solver solver )
	{
		this( solver, null, new LinkedList<Node>() );
	}
	
	public Relation( Solver solver, Node theTarget, LinkedList<Node> theSources )
	{
		satSolver = solver;
		denSolver = new Solver.AndSolver( true );
		
		target	= theTarget;
		sources	= theSources;
	}
	
	@SuppressWarnings("unused")
	private static LinkedList<Node> mkLinkedList( Node aNode )
	{
		LinkedList<Node> aList = new LinkedList<Node>();
		aList.add(aNode);
		return aList;
	}
	
	Label solveForS()
	{
		return satSolver.solve( this );
	}
	
	Label solveForD()
	{
		return denSolver.solve( this );
	}
	
	public Node getTarget()
	{
		return target;
	}
	
	public LinkedList<Node> getSources()
	{
		return sources;
	}
	
	public void setTargetNode(Node aNode)
	{
		target = aNode;
	}
	
	public void setSourceNodes( LinkedList<Node> theChildren )
	{
		sources = theChildren;
	}
	
	public void addSourceNode(Node aNode)
	{
		sources.add(aNode);
	}
	
	public void addChildrenNodes( LinkedList<Node> additionalChildren )
	{
		sources.addAll( additionalChildren );
	}
	
	public void informNodes()
	{
		target.addIncomingRelation(this);
		
		for( Node node : sources )
			node.addOutgoingRelation( this );
	}

	public void setSatSolver( Solver solver )
	{
		this.satSolver = solver;
	}

	public void setDenSolver( Solver solver )
	{
		this.denSolver = solver;
	}

	public void setWeight( float weight )
	{
		this.weight = weight;
	}

	public float getWeight()
	{
		return weight;
	}

	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}

	public String getMnemonic() {
		return mnemonic;
	}
}


