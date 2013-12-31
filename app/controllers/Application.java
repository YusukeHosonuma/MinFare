package controllers;

import static api.MinFareGetAPI.MinFareGetAPIResult;

import api.MinFareGetAPI;

import play.*;
import play.mvc.*;
import play.libs.*;

import views.html.*;

import org.codehaus.jackson.node.ObjectNode;

public class Application extends Controller {
  
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
  
	public static Result asJson(String from, String to) {

		MinFareGetAPIResult apiResult = new MinFareGetAPI().request(from, to);

		ObjectNode result = Json.newObject();

		if (apiResult.status) {
			result.put("status", "OK");
			result.put("fare", Integer.toString(apiResult.fare));
		} else {
			result.put("status", "NG");
			result.put("fare", "");
		}
		result.put("title", resultTitle);

		return ok(result);
	}
}
