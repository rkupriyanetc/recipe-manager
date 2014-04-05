package rvk.recipe.controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.recipes.index;

public class Recipes extends Controller {

	public static Result index() {
		return ok( index.render() );
	}
}
