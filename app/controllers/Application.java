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
        return badRequest("リクエスト構文に誤りがあります。");
    }
  
	public static Result asJson(String from, String to) {

		MinFareGetAPIResult apiResult = new MinFareGetAPI().request(from, to);

		ObjectNode result = Json.newObject();
		result.put("status", Integer.toString(apiResult.statusCode));
		result.put("fare",   Integer.toString(apiResult.fare));
		result.put("url",    apiResult.url);

		return ok(result);
	}
}
