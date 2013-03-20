package controllers;

import java.io.*;
import java.net.*;

import java.util.regex.*;

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
		// JsonNode json = request().body().asJson();

		String resultFare = null;

		try {
			String from_e = URLEncoder.encode(from, "UTF-8");
			String to_e   = URLEncoder.encode(to,   "UTF-8");

			String urlStr = "http://transit.loco.yahoo.co.jp/search/result?"
						+ "from=" + from_e + "&"
						+ "flatlon=&"
						+ "to=" + to_e + "&"
						+ "via=&expkind=1&ym=201303&d=20&datepicker=&hh=10&m1=1&m2=1&type=1&ws=2&s=1&x=101&y=12&kw=";

			URL url = new URL(urlStr);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.connect();

			try (BufferedReader br = new BufferedReader( // AutoClosable
				new InputStreamReader(http.getInputStream()))
			) {
				String line;
				while ((line = br.readLine()) != null) {
					if (line.contains("route-fare-on")) {
						resultFare = getFare(line);
						break;
					}
				}
			}

		} catch (IOException e) {
			return badRequest();
		}

		ObjectNode result = Json.newObject();

		if (resultFare != null) {
			result.put("status", "OK");
			result.put("fare", resultFare);
		} else {
			result.put("status", "NG");
			result.put("fare", "");			
		}

		return ok(result);
	}

	private static String getFare(String fareLine) {
		Pattern p = Pattern.compile("[0-9]+円");
		Matcher m = p.matcher(fareLine);
		if (m.find()) {
			return m.group();
		} else {
			return null;
		}
	} 
}
