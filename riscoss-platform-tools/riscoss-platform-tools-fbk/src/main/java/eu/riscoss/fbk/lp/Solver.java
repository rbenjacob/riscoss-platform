package eu.riscoss.fbk.lp;

import java.util.LinkedList;

public abstract class Solver
{
	public static final class AndSolver extends Solver
	{
		public AndSolver( boolean param )
		{
			super( param );
		}
		
		public Label solve( Relation rel )
		{
			Label productLabel = Label.TOTAL;
			
			for( Node node : rel.getSources() )
			{
				Label tempLabel = getOldLabel( node );
				productLabel = product( productLabel, product( tempLabel, rel.getWeight() ) );
			}
			
			return productLabel;
		}
	}
	
	public static final class OrSolver extends Solver
	{
		public OrSolver( boolean param )
		{
			super( param );
		}
		
		public Label solve( Relation rel )
		{
			Label sumLabel = Label.NO;
			
			for( Node node : rel.getSources() )
			{
				Label tempLabel = getOldLabel( node );
				
				sumLabel = sum( sumLabel, product( tempLabel, rel.getWeight() ) );
			}
			return sumLabel;
		}
	}
	
	public static final class WeightedSolver extends Solver
	{
		public WeightedSolver( boolean param )
		{
			super( param );
		}
		
		public Label solve( Relation rel )
		{
			Label sumLabel = Label.NO;
			
			for( Node node : rel.getSources() )
			{
				Label tempLabel = getOldLabel( node );
				sumLabel = max( sumLabel, product( tempLabel, rel.getWeight() ) );
			}
			
			return sumLabel;
		}
	}
	
	public static final class NotSolver extends Solver
	{
		public NotSolver( boolean param )
		{
			super( param );
		}
		
		public Label solve( Relation rel )
		{
			for( Node node : rel.getSources() )
			{
				if( getOldLabel( node ).getValue() > 0 )
					return new Label( 0f );
			}
			
			return Label.TOTAL;
		}
	}
	
	boolean parameter;
	
	Solver( boolean param )
	{
		parameter = param;
	}
	
	public abstract Label solve( Relation rel );
	
	void mustHaveExactlyOneSource( LinkedList<Node> childNodes )
	{
		if (childNodes.size() != 1)
			throw new UnsupportedOperationException
			("children list of " + this.getClass().getName()
					+ " does not contain exactly one element");
	}
	
	boolean getParameterValue()
	{
		return parameter;
	}
	
	Label getOldLabel( Node aNode )
	{
		if (parameter)
			return aNode.getOldDenLabel();
		else
			return aNode.getOldSatLabel();
	}
	
	protected Label sum( Label l1, final Label aLabel )
	{
		float p1 = l1.getValue();
		float p2 = aLabel.getValue();
		return new Label( Math.abs( p1 + p2 - p1 * p2 ) );
	}
	
	protected Label product( Label l1, final Label aLabel )
	{
		float p1 = l1.getValue();
		float p2 = aLabel.getValue();
		return new Label( p1 * p2 );
	}
	
	protected Label product( Label l1, final float aWeight )
	{
		float p1 = l1.getValue();
		return new Label( p1 * aWeight );
	}
	
	protected Label max( Label l1, Label l2 )
	{
		if( Math.abs( l1.getValue() ) > Math.abs( l2.getValue() ) ) return new Label( l1.getValue() );
		else return new Label( l2.getValue() );
		
//		return new Label( Math.max( l1.getValue(), l2.getValue() ) );
	}
}

