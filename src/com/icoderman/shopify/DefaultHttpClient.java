package com.icoderman.shopify;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.entity.ContentType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultHttpClient implements HttpClient {

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";

	private CloseableHttpClient httpClient;
	private ObjectMapper mapper;
	private String nextPageInfo;
	
	
	public DefaultHttpClient(String consumerKey, String consumerSecret) {
		// yogan naidoo changed
		CredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(consumerKey,
				consumerSecret));
		this.httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
		this.mapper = new ObjectMapper();
	}

	@Override
	public Map<?, ?> get(String url) {
		HttpGet httpGet = new HttpGet(url);
		return getEntityAndReleaseConnection(httpGet, Map.class);
	}

	@Override
	public LinkedHashMap<?, ?> getAll(String url) {
		HttpGet httpGet = new HttpGet(url);
		return getEntityAndReleaseConnection(httpGet, LinkedHashMap.class);
	}

	// yogan naidoo changed
	@Override
	public LinkedHashMap<?, ?> getAll(URIBuilder builder) {
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(builder.build());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getEntityAndReleaseConnection(httpGet, LinkedHashMap.class);
	}

	@Override
	public Map<?, ?> post(String url, Map<String, String> params, Map<String, Object> object) {
		List<NameValuePair> postParameters = getParametersAsList(params);
		HttpPost httpPost;
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			uriBuilder.addParameters(postParameters);
			httpPost = new HttpPost(uriBuilder.build());
			httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
			return postEntity(object, httpPost);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<?, ?> put(String url, Map<String, String> params, Map<String, Object> object) {
		List<NameValuePair> postParameters = getParametersAsList(params);
		HttpPut httpPut;
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			uriBuilder.addParameters(postParameters);
			httpPut = new HttpPut(uriBuilder.build());
			httpPut.setHeader(CONTENT_TYPE, APPLICATION_JSON);
			return postEntity(object, httpPut);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<?, ?> delete(String url, Map<String, String> params) {
		List<NameValuePair> postParameters = getParametersAsList(params);
		HttpDelete httpDelete;
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			uriBuilder.addParameters(postParameters);
			httpDelete = new HttpDelete(uriBuilder.build());
			return getEntityAndReleaseConnection(httpDelete, Map.class);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<?, ?> postEntity(Map<String, Object> objectForJson, HttpEntityEnclosingRequestBase httpPost) {
		try {
			HttpEntity entity = new ByteArrayEntity(this.mapper.writeValueAsBytes(objectForJson),
					ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			return getEntityAndReleaseConnection(httpPost, Map.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private List<NameValuePair> getParametersAsList(Map<String, String> params) {
		List<NameValuePair> postParameters = new ArrayList<>();
		if (params != null && params.size() > 0) {
			for (String key : params.keySet()) {
				postParameters.add(new BasicNameValuePair(key, params.get(key)));
			}
		}
		return postParameters;
	}

	private <T> T getEntityAndReleaseConnection(HttpRequestBase httpRequest, Class<T> objectClass) {
		try {
			CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity httpEntity = httpResponse.getEntity();
			// yogan naidoo added
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (httpEntity == null || (!(statusCode >= 200 && statusCode < 300))) {
				throw new RuntimeException("Error retrieving results from http request" + "- Status Code: " + statusCode
						+ "\r\n" + " Response: "+httpResponse.getStatusLine().getReasonPhrase());
			}
			setNextPageLink(null);
			if(httpResponse.getFirstHeader("link")!=null) {
				String linkvalue = httpResponse.getFirstHeader("link").getValue();
				for(String link:linkvalue.split(",")) {
					if (link.contains("rel=\"next\"")) {
						String page_info = null;
						int startIndex = link.lastIndexOf("page_info");
						if(startIndex!=-1) {
							startIndex += "page_info".length()+1;
							int endIndex = link.lastIndexOf(">");
							if(endIndex!=-1) {
								page_info = link.substring(startIndex, endIndex);
									setNextPageLink(page_info);
								}
							}
					}
				}
				
				
				
				
			}
			
			Object result = mapper.readValue(httpEntity.getContent(), Object.class);
			if (objectClass.isInstance(result)) {
				return objectClass.cast(result);
			}
			throw new RuntimeException("Can't parse retrieved object: " + result.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			httpRequest.releaseConnection();
		}
	}
	
	private void setNextPageLink(String info) {
		this.nextPageInfo = info;	}
	public  String getNextPageLink() {
		return this.nextPageInfo;
	}
	
}
