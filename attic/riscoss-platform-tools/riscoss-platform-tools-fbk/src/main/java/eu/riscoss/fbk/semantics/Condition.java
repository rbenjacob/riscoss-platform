package eu.riscoss.fbk.semantics;

public class Condition
{
	String predicate;
	String value;
	Operator op;
	
	public Condition( String predicate, String value, Operator op )
	{
		this.predicate = predicate;
		this.value = value;
		this.op = op;
	}

	public Operator getOperator() {
		return op;
	}
	
	protected Condition() {}
	
	public String getPredicate()
	{
		return predicate;
	}

	public boolean isPositive()
	{
		return !(op == Operator.Not);
	}
}
