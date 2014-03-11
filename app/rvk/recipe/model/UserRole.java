package rvk.recipe.model;

import com.avaje.ebean.annotation.EnumValue;

/**
 * @author RVK
 */
public enum UserRole {
	@EnumValue( "ADMIN" )
	ADMINISTRATOR, 
	@EnumValue( "PRIV" )
	PRIVILEGEUSER, 
	@EnumValue( "USER" )
	USER, ;
}
