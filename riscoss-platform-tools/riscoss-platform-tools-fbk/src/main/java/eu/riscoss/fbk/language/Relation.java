package eu.riscoss.fbk.language;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Relation implements Cloneable
{
	public static final int	AND			= 0;
	public static final int	OR			= 1;
	
	private String classname;
	
	Proposition				target		= null;
	ArrayList<Proposition>	sources		= new ArrayList<Proposition>();
	int						operator	= AND;
	boolean					negative	= false;
	
	HashMap<String,String>		properties = new HashMap<String,String>();
	
	public Relation()
	{
		classname = getClass().getSimpleName().toLowerCase();
	}
	
	public Relation( String stereotype )
	{
		classname = stereotype;
	}
	
	public String getStereotype()
	{
		return classname;
	}
	
	public Relation clone() throws CloneNotSupportedException
	{
		Constructor<?> ctor;
		try
		{
			ctor = getClass().getConstructor();
			
			Relation r = (Relation) ctor.newInstance();
			
			r.classname = this.classname;
			r.operator = operator;
			r.negative = negative;
			
			for( String key : properties.keySet() )
			{
				r.properties.put( key, properties.get( key ) );
			}
			
			return r;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			
			throw new CloneNotSupportedException();
		}
	}
	
	public String getProperty( String name, String def )
	{
		String ret = properties.get( name );
		
		if( ret == null ) ret = def;
		
		return ret;
	}
	
	public void setProperty( String name, String value )
	{
		properties.put( name, value );
	}

	public void setTarget( Proposition p )
	{
		if( target != null )
		{
			target.in().remove( this );
		}
		p.in().add( this );
		target = p;
	}
	
	public void addSource( Proposition p )
	{
		p.out().add( this );
		sources.add( p );
	}
	
	public void destroy()
	{
		for( Proposition p : sources )
		{
			p.out().remove( this );
		}
		sources.clear();
		target.in().remove( this );
		target = null;
	}	
	public int getSourceCount()
	{
		return sources.size();
	}
	
	String rule( String lhs, String rhs )
	{
		return lhs + " :- " + rhs + ".\n";
	}
	
	String rule( String lhs, String[] rhs )
	{
		String s = "";
		String sep = "";
		
		for( String chunk : rhs )
		{
			s += sep + chunk;
			sep = ", ";
		}
		
		return lhs + " :- " + s + ".\n";
	}
	
	String st( List<Proposition> props )
	{
		return pred( "st", props );
	}
	
	String sf( List<Proposition> props )
	{
		return pred( "sf", props );
	}
	
	String su( List<Proposition> props )
	{
		return pred( "su", props );
	}
	
	String pred( String p, List<Proposition> props )
	{
		String ret = "";
		String sep = "";
		
		for( Proposition prop : props )
		{
			ret += sep + pred( p, prop );
			sep = ", ";
		}
		
		return ret;
	}
	
	String st( Proposition s )
	{
		return pred( "st", s );
	}
	
	String sf( Proposition s )
	{
		return pred( "sf", s );
	}
	
	String su( Proposition s )
	{
		return pred( "su", s );
	}
	
	String pred( String p, Proposition s )
	{
		return pred( p, s.getId() );
	}
	
	String pred( String p, String var )
	{
		return p + "(" + var + ")";
	}
	
	String at( Proposition s )
	{
		return pred( "at", s );
	}
	
	String af( Proposition s )
	{
		return pred( "af", s );
	}
	
	String au( Proposition s )
	{
		return pred( "au", s );
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "( " + target + ", " + sources + " ) ";
	}
	
	public void setOperator( int op )
	{
		this.operator = op;
	}
	
	public int getOperator()
	{
		return this.operator;
	}
	
	public Proposition getTarget()
	{
		return target;
	}
	
	public ArrayList<Proposition> getSources()
	{
		return sources;
	}
	
	public void setSources( ArrayList<Proposition> s )
	{
		sources.clear();
		for( Proposition p : s )
			addSource( p );
	}
	
	public void setNegative( boolean b )
	{
		this.negative = b;
	}
	
	public boolean isNegative()
	{
		return negative;
	}

	public Iterable<String> properties()
	{
		return properties.keySet();
	}
}
