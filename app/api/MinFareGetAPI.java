package api;

import java.io.*;
import java.net.*;

import java.util.regex.*;

public class MinFareGetAPI {

	/**
	 * APIの実行結果
	 */
	public static class MinFareGetAPIResult {
		public final boolean status;
		public final int fare;
		public MinFareGetAPIResult(boolean status, int fare) {
			this.status = status;
			this.fare = fare;
		}
	}

	/**
	 * Yahoo!路線情報から最安値情報を取得します。
	 * 
	 * @param from 出発地
	 * @param to 目的地
	 * @return 結果
	 */
	public MinFareGetAPIResult request(String from, String to) {

		String resultTitle = null;
		int resultFare = -1;

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
				new InputStreamReader(http.getInputStream(), "UTF-8"))
			) {
				String line;
				while ((line = br.readLine()) != null) {
					if (line.contains("route-fare-")) {
						resultFare = getFareFromLine(line);
						break;
					}
				}
			}

		} catch (IOException e) {
			return new MinFareGetAPIResult(false, -1);
		}

		if (resultFare == -1) {
			return new MinFareGetAPIResult(false, -1);
		} else {
			return new MinFareGetAPIResult(true, resultFare);
		}
	}

	private int getFareFromLine(String fareLine) {
		Pattern p = Pattern.compile("([0-9]+)円");
		Matcher m = p.matcher(fareLine);
		if (m.find()) {
			return Integer.parseInt(m.group(1));
		} else {
			return -1;
		}
	} 
}
