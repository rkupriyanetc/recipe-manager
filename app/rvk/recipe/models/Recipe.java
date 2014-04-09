package rvk.recipe.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author RVK
 */
@Entity
@Table( name = "recipes" )
public class Recipe extends Identifier {
	
	@Column( length = 150, nullable = false )
	public String					title;
	
	@Lob
	@Basic( fetch = FetchType.LAZY, optional = true )
	public String					description;
	
	@Basic
	@Column( name = "creation_date", nullable = false )
	public Date						dateCreation;
	
	public String					link;
	
	@ManyToOne
	public User						user;
	
	public String					mainImage;
	
	@ElementCollection( fetch = FetchType.LAZY )
	public List< String >	imagesNames	= new ArrayList< String >();
	
	@ElementCollection( fetch = FetchType.LAZY )
	public Set< String >	tags				= new HashSet< String >();
	
	public boolean				isPrivate;
	
	public int						visiting;
	
	public byte						rating;
	
	public Recipe() {}
	
	public Recipe( final String title, final String description, final User user ) {
		this.title = title;
		this.description = description;
		this.user = user;
		this.dateCreation = Calendar.getInstance().getTime();
		this.visiting = 0;
		this.rating = 0;
	}
	
	public static Recipe byId( final Long id ) {
		return JPA.em().find( Recipe.class, id );
	}
	
	public static List< Recipe > byUser( final User user ) {
		return find.where().eq( "user", user ).findList();
	}
	
	public static List< Recipe > byTag( final String tag ) {
		return find.where().contains( "tags", tag ).findList();
	}
	
	public static List< Recipe > byUserByTag( final User user, final String tag ) {
		return find.where().eq( "user", user ).contains( "tags", tag ).findList();
	}
	
	@Override
	protected String classInfo() {
		final StringBuffer sb = new StringBuffer( "\n" );
		sb.append( "Recipe: " );
		sb.append( title );
		sb.append( ". By User : " );
		sb.append( user.email );
		return sb.toString();
	}
}