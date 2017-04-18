package fitec.swag.fil_rouge_crowdfunding_scoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		// Validate.isTrue(args.length == 1, "usage: supply url to fetch");
		// String url = args[0];
		String url = "https://www.kickstarter.com/discover/advanced?woe_id=0&sort=newest&seed=2487379&page=";

		// get urls of all projects from the first page (search with empty query
		// and order by newest)

		getJSON1(url);

	}

	/**
	 * Récupère les urls des projets depuis la page des résultats de recherche
	 * puis requête la page du projet et récupère le JSON dessus
	 */
	public static void getJSON1(String baseUrl) {
		int nbPage = 15;
		while (true) {
			String url = baseUrl + nbPage;
			try {
				// récupération des urls des 20 premiers projets
				// String url =
				// "https://www.kickstarter.com/discover/advanced?ref=discovery_overlay";
				Document doc;
				doc = Jsoup.connect(url).get();
				Elements scriptTags = doc.getElementsByClass("project-thumbnail-wrap");

				// récupération du JSON contenant les infos de chaque projet
				Collection<JSONObject> collection = new ArrayList<>();
				for (int i = 0; i < scriptTags.size(); i++) {
					Element element = scriptTags.get(i);
					String urlProjet = "https://www.kickstarter.com" + element.attr("href");
					collection.add(buildJSONObject(urlProjet));
				}

				// constitution du JSON
				JSONObject result = new JSONObject();
				result.put("url", url);
				result.put("results", collection);
				result.put("date", new Date().toString());

				String fileName = "kickstarter.JsonBase." + getFormattedDate() + ".page" + nbPage;
				FileUtils.write(new File(fileName), result.toString(), StandardCharsets.UTF_8);
				nbPage++;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

	private static String getFormattedDate() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		return format1.format(cal.getTime());
	}

	public static JSONObject buildJSONObject(String url) throws IOException {
		Document doc;
		JSONObject jsonObject = null;
		doc = Jsoup.connect(url).get();
		Elements scriptTags = doc.getElementsByTag("script");
		for (Element tag : scriptTags) {
			for (DataNode node : tag.dataNodes()) {
				BufferedReader reader = new BufferedReader(new StringReader(node.getWholeData()));
				String line = null;
				do {
					line = reader.readLine();
					if (line != null && line.startsWith("  window.current_project")) {
						String jsonEncoded = line.substring(28, line.length() - 2);
						String jsonDecoded = StringEscapeUtils.unescapeHtml4(jsonEncoded).replaceAll("\\\\\\\\",
								"\\\\");
						jsonObject = new JSONObject(jsonDecoded);
						// System.out.println(jsonObject.keySet());
					}
				} while (line != null);
				reader.close();
			}
		}

		return jsonObject;
	}

}
