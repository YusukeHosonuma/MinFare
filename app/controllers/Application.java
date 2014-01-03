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

	public static Result asXml(String from, String to) {

		MinFareGetAPIResult apiResult = new MinFareGetAPI().request(from, to);

		StringBuilder xmlSb = new StringBuilder();
		xmlSb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlSb.append("<Result>");
		xmlSb.append("<Status>" + apiResult.statusCode + "</Status>");
		xmlSb.append("<Fare>" + apiResult.fare + "</Fare>");
		xmlSb.append("<URL><![CDATA[" + apiResult.url + "]]></URL>");
		xmlSb.append("</Result>");

		return ok(xmlSb.toString()).as("application/xml; charset=UTF-8");
	}
}
