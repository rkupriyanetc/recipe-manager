package rvk.recipe.model;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Constraints.Required;
import rvk.recipe.manager.StringEncrypter;

/**
 * @author RVK
 */
@Entity
@Table( name = "users" )
public class User extends Identifier {
	
	private static final long		serialVersionUID	= 1L;
	
	@Required
	@Basic
	@Column( nullable = false, length = 25 )
	public String								nickname;
	
	@Basic
	@Column( nullable = false, length = 25 )
	public String								password;
	
	@Required
	@Basic
	@Column( nullable = false, length = 40 )
	public String								email;
	
	@Basic
	@Column( nullable = false, length = 13 )
	public UserRole							role;
	
	@Basic
	@Column( length = 60 )
	public String								fullname;
	
	@Basic
	@Column( length = 15 )
	public String								telephoneNumber;
	
	@Basic
	@Column( length = 255 )
	public String								address;
	
	public static List< User >	ALL								= new Finder< Long, User >( Long.class, User.class ).all();
	
	public User() {}
	
	public User( final String email, final String nickname, final String password, final UserRole role ) {
		this.email = email;
		this.nickname = nickname;
		final StringEncrypter se = new StringEncrypter( password );
		this.password = se.toHexString( se.encrypt() );
		this.role = role;
	}
	
	public String validate() {
		if ( password == null || nickname == null || email == null || role == null ) {
			LOGGER.warn( "Password, Nickname, Email or UserRole is not null!" );
			return "Don't create User";
		}
		int n = password.length();
		if ( n < 3 || n > 25 ) {
			LOGGER.warn( "Len string Password between 3 and 25!" );
			return "Len( Password ) in [ 3 .. 25 ]";
		}
		n = nickname.length();
		if ( n < 3 || n > 25 ) {
			LOGGER.warn( "Len string Nickname between 3 and 25!" );
			return "Len( Nickname ) in [ 3 .. 25 ]";
		}
		n = email.length();
		if ( n < 6 || n > 40 ) {
			LOGGER.warn( "Len string Email between 6 and 40!" );
			return "Len( Nickname ) in [ 6 .. 40 ]";
		}
		if ( !email.contains( "@" ) && !email.contains( "." ) && email.indexOf( '@' ) < email.indexOf( '.' ) ) {
			LOGGER.warn( "Dous not email! Example: recipe.manager@ukr.net" );
			return "Example: recipe.manager@ukr.net";
		}
		return null;
	}
	
	protected String classInfo() {
		final StringBuffer sb = new StringBuffer( "\n" );
		sb.append( "Email : " );
		sb.append( email );
		sb.append( ", Nickname : " );
		sb.append( nickname );
		return sb.toString();
	}
}
