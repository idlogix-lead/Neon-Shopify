package za.co.ntier.processes;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

import com.icoderman.shopify.ApiVersionType;
import com.icoderman.shopify.DefaultHttpClient;
import com.icoderman.shopify.EndpointBaseType;
import com.icoderman.shopify.ShopifyAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;

import za.co.ntier.model.MParentProduct;
import za.co.ntier.model.X_zz_shopify;

public class UploadShopifyStock extends SvrProcess {

	String Parent_Product_ID=null;
	String Product_ID=null;
	String Location_ID="";
	String Stock="";
	com.icoderman.shopify.Shopify shopify;
	PO sfDefaults;
	
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("M_Parent_Product_ID"))
				Parent_Product_ID = para[i].getParameterAsString();
			else if (name.equals("M_Product_ID"))
				Product_ID = para[i].getParameterAsString();
			else if (name.equals("M_Location_ID"))
				Location_ID = para[i].getParameterAsString();
			else if (name.equals("stock"))
				Stock = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);	
		}
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
	    OAuthConfig config = new OAuthConfig((String) sfDefaults.get_Value("url"),
				(String) sfDefaults.get_Value("consumerkey"), (String) sfDefaults.get_Value("consumersecret"));
		shopify = new ShopifyAPI(config, ApiVersionType.V1);
		
		if(Parent_Product_ID != null) {
			
			 MParentProduct parentProduct = new MParentProduct(getCtx(), Parent_Product_ID, get_TrxName());

			String ParentproductId= parentProduct.getValue();
			Map<?,?> ParentProduct = shopify.get(EndpointBaseType.PRODUCTS.getValue(), ParentproductId);
			 List<?> variants = (List<?>) ParentProduct.get("variants");
		       
		        for (Object variant : variants) {
		            Map<?, ?> variantMap = (Map<?, ?>) variant;
		            Object inventoryItemId = variantMap.get("inventory_item_id");
		            System.out.println("Inventory Item ID: " + inventoryItemId);
		        }
		}else if (Product_ID != null) {
			Map<?,?> variant = shopify.get(EndpointBaseType.VARIANT.getValue(), Product_ID);
			Object inventoryItemId = variant.get("inventory_item_id");
	        System.out.println("Inventory Item ID: " + inventoryItemId);
		}else {
			throw new IllegalStateException("Please select a parent product or variant to process");
		}
	
		return "Process completed successfully";
	}

	
	 private void UpdateVariantStockOnShopify(Object inventoryItemId, String Location_ID ) throws Exception {
		    String whereClause = " isactive = 'Y' AND AD_Client_ID = ?";
			sfDefaults = new Query(getCtx(), X_zz_shopify.Table_Name, whereClause, null)
					.setParameters(new Object[] { Env.getAD_Client_ID(getCtx()) }).firstOnly();
			if (sfDefaults == null)
				throw new IllegalStateException("/nShopify Defaults need to be set on iDempiere /n");
			DefaultHttpClient client = new DefaultHttpClient((String) sfDefaults.get_Value("consumerkey"),
					(String) sfDefaults.get_Value("consumerSecret"));
		    OAuthConfig config = new OAuthConfig((String) sfDefaults.get_Value("url"),
					(String) sfDefaults.get_Value("consumerkey"), (String) sfDefaults.get_Value("consumersecret"));
			shopify = new ShopifyAPI(config, ApiVersionType.V1);

	 
			Map<String, Object> updateData = new HashMap<>();
			Map<String, Object> variantObject = new HashMap<>();
		    variantObject.put("id",inventoryItemId);
		    variantObject.put("id",Location_ID);
		    variantObject.put("avaliable", Stock); 
			updateData.put("variant", variantObject); 

	
	 Map<?, ?> response = shopify.create(EndpointBaseType.VARIANT.getValue(), updateData);

	 if (response != null) {
	    log .warning("Product Stock updated successfully" + "-" + inventoryItemId + "-" + Location_ID);
	 } else {
		 log .warning("Product Stock didn't updated successfully" + "-" + inventoryItemId + "-" + Location_ID);
	   }
	     }
}



