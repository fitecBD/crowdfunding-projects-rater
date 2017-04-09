package fitec.swag.fil_rouge_crowdfunding_scoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

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
		String url = "https://www.kickstarter.com/projects/geniusgames/cytosis-a-cell-biology-board-game?ref=discovery";
		// "https://www.kickstarter.com/projects/alltheanime/mind-game/description";
		try {

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

			// Elements imports = doc.select("meta[href]");
			// System.out.println(imports.size());
			// link.tagName(),link.attr("abs:href"), link.attr("rel")
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
						String jsonDecoded = StringEscapeUtils.unescapeHtml4(jsonEncoded);
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
