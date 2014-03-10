package rvk.recipe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {
	
	private Logger	LOGGER	= LoggerFactory.getLogger( Global.class );
	
	@Override
	public void onStart( final Application app ) {
		LOGGER.info( "Project Recipe Manager Started!" );
		initialData();
	}
	
	private void initialData() {}
}
