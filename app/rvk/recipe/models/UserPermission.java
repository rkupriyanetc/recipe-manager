package rvk.recipe.models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.ebean.Model;
import be.objectify.deadbolt.core.models.Permission;

/**
 * @author RVK
 */
@Entity
@Table( name = "permissions" )
public class UserPermission extends Identifier implements Permission {
	
	private static final long																	serialVersionUID	= 4L;
	
	public String																							value;
	
	public static final Model.Finder< Long, UserPermission >	find							= new Model.Finder< Long, UserPermission >(
																																									Long.class, UserPermission.class );
	
	public String getValue() {
		return value;
	}
	
	public static UserPermission findByValue( String value ) {
		return find.where().eq( "value", value ).findUnique();
	}
	
	@Override
	protected String classInfo() {
		final StringBuffer sb = new StringBuffer( "\n" );
		sb.append( "Value : " );
		sb.append( value );
		sb.append( ". " );
		return sb.toString();
	}
}
