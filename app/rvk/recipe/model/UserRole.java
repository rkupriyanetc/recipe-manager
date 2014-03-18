package rvk.recipe.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.ebean.Model;
import be.objectify.deadbolt.core.models.Role;

/**
 * @author RVK
 */
@Entity
@Table( name = "roles" )
public class UserRole extends Identifier implements Role {
	
	private static final long			serialVersionUID	= 5L;
	
	public static final String		USER_ROLE_NAME		= "USER";
	
	public static final String		PRIVILE_ROLE_NAME	= "PRIVILEGEUSER";
	
	public static final String		ADMIN_ROLE_NAME		= "ADMIN";
	
	public static final UserRole	USER							= new UserRole( USER_ROLE_NAME );
	
	public static final UserRole	PRIVILEGEUSER			= new UserRole( PRIVILE_ROLE_NAME );
	
	public static final UserRole	ADMIN							= new UserRole( ADMIN_ROLE_NAME );
	
	@Basic
	@Column( nullable = false, length = 13 )
	private final String					name;
	
	private UserRole( final String name ) {
		this.name = name;
	}
	
	public static final Model.Finder< Long, UserRole >	find	= new Model.Finder< Long, UserRole >( Long.class, UserRole.class );
	
	@Override
	public String getName() {
		return name;
	}
	
	public static UserRole findByRoleName( String roleName ) {
		return find.where().eq( "name", roleName ).findUnique();
	}
	
	protected String classInfo() {
		final StringBuffer sb = new StringBuffer( "\n" );
		sb.append( "RoleName : " );
		sb.append( name );
		return sb.toString();
	}
}
