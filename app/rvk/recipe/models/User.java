package rvk.recipe.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import rvk.recipe.models.TokenAction.Type;
import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.FirstLastNameIdentity;
import com.feth.play.module.pa.user.NameIdentity;

/**
 * @author RVK
 */
@Entity
@Table( name = "users" )
public class User extends Identifier implements Subject {
	
	private static final long									serialVersionUID	= 1L;
	
	@Constraints.Email
	// if you make this unique, keep in mind that users *must* merge/link their
	// accounts then on signup with additional providers
	// @Column(unique = true)
	@Column( length = 60 )
	public String															email;
	
	@Required
	@Column( nullable = false, length = 25 )
	public String															nickname;
	
	@Column( length = 60 )
	public String															fullname;
	
	@Column( length = 15 )
	public String															telephoneNumber;
	
	public String															address;
	
	@Formats.DateTime( pattern = "yyyy-MM-dd HH:mm:ss" )
	public Date																lastLogin;
	
	public boolean														active;
	
	public boolean														emailValidated;
	
	public UserRole														role;
	
	@OneToMany( cascade = CascadeType.ALL )
	public List< LinkedAccount >							linkedAccounts;
	
	@ManyToMany
	public List< UserPermission >							permissions;
	
	public static final Finder< Long, User >	find							= new Finder< Long, User >( Long.class, User.class );
	
	@Override
	public String getIdentifier() {
		return Long.toString( id );
	}
	
	@Override
	public List< ? extends Role > getRoles() {
		return Arrays.asList( new UserRole[] { role } );
	}
	
	@Override
	public List< ? extends Permission > getPermissions() {
		return permissions;
	}
	
	public static boolean existsByAuthUserIdentity( final AuthUserIdentity identity ) {
		final ExpressionList< User > exp;
		if ( identity instanceof UsernamePasswordAuthUser ) {
			exp = getUsernamePasswordAuthUserFind( ( UsernamePasswordAuthUser )identity );
		}
		else {
			exp = getAuthUserFind( identity );
		}
		return exp.findRowCount() > 0;
	}
	
	private static ExpressionList< User > getAuthUserFind( final AuthUserIdentity identity ) {
		return find.where().eq( "active", true ).eq( "linkedAccounts.providerUserId", identity.getId() )
				.eq( "linkedAccounts.providerKey", identity.getProvider() );
	}
	
	public static User findByAuthUserIdentity( final AuthUserIdentity identity ) {
		if ( identity == null ) {
			return null;
		}
		if ( identity instanceof UsernamePasswordAuthUser ) {
			return findByUsernamePasswordIdentity( ( UsernamePasswordAuthUser )identity );
		}
		else {
			return getAuthUserFind( identity ).findUnique();
		}
	}
	
	public static User findByUsernamePasswordIdentity( final UsernamePasswordAuthUser identity ) {
		return getUsernamePasswordAuthUserFind( identity ).findUnique();
	}
	
	private static ExpressionList< User > getUsernamePasswordAuthUserFind( final UsernamePasswordAuthUser identity ) {
		return find.where().eq( "active", true ).eq( "email", identity.getEmail() )
				.eq( "linkedAccounts.providerKey", identity.getProvider() );
	}
	
	public void merge( final User otherUser ) {
		for ( final LinkedAccount acc : otherUser.linkedAccounts ) {
			this.linkedAccounts.add( LinkedAccount.create( acc ) );
		}
		// do all other merging stuff here - like resources, etc.
		// deactivate the merged user that got added to this one
		otherUser.active = false;
		Ebean.save( Arrays.asList( new User[] { otherUser, this } ) );
	}
	
	public static User create( final AuthUser authUser ) {
		final User user = new User();
		user.role = UserRole.findByRoleName( UserRole.USER_ROLE );
		// user.permissions = new ArrayList<UserPermission>();
		// user.permissions.add(UserPermission.findByValue("printers.edit"));
		user.active = true;
		user.lastLogin = new Date();
		user.linkedAccounts = Collections.singletonList( LinkedAccount.create( authUser ) );
		if ( authUser instanceof EmailIdentity ) {
			final EmailIdentity identity = ( EmailIdentity )authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
			user.email = identity.getEmail();
			user.emailValidated = false;
		}
		if ( authUser instanceof NameIdentity ) {
			final NameIdentity identity = ( NameIdentity )authUser;
			final String name = identity.getName();
			if ( name != null ) {
				user.nickname = name;
			}
		}
		if ( authUser instanceof FirstLastNameIdentity ) {
			final FirstLastNameIdentity identity = ( FirstLastNameIdentity )authUser;
			final String firstName = identity.getFirstName();
			final String lastName = identity.getLastName();
			if ( firstName != null ) {
				user.fullname = firstName;
			}
			if ( lastName != null ) {
				user.fullname += ' ' + lastName;
			}
		}
		user.save();
		// user.saveManyToManyAssociations( "role" );
		// user.saveManyToManyAssociations("permissions");
		return user;
	}
	
	public static void merge( final AuthUser oldUser, final AuthUser newUser ) {
		User.findByAuthUserIdentity( oldUser ).merge( User.findByAuthUserIdentity( newUser ) );
	}
	
	public Set< String > getProviders() {
		final Set< String > providerKeys = new HashSet< String >( linkedAccounts.size() );
		for ( final LinkedAccount acc : linkedAccounts ) {
			providerKeys.add( acc.providerKey );
		}
		return providerKeys;
	}
	
	public static void addLinkedAccount( final AuthUser oldUser, final AuthUser newUser ) {
		final User u = User.findByAuthUserIdentity( oldUser );
		u.linkedAccounts.add( LinkedAccount.create( newUser ) );
		u.save();
	}
	
	public static void setLastLoginDate( final AuthUser knownUser ) {
		final User u = User.findByAuthUserIdentity( knownUser );
		u.lastLogin = new Date();
		u.save();
	}
	
	public static User findByEmail( final String email ) {
		return find.where().eq( "active", true ).eq( "email", email ).findUnique();
	}
	
	public LinkedAccount getAccountByProvider( final String providerKey ) {
		return LinkedAccount.findByProviderKey( this, providerKey );
	}
	
	public static void verify( final User unverified ) {
		// You might want to wrap this into a transaction
		unverified.emailValidated = true;
		unverified.save();
		TokenAction.deleteByUser( unverified, Type.EMAIL_VERIFICATION );
	}
	
	public void changePassword( final UsernamePasswordAuthUser authUser, final boolean create ) {
		LinkedAccount a = this.getAccountByProvider( authUser.getProvider() );
		if ( a == null ) {
			if ( create ) {
				a = LinkedAccount.create( authUser );
				a.user = this;
			}
			else {
				throw new RuntimeException( "Account not enabled for password usage" );
			}
		}
		a.providerUserId = authUser.getHashedPassword();
		a.save();
	}
	
	public void resetPassword( final UsernamePasswordAuthUser authUser, final boolean create ) {
		// You might want to wrap this into a transaction
		this.changePassword( authUser, create );
		TokenAction.deleteByUser( this, Type.PASSWORD_RESET );
	}
	
	@Override
	protected String classInfo() {
		final StringBuffer sb = new StringBuffer( "\n" );
		sb.append( "Email : " );
		sb.append( email );
		sb.append( ", Nickname : " );
		sb.append( nickname );
		sb.append( ". " );
		return sb.toString();
	}
}
