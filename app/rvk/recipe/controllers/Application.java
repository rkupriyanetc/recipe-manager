package rvk.recipe.controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.about;
import views.html.index;
import views.html.login;
import views.html.rules;
import views.html.signup;

public class Application extends Controller {
	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static Result index() {
		return ok( index.render() );
	}
	
	public static Result rules() {
		return ok( rules.render() );
	}
	
	public static Result about() {
		return ok( about.render() );
	}
	
	public static Result login() {
		return ok( login.render() );
	}
	
	public static Result signup() {
		return ok( signup.render() );
	}
}
