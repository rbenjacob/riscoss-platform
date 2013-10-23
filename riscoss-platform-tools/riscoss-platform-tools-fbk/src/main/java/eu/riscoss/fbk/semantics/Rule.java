package eu.riscoss.fbk.semantics;


public class Rule
{
	public static final int All = 0;
	public static final int Exists = 1;
	
	public String targetPred;
	
	public Condition condition = new Condition( "", "", Operator.Equals );
	
	public int connective = All;
	
	
	public Rule( int connective, String target, Condition condition )
	{
		this.connective = connective;
		this.targetPred = target;
		this.condition = condition;
	}
	
	public Rule( String target, Condition condition )
	{
		this( All, target, condition );
	}
	
	public Condition getCondition() {
		return condition;
	}
	
	public String getSourcePred() {
		return condition.predicate;
	}
}