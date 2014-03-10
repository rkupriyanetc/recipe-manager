package rvk.recipe.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.db.ebean.Model;

/**
 * @author RVK
 */
@MappedSuperclass
public abstract class Identifier extends Model {
	
	protected static final Logger	LOGGER						= LoggerFactory.getLogger( Identifier.class );
	
	private static final long			serialVersionUID	= -1L;
	
	@Id
	@GeneratedValue
	public Long										id;
	
	protected abstract String classInfo();
	
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
