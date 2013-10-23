package eu.riscoss.fbk.language;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Proposition
{
	
	String						id;
	private ArrayList<Relation>	in	= new ArrayList<Relation>();
	private ArrayList<Relation>	out	= new ArrayList<Relation>();
	HashMap<String,String>		properties = new HashMap<String,String>();
	HashMap<String,Double>		values = new HashMap<String,Double>();
	
	private String classname;
	
	public Proposition( String classname, String id )
	{
		this.classname = classname;
		this.id = id;
	}
	
	public Proposition( String id )
	{
		classname = this.getClass().getSimpleName().toLowerCase();
		this.id = id;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String toString()
	{
		return getId();
	}
	
	public Proposition clone() throws CloneNotSupportedException
	{
		Constructor<?> ctor;
		try
		{
			ctor = getClass().getConstructor( String.class );
			
			Proposition p = (Proposition) ctor.newInstance( getId() );
			
			p.classname = this.classname;
			
			for( String key : this.values.keySet() ) {
				p.values.put( key,  this.values.get( key ) );
			}
			
			for( String key : this.properties.keySet() ) {
				p.properties.put( key,  this.properties.get( key ) );
			}
			
			return p;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			
			throw new CloneNotSupportedException();
		}
	}
	
	public Proposition clone( String id )
	{
		Constructor<?> ctor;
		try
		{
			ctor = getClass().getConstructor( String.class );
			
			Proposition p = (Proposition) ctor.newInstance( id );
			
			p.classname = this.classname;
			
			for( String key : this.values.keySet() ) {
				p.values.put( key,  this.values.get( key ) );
			}
			
			for( String key : this.properties.keySet() ) {
				p.properties.put( key,  this.properties.get( key ) );
			}
			
			return p;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			
			return null;
		}
	}
	
	public List<Relation> in()
	{
		return in;
	}
	
	public List<Relation> out()
	{
		return out;
	}
	
	public String getStereotype()
	{
		return classname;
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

	public void setValue( String name, double value )
	{
		values.put( name, value );
	}
}
