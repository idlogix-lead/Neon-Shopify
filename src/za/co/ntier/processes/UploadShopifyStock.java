package za.co.ntier.processes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.compiere.model.MProduct;
import org.compiere.model.MStorageOnHand;
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

import za.co.ntier.model.MLocation;
import za.co.ntier.model.X_zz_shopify;

public class UploadShopifyStock extends SvrProcess {

	int Parent_Product_ID=0;
	int Product_ID=0;
	int Location_ID=0;
	int Locator_ID=0;
	Boolean IsVariant=null;
	BigDecimal Stock= new BigDecimal(0);
	Boolean IsProductWithZeroStock=null;
	com.icoderman.shopify.Shopify shopify;
	int Client_ID=1000000;
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
				Parent_Product_ID = para[i].getParameterAsInt();
			else if (name.equals("M_Product_ID"))
				Product_ID = para[i].getParameterAsInt();
			else if (name.equals("M_Location_ID"))
				Location_ID = para[i].getParameterAsInt();
			else if (name.equals("M_Locator_ID"))
				Locator_ID = para[i].getParameterAsInt();
			else if (name.equals("is_variant"))
				IsVariant = para[i].getParameterAsBoolean();
			else if (name.equals("ProductWithZeroStock"))
				IsProductWithZeroStock = para[i].getParameterAsBoolean();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);	
		}
	}

	@Override
	protected String doIt() throws Exception {
//		String whereClause = "case when ? <> 0 then m_product_id = ? else m_parent_product_id = ? end ";
		String whereClause = "ad_client_id = ? AND (case when ? <> 0 then m_product_id = ? else (case when ? <> 0 then m_parent_product_id = ?"
				+ " else 1=1 end) end)";
		
        List<MProduct> products = new Query(getCtx(), MProduct.Table_Name,whereClause, null)
                .setParameters(Client_ID,Product_ID,Product_ID,Parent_Product_ID,Parent_Product_ID).list();
        
        MLocation shopifylocation = new MLocation(getCtx(), Location_ID, null);
        String ShopifylocId= shopifylocation.getValue();
        
    for(MProduct product :products) {
    	int productId= product.get_ID();
    	String inventoryItemId = product.get_ValueAsString("inventory_item_id");
    	
    	 Stock =   MStorageOnHand.getQtyOnHandForLocator(productId, Locator_ID, 0, null);
       
    	 if (inventoryItemId != null && !inventoryItemId.isEmpty() && (Stock.compareTo(BigDecimal.ZERO) > 0 || IsProductWithZeroStock)) {
             UpdateVariantStockOnShopify(inventoryItemId, ShopifylocId, Stock);
         } else {
        	  addBufferLog(productId, null, Stock, "Stock is zero and product " + product.getName() + " not updated for inventory item: " + inventoryItemId, 0, 0);
                      }
       
    }
	
		return "Process completed successfully";
	}

	
	 private void UpdateVariantStockOnShopify(Object inventoryItemId, String Location_ID,BigDecimal Stock ) throws Exception {
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

			int stockIntValue = Stock.setScale(0, RoundingMode.DOWN).intValue();
	 
			Map<String, Object> InventoryItemObject = new HashMap<>();
			InventoryItemObject.put("inventory_item_id",inventoryItemId);
			InventoryItemObject.put("location_id",Location_ID);
			InventoryItemObject.put("available", stockIntValue);

		System.out.println(InventoryItemObject);
	 Map<?, ?> response = shopify.create(EndpointBaseType.Inventory_Item.getValue(), InventoryItemObject);
	 if (response != null) {
		    if (response.containsKey("errors")) {
		        Object errors = response.get("errors");
		        log.warning("Product Stock update failed due to errors: " + errors);
		    } else {
		        log.warning("Product Stock updated successfully" + "-" + inventoryItemId + "-" + Location_ID);
		    }
		} else {
		    log.warning("Product Stock didn't update successfully" + "-" + inventoryItemId + "-" + Location_ID);
		}
	 
	 }
}



