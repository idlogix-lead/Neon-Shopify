package za.co.ntier.processes;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.compiere.model.MProduct;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

import com.icoderman.shopify.ApiVersionType;
import com.icoderman.shopify.DefaultHttpClient;
import com.icoderman.shopify.EndpointBaseType;
import com.icoderman.shopify.ShopifyAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;

import za.co.ntier.model.MParentProduct;
import za.co.ntier.model.X_zz_shopify;

public class SyncShopifyProductPrice  extends SvrProcess{

	String Product_ID="";
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
			else if (name.equals("Product Id"))
				Product_ID = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);	
		}	
	}
		
	

	@Override
	protected String doIt() throws Exception {
		
	
		syncVariantsWithProducts();
		return null;
	}

	 private List<MParentProduct> getAllParentProducts() {
	        List<MParentProduct> parentProducts = new Query(getCtx(), MParentProduct.Table_Name, "IsActive='Y'", get_TrxName())
	                .list();
	        return parentProducts;
	    }
	 private void syncVariantsWithProducts() throws Exception {
	        List<MParentProduct> parentProducts = getAllParentProducts();
	        for (MParentProduct parentProduct : parentProducts) {
	            String productId = parentProduct.getValue();
	            String productName = parentProduct.getName(); 
	            List<Map<?, ?>> variantList = fetchVariantsForProduct(productId);
	            for (Map<?, ?> variant : variantList) {
	                String variantId = String.valueOf(variant.get("id"));
	                String variantName = String.valueOf(variant.get("title"));   
	                String variantPrice = String.valueOf(variant.get("price"));
	                Integer mProductId = productExistsInMProduct(variantId);
	                if (mProductId != null) {
	                    Map<String, BigDecimal> prices = variantExistsInMProductPrice( mProductId);
	                    if (!prices.isEmpty()) {
	                        BigDecimal priceStd = prices.get("priceStd");
	                        BigDecimal priceLimit = prices.get("priceLimit");
	                        BigDecimal priceList = prices.get("priceList");
	                        log.warning("This variant exists: " + variantId + " - " + variantName + " - " + variantPrice +
	                                    " PriceStd: " + priceStd + ", PriceLimit: " + priceLimit + ", PriceList: " + priceList);
	                        // updateProduct(parentProduct, productName, variantName); // Uncomment and implement this if needed
	                    } else {
	                        log.warning("Variant found in MProduct, but prices not found: " + variantId + " - " + variantName + " - " + variantPrice);
	                    }
	                } else {
	                    log.warning("This variant does not exist in MProduct: " + variantId + " - " + variantName + " - " + variantPrice);
	                }
	            }
	        }
	    }
	 
	  private List<Map<?, ?>> fetchVariantsForProduct(String productId) throws Exception {
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

			
			List<Map<?, ?>> variantsList = new ArrayList<>();
		    try {
		        Map<?, ?> mapWcProduct = shopify.get(EndpointBaseType.PRODUCTS.getValue(), productId);
		        Map<?, ?> product = (Map<?, ?>) mapWcProduct.get("product");
		        List<?> variants = (List<?>) product.get("variants");
		        for (Object variantObj : variants) {
		            Map<?, ?> variant = (Map<?, ?>) variantObj;
		            variantsList.add(variant);
		        }
		    } catch (Exception e) {
		        throw new RuntimeException("Error fetching variants for product " + productId + ": " + e.getMessage());
		    }
		    return variantsList;
		}
	 
	  private Map<String, BigDecimal> variantExistsInMProductPrice(int mproductId) {
		    String sql = "SELECT PriceStd, PriceLimit, PriceList FROM M_ProductPrice WHERE m_product_id =CAST(? AS NUMERIC)";
		    Map<String, BigDecimal> prices = new HashMap<>();

		    try (PreparedStatement pstmt = DB.prepareStatement(sql, null)) {
		        pstmt.setInt(1, mproductId);
		        try (ResultSet rs = pstmt.executeQuery()) {
		            if (rs.next()) {
		                prices.put("priceStd", rs.getBigDecimal("PriceStd"));
		                prices.put("priceLimit", rs.getBigDecimal("PriceLimit"));
		                prices.put("priceList", rs.getBigDecimal("PriceList"));
		            }
		        }
		    } catch (SQLException e) {
		        log.severe("Error fetching variant prices: " + e.getMessage());
		    }

		    return prices;
		}
	  
	  private Integer productExistsInMProduct(String variantId) {
			int m_product_id= 0;
	        String sql = "SELECT M_Product_ID FROM  M_Product WHERE value = ? ";
	        m_product_id = DB.getSQLValue(null, sql, variantId);
	    
	    if (m_product_id > 0) {
	        return m_product_id;
	    }
	    return null;
		}
	  
//	 private boolean variantExistsInMProductPrice(String variantId) {
//	        String sql = "SELECT COUNT(*) FROM M_ProductPrice WHERE m_product_id = ?";
//	        int count = DB.getSQLValue(null, sql, variantId);
//	        return count > 0;
//	    }
}
