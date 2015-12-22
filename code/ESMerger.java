/**
 * 
 */
//package ir.test;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;

/**
 * @author Sagar6903
 *
 */
public class ESMerger {
	
	//Helpers.INDEX_NAME is the index name of your local index (not the team index ) 
	public static final String TEAM_INDEX = "test_sdk";
	public static Client client;
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException{
		
		Node node = NodeBuilder.nodeBuilder().clusterName("informationcluster").node();
		client = node.client();
		
		System.out.println("start");
		searchAndMergeIndex();
		System.out.println("complete");
		//mergeAndUpdateInLinks("http://en.wikipedia.org/wiki/Robert_H._Michel", "Check	It");
		//mergeAndUpdateInLinks("http://en.wikipedia.org/wiki/Anne,_Queen_of_Great_Britain", "Hello	I	am	Darshan	Patel");
		client.close();
		node.close();
	}
	
	public static void searchAndMergeIndex() throws IOException, InterruptedException, ExecutionException{ 
		// Helpers.INDEX_NAME is your own local index ( not the team one )
		// Scroll size has been set to 100, since setting to 1000 or more leads to 
		// java heap out of memory error 
		SearchResponse searchResp = client.prepareSearch("hw3_test_kv").setTypes("hw_3_dataset")
				.setScroll(new TimeValue(2000000))
				.setQuery(QueryBuilders.matchAllQuery()).setExplain(true)
				.setSize(100).execute().actionGet();
		String inlinksFromTeamIndex = "";
		int i=1;
		while (true) {
			for (SearchHit hit : searchResp.getHits().getHits()) {
				// check whether the docno/url of your own local index exists in TEAM INDEX
				inlinksFromTeamIndex = doesExistsInTeamIndex(hit.getId());
				//System.out.println(hit.getId());
				System.out.println(i++);
				
				if(inlinksFromTeamIndex.isEmpty()) // case 1 : that docno doesnot exists in team index, so create a new one.
					addToTeamIndex(hit);
				else {
					// case 2 : docno already exists in team index, so just update the inlinks ( removing duplicates )
					String mergedInLinks = getUniqueInLinks(hit.getSource().get("inlinks").toString(), inlinksFromTeamIndex);
					mergeAndUpdateInLinks(hit.getId(), mergedInLinks);
				}
			
			}

			searchResp = client.prepareSearchScroll(searchResp.getScrollId())
					.setScroll(new TimeValue(2000000)).execute().actionGet();
			
			if (searchResp.getHits().getHits().length == 0) {
				break;
			} 
//			break;
		}
	}
	
	public static String doesExistsInTeamIndex(String docNo) {
		SearchResponse search = client
				.prepareSearch(TEAM_INDEX)
				.setTypes("document")
				.setQuery(QueryBuilders.matchQuery("docno", docNo))
				.setExplain(true).execute().actionGet();
		String inlinks = "";
		if(search.getHits().getTotalHits() > 0){
			for (SearchHit hit : search.getHits().getHits()) {
				inlinks = (String) hit.getSource().get("inlinks");
			}
		}
		return inlinks;
	}
	
	public static void mergeAndUpdateInLinks(String docNo, String inlinks) throws IOException, InterruptedException, ExecutionException{
		UpdateRequest updateRequest = new UpdateRequest(TEAM_INDEX,
				"document", docNo).doc(jsonBuilder().startObject()
				.field("inlinks", inlinks).endObject());
		client.update(updateRequest).get();
		//System.out.println("Operation is completed");
	}
	
	@SuppressWarnings("unused")
	public static void addToTeamIndex(SearchHit hit) throws IOException{
		XContentBuilder content = jsonBuilder().startObject()
				.field("docno", hit.getId())
				.field("text", hit.getSource().get("text"))
				.field("title", hit.getSource().get("url_head"))
				.field("html", hit.getSource().get("html_source"))
				.field("outlinks", hit.getSource().get("outlinks"))
				// assuming in your own index you would not have any duplicates in inlinks !!!! 
				.field("inlinks", hit.getSource().get("inlinks")).endObject();

	
		IndexResponse response = client
				.prepareIndex(TEAM_INDEX, "document", hit.getId())
				.setSource(content)
				.execute().actionGet();
	}
	
	public static String getUniqueInLinks(String inlinksFromOwnIndex, String inlinksFromTeamIndex) {
		LinkedHashMap<String, Integer> uniqueInLinksMap = new LinkedHashMap<String, Integer>();
		for(String inlink : inlinksFromOwnIndex.split("\t")){
			uniqueInLinksMap.put(inlink, 1);
		}
		
		for(String inlink : inlinksFromTeamIndex.split("\t")){
			uniqueInLinksMap.put(inlink, 1);
		}
		StringBuilder inlinks = new StringBuilder();
		for(Map.Entry<String,Integer> pair : uniqueInLinksMap.entrySet()){
			inlinks.append(pair.getKey()+"\t");
		}
		return inlinks.toString();
		/*
		return Arrays.asList(uniqueInLinksMap.keySet()).toString().replace("[", "")
				.replace("]", "").replace(",", "")
				.replace(" ", Helpers.TAB_SEPARATOR);
		*/
	}
}
