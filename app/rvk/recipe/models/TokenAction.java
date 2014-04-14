package rvk.recipe.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import play.data.format.Formats;
import play.db.jpa.JPA;

@Entity
@Table( name = "tokens" )
public class TokenAction extends Identifier {
	
	public enum Type {
		// @EnumValue( "EV" )
		EMAIL_VERIFICATION, // @EnumValue( "PR" )
		PASSWORD_RESET
	}
	
	// private static final long serialVersionUID = 6L;
	/**
	 * Verification time frame (until the user clicks on the link in the email)
	 * in seconds
	 * Defaults to one week
	 */
	private final static long	VERIFICATION_TIME	= 7 * 24 * 3600;
	
	@Column( unique = true )
	public String							token;
	
	@ManyToOne
	public User								targetUser;
	
	@Enumerated( EnumType.ORDINAL )
	public Type								type;
	
	@Formats.DateTime( pattern = "yyyy-MM-dd HH:mm:ss" )
	public Date								created;
	
	@Formats.DateTime( pattern = "yyyy-MM-dd HH:mm:ss" )
	public Date								expires;
	
	// public static final Finder< Long, TokenAction > find = new Finder< Long,
	// TokenAction >( Long.class, TokenAction.class );
	public static TokenAction findByToken( final String token, final Type type ) {
		final TypedQuery< TokenAction > q = JPA.em().createNamedQuery(
				"select t from TokenAction t where t.token = :token and t.type = :type", TokenAction.class );
		return q.setParameter( "token", token ).setParameter( "type", type ).getSingleResult();
		// return find.where().eq( "token", token ).eq( "type", type ).findUnique();
	}
	
	public static void deleteByUser( final User u, final Type type ) {
		final Query q = JPA.em().createQuery( "delete from tokens where target_user_id = :userid and type = :type" );
		q.setParameter( "userid", u.id ).setParameter( "type", type.ordinal() ).executeUpdate();
		/**
		 * QueryIterator< TokenAction > iterator = find.where().eq( "targetUser.id",
		 * u.id ).eq( "type", type ).findIterate();
		 * Ebean.delete( iterator );
		 * iterator.close();
		 */
	}
	
	public boolean isValid() {
		return this.expires.after( new Date() );
	}
	
	public static TokenAction create( final Type type, final String token, final User targetUser ) {
		final TokenAction ua = new TokenAction();
		ua.targetUser = targetUser;
		ua.token = token;
		ua.type = type;
		final Date created = new Date();
		ua.created = created;
		ua.expires = new Date( created.getTime() + VERIFICATION_TIME * 1000 );
		JPA.em().persist( ua );
		// ua.save();
		return ua;
	}
	
	@Override
	protected String classInfo() {
		final StringBuffer sb = new StringBuffer( "\n" );
		sb.append( "Token : " );
		sb.append( token );
		sb.append( ", Type : " );
		sb.append( type );
		sb.append( "Target User : " );
		sb.append( targetUser );
		return sb.toString();
	}
}
