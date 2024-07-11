package za.co.ntier.processes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

import com.icoderman.shopify.ApiVersionType;
import com.icoderman.shopify.DefaultHttpClient;
import com.icoderman.shopify.EndpointBaseType;
import com.icoderman.shopify.ShopifyAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;

import za.co.ntier.model.X_zz_shopify;


public class UploadShopifyPrice  extends SvrProcess {

	PO sfDefaults;
	com.icoderman.shopify.Shopify shopify;
	
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub
		
		int plVersion = getRecord_ID();
		List<MProductPrice> productlist = new Query(getCtx(), MProductPrice.Table_Name, " M_PriceList_Version_ID = ?", null).setParameters(plVersion).list();
		
		for(MProductPrice price : productlist) {
			MProduct product = (MProduct) price.getM_Product();
			String variantID = product.getValue();
			BigDecimal listPrice = price.getPriceList();
			BigDecimal StdPrice = price.getPriceStd();
			
		
			 try {
				 UpdateVariantPriceOnShopify(variantID, listPrice);
	            } catch (Exception e) {
	                log.warning("Failed to update price for variant ID " + variantID + ": " + e.getMessage());
	            }
		}
		
		log.warning("Price Process Completed!");		
		return null;
	}

	 
	 
	 private void UpdateVariantPriceOnShopify(String variantId, BigDecimal Pricelist ) throws Exception {
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
//		    variantObject.put("id",variantId); 
		    variantObject.put("price", String.valueOf(Pricelist)); 
			updateData.put("variant", variantObject); 

	
//	 Map<?, ?> response = shopify.update(EndpointBaseType.VARIANT.getValue(), variantId, updateData);

//	 if (response != null) {
//	    log .warning("Product updated successfully" + "-" + variantId + "-" + Pricelist);
//	 } else {
//		 log .warning("Product didn't updated successfully" + "-" + variantId + "-" + Pricelist);
//	   }
	     }
	
}
