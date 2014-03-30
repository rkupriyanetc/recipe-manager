package rvk.recipe.providers;

import rvk.recipe.providers.MyUsernamePasswordAuthProvider.MySignup;

import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.NameIdentity;

public class MyUsernamePasswordAuthUser extends UsernamePasswordAuthUser implements NameIdentity {
	
	private static final long	serialVersionUID	= 1L;
	
	private final String			nickname;
	
	public MyUsernamePasswordAuthUser( final MySignup signup ) {
		super( signup.password, signup.email );
		this.nickname = signup.nickname;
	}
	
	/**
	 * Used for password reset only - do not use this to signup a user!
	 * 
	 * @param password
	 */
	public MyUsernamePasswordAuthUser( final String password ) {
		super( password, null );
		nickname = null;
	}
	
	@Override
	public String getName() {
		return nickname;
	}
}
