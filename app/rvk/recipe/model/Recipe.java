package rvk.recipe.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import play.db.ebean.Model;

/**
 * @author RVK
 */
@Entity
@Table( name = "recipes" )
public class Recipe extends Identifier {
	
	private static final long										serialVersionUID	= 2L;
	
	@Basic
	@Column( length = 150, nullable = false )
	private String															title;
	
	@Lob
	@Basic( fetch = FetchType.LAZY, optional = true )
	public String																description;
	
	@Basic
	@Column( name = "date_creation", nullable = false )
	public Date																	dateCreation;
	
	@Basic
	@Column( length = 255 )
	public String																link;
	
	@ManyToOne
	public User																	user;
	
	@Basic
	@Column( name = "image_name", length = 36 )
	public String																imageName;
	
	@OrderColumn( name = "id" )
	@CollectionTable( name = "recipe_images", joinColumns = { @JoinColumn( name = "recipe_id" ) } )
	@ElementCollection( fetch = FetchType.LAZY )
	@Column( name = "image_name", length = 36 )
	public List< String >												imagesNames				= new ArrayList< String >();
	
	@CollectionTable( name = "recipe_tags", joinColumns = { @JoinColumn( name = "recipe_id" ) } )
	@ElementCollection( fetch = FetchType.LAZY )
	@Column( name = "tag", length = 25 )
	private Set< String >												tags							= new HashSet< String >();
	
	@Basic
	public boolean															isPrivate;
	
	@Basic
	public int																	visiting;
	
	@Basic
	public byte																	rating;
	
	public static Model.Finder< Long, Recipe >	find							= new Model.Finder< Long, Recipe >( Long.class, Recipe.class );
	
	public Recipe( final String title, final String description, final User user ) {
		this.title = title;
		this.description = description;
		this.user = user;
		this.dateCreation = Calendar.getInstance().getTime();
		this.visiting = 0;
		this.rating = 0;
	}
	
	public static Recipe byId( final Long id ) {
		return find.byId( id );
	}
	
	public static List< Recipe > byUser( final User user ) {
		return find.where().eq( "user_id", user ).findList();
	}
	
	protected String classInfo() {
		final StringBuffer sb = new StringBuffer( "\n" );
		sb.append( "Recipe: " );
		sb.append( title );
		sb.append( ". By User : " );
		sb.append( user.email );
		return sb.toString();
	}
}
