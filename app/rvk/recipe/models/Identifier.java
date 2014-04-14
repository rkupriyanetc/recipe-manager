package rvk.recipe.models;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author RVK
 */
@MappedSuperclass
public abstract class Identifier {
	
	protected static final Logger	LOGGER	= LoggerFactory.getLogger( Identifier.class );
	
	// private static final long serialVersionUID = -1L;
	@Id
	@GeneratedValue
	public Long										id;
	
	public Long getId() {
		return id;
	}
	
	protected abstract String classInfo();
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer( getClass().getName() );
		sb.append( " - " );
		sb.append( getClass().getSimpleName() );
		sb.append( classInfo() );
		sb.append( "ID - " );
		sb.append( id );
		final String s = sb.toString();
		LOGGER.info( s );
		return s;
	}
}