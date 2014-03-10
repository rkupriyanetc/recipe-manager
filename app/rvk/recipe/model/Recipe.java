package rvk.recipe.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;

/**
 * @author RVK
 */
public class Recipe extends Identifier {
	
	private static final long				serialVersionUID	= 2L;
	
	@Basic
	@Column( length = 255 )
	public String										description;
	
	@Basic
	public Date											dateCreation;
	
	@Basic
	public String										link;
	
	public User											userId;
	
	@Basic
	public String										imageName;
	
	public java.util.List< String >	imagesNames;
	
	public Set< String >						tags;
	
	@Basic
	public boolean									isPrivate;
	
	@Basic
	public int											visiting;
	
	@Basic
	public byte											rating;
	
	protected String classInfo() {
		final StringBuffer sb = new StringBuffer( "\n" );
		sb.append( "By User : " );
		sb.append( userId.email );
		return sb.toString();
	}
	
	public static class List {
		
		private static Finder< Long, Recipe >		find	= new Finder< Long, Recipe >( Long.class, Recipe.class );
		
		public static java.util.List< Recipe >	ALL		= find.all();
		
		public static boolean add( final Recipe r ) {
			if ( !ALL.contains( r ) )
				return ALL.add( r );
			else
				return false;
		}
		
		public static Recipe get( final int index ) {
			if ( index >= 0 && index < ALL.size() )
				return ALL.get( index );
			else
				return null;
		}
		
		public static int size() {
			return ALL.size();
		}
	}
}
