package rvk.recipe.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.format.Formats;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.QueryIterator;
import com.avaje.ebean.annotation.EnumValue;

@Entity
@Table( name = "tokens" )
public class TokenAction extends Identifier {
	
	public enum Type {
		@EnumValue( "EV" )
		EMAIL_VERIFICATION, @EnumValue( "PR" )
		PASSWORD_RESET
	}
	
	/**
	 * 
	 */
	private static final long												serialVersionUID	= 6L;
	
	/**
	 * Verification time frame (until the user clicks on the link in the email)
	 * in seconds
	 * Defaults to one week
	 */
	private final static long												VERIFICATION_TIME	= 7 * 24 * 3600;
	
	@Column( unique = true, name = "name" )
	public String																		token;
	
	@ManyToOne
	public User																			user;
	
	@Enumerated( EnumType.STRING )
	@Column( length = 2, nullable = false )
	public Type																			type;
	
	@Formats.DateTime( pattern = "yyyy-MM-dd HH:mm:ss" )
	public Date																			created;
	
	@Formats.DateTime( pattern = "yyyy-MM-dd HH:mm:ss" )
	public Date																			expires;
	
	public static final Finder< Long, TokenAction >	find							= new Finder< Long, TokenAction >( Long.class,
																																				TokenAction.class );
	
	public static TokenAction findByToken( final String token, final Type type ) {
		return find.where().eq( "name", token ).eq( "type", type ).findUnique();
	}
	
	public static void deleteByUser( final User u, final Type type ) {
		QueryIterator< TokenAction > iterator = find.where().eq( "user.id", u.id ).eq( "type", type ).findIterate();
		Ebean.delete( iterator );
		iterator.close();
	}
	
	public boolean isValid() {
		return this.expires.after( new Date() );
	}
	
	public static TokenAction create( final Type type, final String token, final User targetUser ) {
		final TokenAction ua = new TokenAction();
		ua.user = targetUser;
		ua.token = token;
		ua.type = type;
		final Date created = new Date();
		ua.created = created;
		ua.expires = new Date( created.getTime() + VERIFICATION_TIME * 1000 );
		ua.save();
		return ua;
	}
	
	@Override
	protected String classInfo() {
		final StringBuffer sb = new StringBuffer( "\n" );
		sb.append( "Token : " );
		sb.append( token );
		sb.append( ", Type : " );
		sb.append( type );
		sb.append( "User : " );
		sb.append( user );
		return sb.toString();
	}
}
