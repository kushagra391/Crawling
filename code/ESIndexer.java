import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;


public class ESIndexer {

	final static String indexName = "team_index";
	final static String documentType = "document";
	final static String fieldTitle = "title";
	final static String fieldText = "text";
	final static String fieldHtml = "html";
	final static String fieldOutlinks = "outlinks";

//	private static int LIMIT = 19617;
	private static int LIMIT = 14653;
	// final static String fieldDocNo = "contents";

	public static void main(String[] args) throws IOException {

		System.out.println("Index Started");

		/*********** Pre - Index Steps [ES Ready] ***********/

		// create client
		final Client client = getClient();

		// placeholder for pre-index operations
		final CreateIndexRequestBuilder createIndexRequestBuilder = client
				.admin().indices().prepareCreate(indexName);

		/* --- Add mapping and Settings have been added to the index --- */

		// Add Mapping
		XContentBuilder mappingBuilder = createMapper();
		createIndexRequestBuilder.addMapping(documentType, mappingBuilder); // mapping
		// Add Setting
		XContentBuilder settingsBuilder = createSetting();
		createIndexRequestBuilder.setSettings(settingsBuilder); // setting added
		createIndexRequestBuilder.execute().actionGet(); // CreateIndexRequestBuilder

		/* --- Mapping and Settings have been added to the index --- */

		/************ Prepare Index [process] **********************/

		DocAttributes docAttributes = new DocAttributes();

		for (int i = 0; i < LIMIT; i++) {
//		for (int i = 0; i < 1000; i++) {
			String filePath = ESParser.freshFilePrefixPath + ("" + i) + ".txt";
			docAttributes = ESParser.createDocAttributes(filePath);
			System.out.println(filePath + " --> read");

			// TODO: Process the doc read for ES
			addToIndex(docAttributes, client);

		}

		/*************** Post Preparation Steps [submit] ***************/
		
		System.out.println("Index Created");
		client.close();

	}

	private static void addToIndex(DocAttributes da, Client client) {

		Map<String, Object> json = new HashMap<String, Object>(); // placeholder

		String id = da.docno;
		json.put("title", da.title);
		json.put("text", da.text);
		json.put("html", da.html);
		json.put("outlinks", da.outlinks);
		json.put("inlinks", da.inlinks);

		IndexRequestBuilder indexRequestBuilder;
		indexRequestBuilder = client.prepareIndex(indexName, documentType, id);
		indexRequestBuilder.setSource(json);
		indexRequestBuilder.execute().actionGet();

	}

	private static Client getClient() {

		Node node = nodeBuilder().clusterName("informationcluster").node();
		Client client = node.client(); // create a client of that node

		return client;

	}

	private static XContentBuilder createSetting() throws IOException {

		// String stops[] = FileParser.getStopWords();
		XContentBuilder settingsBuilder = jsonBuilder()
				.startObject()
				.startObject("analysis")
				.startObject("analyzer")
				.startObject("fulltext_analyzer")
				.field("type", "standard")
				.field("tokenizer", "whitespace")
				.field("filter",
						new String[] { "type_as_payload", "lowercase" })
				// .field("stopwords", stops)
				.endObject().endObject().endObject();

		return settingsBuilder;

	}

	private static XContentBuilder createMapper() throws IOException {

		XContentBuilder mappingBuilder = jsonBuilder().startObject()
				.startObject(documentType).startObject("properties")
				.startObject("contents").field("type", "string")
				.field("index_analyzer", "fulltext_analyzer")
				.field("term_vector", "with_positions_offsets_payloads")
				.field("store", true).endObject().endObject().endObject()
				.endObject();

		return mappingBuilder;
	}

}
