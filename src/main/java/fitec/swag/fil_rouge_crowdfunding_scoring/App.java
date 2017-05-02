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
		// String baseUrl =
		// "https://www.kickstarter.com/discover/advanced?woe_id=23424819&sort=magic&seed=2488399&page=";
		// String outputFileBase = "kickstarter.scotland-uk." +
		// getFormattedDate();
		// getJSON1(baseUrl, outputFileBase, 1);

		int nbPage = 172;
		String baseUrl = "https://www.kickstarter.com/discover/advanced?woe_id=23424829&sort=newest&seed=2488399&page="
				+ nbPage;
		String outputFileName = "C:/Users/Fitec/workspace/crowdfunding-projects-rater/all_results/Deutschland/Deutschland_23424829.page"
				+ nbPage + ".json";
		try {
			getJSON1Inner(baseUrl, "test_results/Deutschland", nbPage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Récupère les urls des projets depuis la page des résultats de recherche
	 * puis requête la page du projet et récupère le JSON dessus
	 */
	public static void getJSON1(String baseUrl, String outputFileBase, int startPage) {
		try {
			getJSON1InnerLocation(baseUrl, outputFileBase, startPage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getJSON1Inner(String baseUrl, String outputFileBase, int nbPage) throws IOException {
		Elements scriptTags = null;
		do {
			String url = baseUrl + nbPage;
			System.out.println("scraping page : " + url);
			Document doc;
			doc = Jsoup.connect(url).get();
			// scriptTags =
			// doc.getElementsByClass("project-thumbnail-wrap");
			scriptTags = doc.getElementsByAttribute("data-project_pid");

			if (!scriptTags.isEmpty()) {
				// récupération du JSON contenant les infos de chaque projet
				Collection<JSONObject> collection = new ArrayList<>();
				for (int i = 0; i < scriptTags.size(); i++) {
					Element element = scriptTags.get(i);
					String urlProjet = "https://www.kickstarter.com"
							+ element.getElementsByAttribute("href").attr("href");
					collection.add(buildJSONObject(urlProjet));
				}

				// constitution du JSON
				JSONObject result = new JSONObject();
				result.put("url", url);
				result.put("results", collection);
				result.put("date", new Date().toString());

				String fileName = outputFileBase + ".page" + nbPage + ".json";
				FileUtils.write(new File("france", fileName), result.toString(), StandardCharsets.UTF_8);
			}
			nbPage++;
		} while (scriptTags != null && !scriptTags.isEmpty());
	}

	private static void getJSON1InnerLocation(String baseUrl, String outputFileBase, int nbPage) throws IOException {
		Elements scriptTags = null;
		do {
			String url = baseUrl + nbPage;
			System.out.println("scraping page : " + url);
			Document doc;
			doc = Jsoup.connect(url).get();
			// scriptTags =
			// doc.getElementsByClass("project-thumbnail-wrap");
			scriptTags = doc.getElementsByAttribute("data-project_pid");

			if (!scriptTags.isEmpty()) {
				// récupération du JSON contenant les infos de chaque projet
				Collection<JSONObject> collection = new ArrayList<>();
				for (int i = 0; i < scriptTags.size(); i++) {
					Element element = scriptTags.get(i);
					String urlProjet = "https://www.kickstarter.com"
							+ element.getElementsByAttribute("href").attr("href");
					collection.add(buildJSONObject(urlProjet));
				}

				// constitution du JSON
				JSONObject result = new JSONObject();
				result.put("url", url);
				result.put("results", collection);
				result.put("date", new Date().toString());

				String fileName = outputFileBase + ".page" + nbPage + ".json";
				FileUtils.write(new File("france", fileName), result.toString(), StandardCharsets.UTF_8);
			}
			nbPage++;
		} while (scriptTags != null && !scriptTags.isEmpty());
	}

	private static void getOnePage(String url, String destDir, String outputFileName) throws IOException {
		Elements scriptTags = null;
		System.out.println("scraping page : " + url);
		Document doc;
		doc = Jsoup.connect(url).get();
		// scriptTags =
		// doc.getElementsByClass("project-thumbnail-wrap");
		scriptTags = doc.getElementsByAttribute("data-project_pid");

		if (!scriptTags.isEmpty()) {
			// récupération du JSON contenant les infos de chaque projet
			Collection<JSONObject> collection = new ArrayList<>();
			for (int i = 0; i < scriptTags.size(); i++) {
				Element element = scriptTags.get(i);
				String urlProjet = "https://www.kickstarter.com" + element.getElementsByAttribute("href").attr("href");
				collection.add(buildJSONObject(urlProjet));
			}

			// constitution du JSON
			JSONObject result = new JSONObject();
			result.put("url", url);
			result.put("results", collection);
			result.put("date", new Date().toString());

			FileUtils.write(new File(destDir, outputFileName), result.toString(), StandardCharsets.UTF_8);
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
