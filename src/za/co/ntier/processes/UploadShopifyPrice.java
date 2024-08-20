package za.co.ntier.processes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.Env;

import com.icoderman.shopify.ApiVersionType;
import com.icoderman.shopify.DefaultHttpClient;
import com.icoderman.shopify.EndpointBaseType;
import com.icoderman.shopify.ShopifyAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;

import za.co.ntier.model.X_zz_shopify;


public class UploadShopifyPrice  extends SvrProcess {

	int Product_ID=0;
	int Parent_Product_ID=0;
	int plvID = 0;
	PO sfDefaults;
	com.icoderman.shopify.Shopify shopify;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("M_Product_ID"))
				Product_ID = para[i].getParameterAsInt();
			else if (name.equals("M_Parent_Product_ID"))
				Parent_Product_ID  = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);	
		}	
		plvID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		
		String whereClause = "case when ? <> 0 then m_product_id = ? else m_parent_product_id = ? end ";
        List<MProduct> products = new Query(getCtx(), MProduct.Table_Name,whereClause, null)
                .setParameters(Product_ID,Product_ID,Parent_Product_ID).list();
        
        String ids = products.stream()
                .map(product -> String.valueOf(product.get_ID()))
                .collect(Collectors.joining(","));
        
        whereClause = " m_pricelist_version_id = ? ";
        if(ids.length()>0)
        	whereClause = whereClause + "AND m_product_id IN (" + ids + ")";
        List<MProductPrice> prices = new Query(getCtx(), MProductPrice.Table_Name,whereClause, null)
                .setParameters(plvID).list();
        
        for(MProductPrice price:prices) {
        	
        	MProduct product = (MProduct) price.getM_Product();
        	String variantID = product.getValue();
            BigDecimal listPrice = price.getPriceList();
            BigDecimal StdPrice = price.getPriceStd();

		
			 try {
				 UpdateVariantPriceOnShopify(variantID, listPrice,StdPrice);
	            } catch (Exception e) {
	                log.warning("Failed to update price for variant ID " + variantID + ": " + e.getMessage());
	            }
			 log.warning("Price Completed!" + variantID + "-" + listPrice + "-" + StdPrice );		
		}
	
		 log.warning("Price Process Completed!");	
		return null;
	}

	 
	 
	 private void UpdateVariantPriceOnShopify(String variantId, BigDecimal Pricelist,BigDecimal PriceStd ) throws Exception {
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
		    variantObject.put("price", String.valueOf(PriceStd));
		    variantObject.put("compare_at_price", String.valueOf(Pricelist)); 
			updateData.put("variant", variantObject); 

	
	 try {
         Map<?, ?> response = shopify.update(EndpointBaseType.VARIANT.getValue(),variantId, updateData);
         if (response != null) {
             log.warning("Product updated successfully - " + variantId + " - " + Pricelist);
         } else {
             log.warning("Product didn't update successfully - " + variantId + " - " + Pricelist);
         }
     } catch (Exception e) {
         if (e.getMessage().contains("404")) {
        	 addBufferLog(0, null, null, "Variant ID Not Found On Shopify Website To Update Price: " + variantId, 0, 0);
         } else {
             throw e;
         }
     }
 }
}
