/**
 * (C) Copyright 2011.03.25 Home Edition.
 */
package rvk.recipe.manager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The StringEncrypter is simple realization of the string encrypt
 * 
 * @author RVK
 * @version 1.0.0
 */
public class StringEncrypter {
	
	/**
	 * The Log instance
	 */
	private static final Logger	LOGGER		= LoggerFactory.getLogger( StringEncrypter.class );
	
	/**
	 * The constants provider
	 */
	public final String					DSA				= "DSA";
	
	public final String					RSA				= "RSA";
	
	public final String					MD5				= "MD5";
	
	public final String					SHA_1			= "SHA-1";
	
	/**
	 * The Provider metod to encrypt String
	 */
	private String							algorithm	= SHA_1;
	
	/**
	 * The private field String of the string encrypt
	 */
	private String							encryptString;
	
	/**
	 * The Sole constructor which uses the SHA_1 algorithm by default
	 */
	public StringEncrypter( final String encryptString ) throws IllegalArgumentException {
		if ( encryptString == null )
			throw new IllegalArgumentException( "The encrypt string is NULL" );
		this.encryptString = encryptString;
	}
	
	/**
	 * Convert the String to the Hex String
	 */
	public String toHexString( final byte[] value ) {
		final StringBuffer sb = new StringBuffer( value.length * 2 );
		final int n = value.length;
		for ( int i = 0; i < n; i++ ) {
			final int k = value[ i ] & 0xff;
			if ( k < 16 )
				sb.append( '0' );
			sb.append( Integer.toHexString( k ) );
		}
		return sb.toString();
	}
	
	/**
	 * The Algorithm to encrypt string. Support only the MD5, RSA, DSA and SHA-1
	 * metod provider
	 * 
	 * @see <code>java.security.Provider</code>.
	 */
	public void setAlgorithm( final String algorithm ) throws NoSuchAlgorithmException {
		if ( !algorithm.equalsIgnoreCase( this.algorithm ) ) {
			if ( !algorithm.equalsIgnoreCase( this.DSA ) || !algorithm.equalsIgnoreCase( this.MD5 )
					|| !algorithm.equalsIgnoreCase( this.RSA ) || !algorithm.equalsIgnoreCase( this.SHA_1 ) ) {
				LOGGER.error( "Algorithm {} is no support algorithm.", algorithm );
				throw new NoSuchAlgorithmException( "This string is no support Algorithm" );
			}
			this.algorithm = algorithm.toUpperCase();
		}
	}
	
	/**
	 * @return The Algorithm to Encrypt
	 */
	public String getAlgorithm() {
		return this.algorithm;
	}
	
	/**
	 * The Encrypt string
	 * 
	 * @return the Encrypt byte array
	 */
	public byte[] encrypt() {
		byte b[] = null;
		try {
			final MessageDigest md = MessageDigest.getInstance( this.algorithm );
			md.reset();
			md.update( this.encryptString.getBytes() );
			b = md.digest();
		}
		catch ( NoSuchAlgorithmException ae ) {
			LOGGER.error( "The class cause : {}. The message : {}.", ae.getCause(), ae.getMessage() );
		}
		return b;
	}
}
