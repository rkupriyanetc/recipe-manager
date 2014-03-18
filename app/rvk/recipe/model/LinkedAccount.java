package rvk.recipe.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

import com.feth.play.module.pa.user.AuthUser;

/**
 * @author RVK
 */
@Entity
@Table( name = "linkeds" )
public class LinkedAccount extends Identifier {
	
	private static final long																serialVersionUID	= 3L;
	
	@ManyToOne
	public User																							user;
	
	@Column( name = "provider_user_id" )
	public String																						providerUserId;
	
	@Column( name = "provider_key" )
	public String																						providerKey;
	
	public static final Model.Finder< Long, LinkedAccount >	find							= new Model.Finder< Long, LinkedAccount >(
																																								Long.class, LinkedAccount.class );
	
	public static LinkedAccount findByProviderKey( final User user, String key ) {
		return find.where().eq( "user_id", user ).eq( "provider_key", key ).findUnique();
	}
	
	public static LinkedAccount create( final AuthUser authUser ) {
		final LinkedAccount ret = new LinkedAccount();
		ret.update( authUser );
		return ret;
	}
	
	public void update( final AuthUser authUser ) {
		this.providerKey = authUser.getProvider();
		this.providerUserId = authUser.getId();
	}
	
	public static LinkedAccount create( final LinkedAccount acc ) {
		final LinkedAccount ret = new LinkedAccount();
		ret.providerKey = acc.providerKey;
		ret.providerUserId = acc.providerUserId;
		return ret;
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