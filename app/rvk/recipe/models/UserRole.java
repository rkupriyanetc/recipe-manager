package rvk.recipe.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import be.objectify.deadbolt.core.models.Role;

/**
 * @author RVK
 */
@Entity
@Table( name = "roles" )
public class UserRole extends Identifier implements Role {
	
	private static final long											serialVersionUID	= 5L;
	
	@Column( length = 8 )
	public String																	roleName;
	
	public static final String										USER_ROLE					= "USER";
	
	public static final String										PRIV_USER_ROLE		= "PRIVUSER";
	
	public static final String										ADMIN_ROLE				= "ADMIN";
	
	public static final UserRole									USER							= new UserRole( USER_ROLE );
	
	public static final UserRole									PRIVILEGEUSER			= new UserRole( PRIV_USER_ROLE );
	
	public static final UserRole									ADMIN							= new UserRole( ADMIN_ROLE );
	
	public static final Finder< Long, UserRole >	find							= new Finder< Long, UserRole >( Long.class, UserRole.class );
	
	private UserRole( final String name ) {
		this.roleName = name;
	}
	
	@Override
	public String getName() {
		return roleName;
	}
	
	public static UserRole findByRoleName( String roleName ) {
		return find.where().eq( "roleName", roleName ).findUnique();
	}
	
	protected String classInfo() {
		final StringBuffer sb = new StringBuffer( "\n" );
		sb.append( "RoleName : " );
		sb.append( roleName );
		return sb.toString();
	}
}
