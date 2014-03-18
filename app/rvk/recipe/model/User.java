package rvk.recipe.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.validation.Constraints.Required;
import rvk.recipe.manager.StringEncrypter;
import rvk.recipe.model.TokenAction.Type;
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
	
	private static final long						serialVersionUID	= 1L;
	
	@Required
	@Basic
	@Column( nullable = false, length = 25 )
	public String												nickname;
	
	@Basic
	@Column( nullable = false, length = 25 )
	public String												password;
	
	@Required
	@Basic
	@Column( nullable = false, length = 40 )
	public String												email;
	
	public UserRole											role;
	
	@Basic
	@Column( length = 60 )
	public String												fullname;
	
	@Basic
	@Column( length = 15 )
	public String												telephoneNumber;
	
	@Basic
	@Column( length = 255 )
	public String												address;
	
	public boolean											active;
	
	@Column( name = "email_validated" )
	public boolean											emailValidated;
	
	@OneToMany( cascade = CascadeType.ALL )
	public List< LinkedAccount >				linkeds						= new ArrayList< LinkedAccount >();
	
	@ManyToMany
	public List< UserPermission >				permissions				= new ArrayList< UserPermission >();
	
	public static Finder< Long, User >	find							= new Finder< Long, User >( Long.class, User.class );
	
	public User() {}
	
	public User( final String email, final String nickname, final String password, final UserRole role ) {
		this.email = email;
		this.nickname = nickname;
		final StringEncrypter se = new StringEncrypter( password );
		this.password = se.toHexString( se.encrypt() );
		this.role = role;
	}
	
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
	
	public static List< User > getAll() {
		return find.all();
	}
	
	public static User byId( final Long id ) {
		return find.byId( id );
	}
	
	public static User byEmail( final String email ) {
		return find.where().eq( "email", email ).findUnique();
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
		return find.where().eq( "active", true ).eq( "linkeds.provider_user_id", identity.getId() )
				.eq( "linkeds.provider_key", identity.getProvider() );
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
		return getEmailUserFind( identity.getEmail() ).eq( "linkeds.provider_key", identity.getProvider() );
	}
	
	public void merge( final User otherUser ) {
		for ( final LinkedAccount acc : otherUser.linkeds ) {
			this.linkeds.add( LinkedAccount.create( acc ) );
		}
		// do all other merging stuff here - like resources, etc.
		// deactivate the merged user that got added to this one
		otherUser.active = false;
		Ebean.save( Arrays.asList( new User[] { otherUser, this } ) );
	}
	
	public static User create( final AuthUser authUser ) {
		final User user = new User();
		user.role = UserRole.findByRoleName( UserRole.USER_ROLE_NAME );
		// user.permissions = new ArrayList<UserPermission>();
		// user.permissions.add(UserPermission.findByValue("printers.edit"));
		user.active = true;
		user.linkeds = Collections.singletonList( LinkedAccount.create( authUser ) );
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
		user.saveManyToManyAssociations( "role_id" );
		// user.saveManyToManyAssociations("permissions");
		return user;
	}
	
	public static void merge( final AuthUser oldUser, final AuthUser newUser ) {
		User.findByAuthUserIdentity( oldUser ).merge( User.findByAuthUserIdentity( newUser ) );
	}
	
	public Set< String > getProviders() {
		final Set< String > providerKeys = new HashSet< String >( linkeds.size() );
		for ( final LinkedAccount acc : linkeds ) {
			providerKeys.add( acc.providerKey );
		}
		return providerKeys;
	}
	
	public static void addLinkedAccount( final AuthUser oldUser, final AuthUser newUser ) {
		final User u = User.findByAuthUserIdentity( oldUser );
		u.linkeds.add( LinkedAccount.create( newUser ) );
		u.save();
	}
	
	public static void setLastLoginDate( final AuthUser knownUser ) {
		final User u = User.findByAuthUserIdentity( knownUser );
		u.save();
	}
	
	public static User findByEmail( final String email ) {
		return getEmailUserFind( email ).findUnique();
	}
	
	private static ExpressionList< User > getEmailUserFind( final String email ) {
		return find.where().eq( "active", true ).eq( "email", email );
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
