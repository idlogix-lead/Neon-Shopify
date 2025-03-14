package za.co.ntier.processes;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

import com.icoderman.shopify.ApiVersionType;
import com.icoderman.shopify.DefaultHttpClient;
import com.icoderman.shopify.ShopifyAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;

import za.co.ntier.model.MLocation;
import za.co.ntier.model.X_zz_shopify;

public class SyncShopifyLocation extends SvrProcess {

	PO sfDefaults;
	com.icoderman.shopify.Shopify shopify;
	
	
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub
		String whereClause = " isactive = 'Y' AND AD_Client_ID = ?";
		sfDefaults = new Query(getCtx(), X_zz_shopify.Table_Name, whereClause, null)
				.setParameters(new Object[] { Env.getAD_Client_ID(getCtx()) }).firstOnly();
		if (sfDefaults == null)
			throw new IllegalStateException("/nShopify Defaults need to be set on iDempiere /n");
		DefaultHttpClient client = new DefaultHttpClient((String) sfDefaults.get_Value("consumerkey"),
				(String) sfDefaults.get_Value("consumerSecret"));
		URIBuilder builder = null;
		try {
			builder = new URIBuilder((String) sfDefaults.get_Value("url") + "/admin/api/2024-01/locations.json");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			
		}
		
		OAuthConfig config = new OAuthConfig((String) sfDefaults.get_Value("url"),
				(String) sfDefaults.get_Value("consumerkey"), (String) sfDefaults.get_Value("consumersecret"));
		shopify = new ShopifyAPI(config, ApiVersionType.V1);
		
		Map<?, ?> locations =client.getAll(builder);
		 List<?> location = (List<?>) locations.get("locations");
		 for (Object locationObj : location) {
             Map<?, ?> shoplocation = (Map<?, ?>) locationObj;
              MLocation locate = new MLocation(getCtx(), 0, null);
              locate.setValue(String.valueOf(shoplocation.get("id")));
              locate.setName(String.valueOf(shoplocation.get("name")));
              locate.setCity(String.valueOf(shoplocation.get("city")));
              locate.setCountryName(String.valueOf(shoplocation.get("country_name")));
              locate.setAddress1(String.valueOf(shoplocation.get("address1")));
              locate.setPhone(String.valueOf(shoplocation.get("phone")));
              locate.saveEx();
              
		 }
		
		System.out.println(location);

		return null;
	}

}
