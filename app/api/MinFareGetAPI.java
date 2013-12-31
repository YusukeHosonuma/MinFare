package api;

import java.io.*;
import java.net.*;

import java.util.regex.*;

/**
 * Yahoo!路線情報のページから区間を指定して最安値を取得するAPI。
 */
public class MinFareGetAPI {

	/**
	 * APIの実行結果。
	 */
	public static class MinFareGetAPIResult {

		public final int statusCode;
		public final int fare;
		public final String url;

		public MinFareGetAPIResult(int statusCode, int fare, String url) {
			this.statusCode = statusCode;
			this.fare       = fare;
			this.url        = url;
		}
	}

	/**
	 * リクエスト用のURLを組み立てるビルダー。
	 */
	public static class URLBuilder {

		private String from;
		private String to;

		public URLBuilder(String from, String to) {
			try {
				this.from = URLEncoder.encode(from, "UTF-8");
				this.to   = URLEncoder.encode(to,   "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// do nothing (Don't happend at target environment)
			}
		}

		public String build() {
			String urlString = "http://transit.loco.yahoo.co.jp/search/result?"
						+ "from=" + from + "&"
						+ "flatlon=&"
						+ "to=" + to + "&"
						+ "via=&expkind=1&ym=201303&d=20&datepicker=&hh=10&m1=1&m2=1&type=1&ws=2&s=1&x=101&y=12&kw=";
			return urlString;
		}
	}

	/**
	 * 最安値情報を取得します。
	 * 
	 * @param from 出発地
	 * @param to 目的地
	 * @return 結果
	 */
	public MinFareGetAPIResult request(String from, String to) {

		// 以下がそれぞれ正しく取得できなかった場合のAPI返却値
		int statusCode = -1;
		int resultFare = -1;

		String urlString = new URLBuilder(from, to).build();

		try {
			HttpURLConnection http = (HttpURLConnection) new URL(urlString).openConnection();
			http.setRequestMethod("GET");
			http.connect();

			statusCode = http.getResponseCode();

			// 200以外は中断
			if (statusCode != HttpURLConnection.HTTP_OK) {
				return new MinFareGetAPIResult(statusCode, resultFare, urlString);
			}

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
			// do nothing
		}

		return new MinFareGetAPIResult(statusCode, resultFare, urlString);
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
