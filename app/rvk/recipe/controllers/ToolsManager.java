package rvk.recipe.controllers;

import static play.data.Form.form;

import java.util.List;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import rvk.recipe.model.User;
import views.html.manager.adminUsers;
import views.html.manager.index;

public class ToolsManager extends Controller {
	
	public static Result manager() {
		return ok( index.render() );
	}
	
	public static Result adminUsers() {
		final List< User > list = User.getAll();
		return ok( adminUsers.render( list, form( User.class ), "" ) );
	}
	
	public static Result newUser() {
		final Form< User > userForm = form( User.class ).bindFromRequest();
		if ( userForm.hasErrors() ) {
			return badRequest( adminUsers.render( User.getAll(), userForm, "Bad input for new User" ) );
		}
		final User user = userForm.get();
		User.getAll().add( user );
		user.save();
		return redirect( routes.ToolsManager.adminUsers() );
	}
}
