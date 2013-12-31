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
		public final boolean status;
		public final int fare;
		public MinFareGetAPIResult(boolean status, int fare) {
			this.status = status;
			this.fare = fare;
		}
	}

	/**
	 * リクエスト用のURLを組み立てるビルダー。
	 */
	public static class URLBuilder {

		private final String from;
		private final String to;

		public URLBuilder(String from, String to) throws UnsupportedEncodingException {
			this.from = URLEncoder.encode(from, "UTF-8");
			this.to   = URLEncoder.encode(to,   "UTF-8");
		}

		public URL build() throws MalformedURLException {
			String urlStr = "http://transit.loco.yahoo.co.jp/search/result?"
						+ "from=" + from + "&"
						+ "flatlon=&"
						+ "to=" + to + "&"
						+ "via=&expkind=1&ym=201303&d=20&datepicker=&hh=10&m1=1&m2=1&type=1&ws=2&s=1&x=101&y=12&kw=";
			return new URL(urlStr);
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

		int resultFare = -1;

		try {
			URL url = new URLBuilder(from, to).build();

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
