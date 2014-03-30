package rvk.recipe.models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.feth.play.module.pa.user.AuthUser;

/**
 * @author RVK
 */
@Entity
@Table( name = "linkeds" )
public class LinkedAccount extends Identifier {
	
	private static final long													serialVersionUID	= 3L;
	
	@ManyToOne
	public User																				user;
	
	public String																			providerUserId;
	
	public String																			providerKey;
	
	public static final Finder< Long, LinkedAccount >	find							= new Finder< Long, LinkedAccount >( Long.class,
																																					LinkedAccount.class );
	
	public static LinkedAccount findByProviderKey( final User user, String key ) {
		return find.where().eq( "user", user ).eq( "providerKey", key ).findUnique();
	}
	
	public static LinkedAccount create( final AuthUser authUser ) {
		final LinkedAccount ret = new LinkedAccount();
		ret.update( authUser );
		return ret;
	}
	
	public static LinkedAccount create( final LinkedAccount acc ) {
		final LinkedAccount ret = new LinkedAccount();
		ret.providerKey = acc.providerKey;
		ret.providerUserId = acc.providerUserId;
		return ret;
	}
	
	public void update( final AuthUser authUser ) {
		this.providerKey = authUser.getProvider();
		this.providerUserId = authUser.getId();
	}
	
	@Override
	protected String classInfo() {
		final StringBuffer sb = new StringBuffer( "\n" );
		sb.append( "Provided [ " );
		sb.append( "Provider User Id : " );
		sb.append( providerUserId );
		sb.append( ", Provider Key : " );
		sb.append( providerKey );
		sb.append( " ]. " );
		return sb.toString();
	}
}