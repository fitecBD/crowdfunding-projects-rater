package fitec.swag.fil_rouge_crowdfunding_scoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
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

		// get urls of all projects from the first page (search with empty query
		// and order by newest)

		try {
			// récupération des urls des 20 premiers projets
			String url = "https://www.kickstarter.com/discover/advanced?sort=newest";
			Document doc;
			JSONObject jsonObject = null;
			doc = Jsoup.connect(url).get();
			Elements scriptTags = doc.getElementsByClass("project-thumbnail-wrap");

			// récupération du JSON contenant les infos de chaque projet
			Collection<JSONObject> collection = new ArrayList<>();
			for (int i = 0; i < scriptTags.size(); i++) {
				Element element = scriptTags.get(i);
				String urlProjet = "https://www.kickstarter.com" + element.attr("href");
				collection.add(buildJSONObject(urlProjet));
				String string = "\"";
			}
			FileUtils.write(new File("output.json"), new JSONArray(collection).toString(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void buildPropsMapFromJSON(String url) throws IOException {
		// Document doc;
		// doc = Jsoup.connect(url).get();
		Map<String, Object> props = new HashMap<>();
		JSONObject jsonObject = buildJSONObject(url);

		// id
		props.put(CONSTANTS.META_PROJECT_ID, jsonObject.getInt("id"));

		// name
		props.put(CONSTANTS.META_NAME, jsonObject.getString("name"));
		// Elements imports = doc.select("meta[property='og:title']");
		// if (!imports.isEmpty()) {
		// props.put(CONSTANTS.META_NAME, imports.get(0).attr("content"));
		// } else {
		// System.out.println("coucou");
		// }
		// url
		props.put(CONSTANTS.META_URL, jsonObject.getJSONObject("urls").getJSONObject("web").getString("project"));

		// category
		props.put(CONSTANTS.META_CATEGORY, jsonObject.getJSONObject("category").getString("name"));

		// subcategory
		props.put(CONSTANTS.META_SUBCATEGORY, null);

		// location
		props.put(CONSTANTS.META_LOCATION, jsonObject.getJSONObject("location").getString("short_name"));

		// status
		props.put(CONSTANTS.META_STATUS, jsonObject.getString("state"));

		// goal
		props.put(CONSTANTS.META_GOAL, jsonObject.getInt("goal"));

		// pledged
		props.put(CONSTANTS.META_PLEDGED, jsonObject.getInt("pledged"));

		// funded percentage
		props.put(CONSTANTS.META_FUNDED_PERCENTAGE, null);

		// backers
		props.put(CONSTANTS.META_BACKERS, jsonObject.getInt("backers_count"));

		// funded date
		props.put(CONSTANTS.META_FUNDED_DATE, null);

		// levels
		props.put(CONSTANTS.META_LEVELS, jsonObject.getJSONArray("rewards").length());

		// reward level
		props.put(CONSTANTS.META_REWARD_LEVELS, null);

		// updates
		props.put(CONSTANTS.META_UPDATES, jsonObject.getInt("updates_count"));

		// comments
		props.put(CONSTANTS.META_COMMENTS, jsonObject.getInt("comments_count"));

		// duration
		props.put(CONSTANTS.META_SUBCATEGORY, null);

		// System.out.println(props);

		JSONObject jsonObject2 = new JSONObject(props);
		System.out.println(jsonObject2.toString());
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
			}
		}

		return jsonObject;
	}

	public static void mapToJSON(Map<String, String> map) {

	}

	public static void mapToCSV(Map<String, String> map) {

	}

}
