package rvk.recipe.controllers;

import java.util.Date;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import rvk.recipe.models.Recipe;
import rvk.recipe.models.User;
import rvk.recipe.models.UserRole;
import views.html.recipes.editRecipe;
import views.html.recipes.index;
import views.html.recipes.newRecipe;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

public class Recipes extends Controller {
	
	private static final Form< Recipe >	RECIPE_DESIGN_FORM	= Form.form( Recipe.class );
	
	public static Result index() {
		return ok( index.render( Recipe.byUser( Application.getLocalUser( session() ) ) ) );
	}
	
	@Restrict( @Group( UserRole.USER_ROLE ) )
	public static Result newRecipe() {
		return ok( newRecipe.render( RECIPE_DESIGN_FORM.fill( new Recipe() ) ) );
	}
	
	@Restrict( @Group( UserRole.USER_ROLE ) )
	public static Result doNewRecipe() {
		final Form< Recipe > formBinding = RECIPE_DESIGN_FORM.bindFromRequest();
		if ( formBinding.hasErrors() ) {
			flash( Application.FLASH_ERROR_KEY, "--Bad input for new Recipe--" );
			return badRequest( newRecipe.render( formBinding ) );
		}
		final Recipe form = formBinding.get();
		final User user = Application.getLocalUser( session() );
		final Recipe recipe = new Recipe( form.title, form.description, user );
		recipe.dateCreation = new Date();
		recipe.imageName = form.imageName;
		recipe.tags = form.tags;
		recipe.isPrivate = true;
		recipe.rating = 0;
		recipe.visiting = 0;
		recipe.save();
		// recipe.saveManyToManyAssociations( "user" );
		return ok( index.render( Recipe.byUser( user ) ) );
	}
	
	@Restrict( @Group( UserRole.USER_ROLE ) )
	public static Result editRecipe( final Long idRecipe ) {
		return ok( editRecipe.render( RECIPE_DESIGN_FORM.fill( Recipe.byId( idRecipe ) ) ) );
	}
	
	@Restrict( @Group( UserRole.USER_ROLE ) )
	public static Result doEditRecipe() {
		final Form< Recipe > formBinding = RECIPE_DESIGN_FORM.bindFromRequest();
		if ( formBinding.hasErrors() ) {
			flash( Application.FLASH_ERROR_KEY, "--Bad input for new Recipe--" );
			return badRequest( editRecipe.render( formBinding ) );
		}
		final Recipe form = formBinding.get();
		final Recipe recipe = Recipe.byId( form.id );
		recipe.description = form.description;
		recipe.title = form.title;
		recipe.link = form.link;
		recipe.isPrivate = form.isPrivate;
		recipe.save();
		return ok( index.render( Recipe.byUser( Application.getLocalUser( session() ) ) ) );
	}
}
