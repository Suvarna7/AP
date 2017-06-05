package iit.pc.javainterface;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;




@SuppressWarnings("deprecation")
public class MyHttpClient {

	private HttpClient   httpClient;

	public MyHttpClient(){
		//httpClient = HttpClientBuilder.create().build();
		httpClient = new DefaultHttpClient();
		//Close
	}

	public boolean executePOST  (HttpHost host, ArrayList<NameValuePair>postParameters,ResponseHandler<String> handler)throws ClientProtocolException, IOException {
		// Prepare the POST request
		//System.out.println(params);
		//ArrayList<NameValuePair>postParameters = new ArrayList<NameValuePair>();
		//postParameters.add(new BasicNameValuePair(BMBridge.JSON_ID, params));

		HttpPost post = new HttpPost(host.getHostName());

		HttpEntity ent = new UrlEncodedFormEntity(postParameters, "utf-8");
		post.setEntity(ent);
		//System.out.println(post.getEntity().getContentEncoding() );
		// TODO Auto-generated catch block




		//Send the request
		
			httpClient.execute(post,handler);

			//httpClient.execute(target, request, callback);
			//HttpResponse response = httpClient.execute(post);
			//String resp = httpClient.execute(post, handler);
			//System.out.println(resp);
			/*System.out.println(response.getStatusLine().toString());
			System.out.println(response.getAllHeaders());
			InputStream in = response.getEntity().getContent();*/


		

		return true;
	}
	public void closeConnection(){
		//cManager.closeExpiredConnections(); 	
		//cManager. 
	}


}
