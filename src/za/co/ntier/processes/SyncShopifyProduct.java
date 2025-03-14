package za.co.ntier.processes;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.http.client.utils.URIBuilder;
import org.compiere.model.MOrder;
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

public class SyncShopifyProduct  extends SvrProcess {
	
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
		String whereClause = " isactive = 'Y' AND AD_Client_ID = ?";
		sfDefaults = new Query(getCtx(), X_zz_shopify.Table_Name, whereClause, null)
				.setParameters(new Object[] { Env.getAD_Client_ID(getCtx()) }).firstOnly();
		if (sfDefaults == null)
			throw new IllegalStateException("/nShopify Defaults need to be set on iDempiere /n");
		DefaultHttpClient client = new DefaultHttpClient((String) sfDefaults.get_Value("consumerkey"),
				(String) sfDefaults.get_Value("consumerSecret"));
		URIBuilder builder = null;
		try {
			builder = new URIBuilder((String) sfDefaults.get_Value("url") + "/admin/api/2024-01/products.json");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			
		}
		
		OAuthConfig config = new OAuthConfig((String) sfDefaults.get_Value("url"),
				(String) sfDefaults.get_Value("consumerkey"), (String) sfDefaults.get_Value("consumersecret"));
		shopify = new ShopifyAPI(config, ApiVersionType.V1);
		
		if (Product_ID != null && !Product_ID.isEmpty()) {
	        // Fetch and process a single product
	        fetchAndProcessSingleProduct(Product_ID);
	        syncVariantsWithProducts();
	        return "Single product fetched and processed successfully.";
	    } else {
	        // Fetch and process all products
	        int totalProducts = fetchProductsIds(client, builder);
	        System.out.println("Total number of products fetched: " + totalProducts);
	        syncVariantsWithProducts();
	        return "Products fetched successfully. Total: " + totalProducts;
	    }
//		 int totalProducts = fetchProductsIds(client, builder);
//	        
//	        System.out.println("Total number of products fetched: " + totalProducts);
//	        syncVariantsWithProducts();
//	        return "Products fetched successfully." + totalProducts ;
	    }
	
	private int fetchProductsIds(DefaultHttpClient client, URIBuilder builder) throws Exception {
        int totalProducts = 0;
        String page_info = null;
        do {
            try {
                LinkedHashMap<?, ?> mapWcProducts = client.getAll(builder);
                List<?> wcProducts = (List<?>) mapWcProducts.get("products");
                totalProducts += wcProducts.size();
                for (Object wcProductObj : wcProducts) {
                    Map<?, ?> product = (Map<?, ?>) wcProductObj;
                    String productId = String.valueOf(product.get("id"));
                    String productName = String.valueOf(product.get("title")); 
                  
                          MParentProduct parentProduct = productExistsInParentProduct(productId);
                          if (parentProduct != null) {
                        	  updateParentProduct(parentProduct,productName);
                      
                          } else {
                                   
                        	  CreateParentProduct(productId, productName);
                          }     
                          }

                page_info = client.getNextPageLink();
                builder.removeQuery();
                if (page_info != null && !page_info.isEmpty()) {
                    builder.addParameter("page_info", page_info);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error fetching products: " + e.getMessage());
            }
        } while (page_info != null);
        return totalProducts;
    }
	
	private void fetchAndProcessSingleProduct(String productId) throws Exception {
	    try {
	        
	        Map<?, ?> mapWcProduct = shopify.get(EndpointBaseType.PRODUCTS.getValue(), productId);
	        
	        if (mapWcProduct != null && mapWcProduct.containsKey("product")) {
	            Map<?, ?> product = (Map<?, ?>) mapWcProduct.get("product");
	            String productName = String.valueOf(product.get("title"));

	            MParentProduct parentProduct = productExistsInParentProduct(productId);
	            if (parentProduct != null) {
	                updateParentProduct(parentProduct, productName);
	                log.log(Level.SEVERE, "Exist All Ready " + productName);
	            } else {
	                CreateParentProduct(productId, productName);
	                log.log(Level.SEVERE, "not Exist All Ready " + productName);
	            }
	        } else {
	            throw new RuntimeException("Product with ID " + productId + " not found.");
	        }
	    } catch (Exception e) {
	        throw new RuntimeException("Error fetching product: " + e.getMessage());
	    }
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
            if (variantExistsInMProduct(variantId)) {
            	     updateProduct(parentProduct,productName,variantName);
                }else {
                	CreateProduct(parentProduct.get_ID(),productId,variantId,variantName,productName);
                	
                }
            }
        }
    }
	
	 private List<MParentProduct> getAllParentProducts() {
	        List<MParentProduct> parentProducts = new Query(getCtx(), MParentProduct.Table_Name, "IsActive='Y'", get_TrxName())
	                .list();
	        return parentProducts;
	    }
	 
	 
	  private boolean variantExistsInMProduct(String variantId) {
	        String sql = "SELECT COUNT(*) FROM M_Product WHERE value = ?";
	        int count = DB.getSQLValue(null, sql, variantId);
	        return count > 0;
	    } 
	  
//	  private List<String> fetchVariantIdsForProduct(String productId) throws Exception {
//	        List<String> variantIds = new ArrayList<>();
//	        try {
//	        	 Map<?, ?> mapWcProduct = shopify.get(EndpointBaseType.PRODUCTS.getValue(), productId);
//	            LinkedHashMap<?, ?> product = (LinkedHashMap<?, ?>) mapWcProduct.get("product");
//	            List<?> variants = (List<?>) product.get("variants");
//	            for (Object variantObj : variants) {
//	                LinkedHashMap<?, ?> variant = (LinkedHashMap<?, ?>) variantObj;
//	                String variantId = String.valueOf(variant.get("id"));
//	                variantIds.add(variantId);
//	            }
//	        } catch (Exception e) {
//	            throw new RuntimeException("Error fetching variants for product " + productId + ": " + e.getMessage());
//	        }
//	        return variantIds;
//	    }

	  
	  
	  
	  private List<Map<?, ?>> fetchVariantsForProduct(String productId) throws Exception {
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

		 private void CreateProduct(int myppid,String productId,String VariantId, String variantName, String productName) {
		        try {
		        	 MProduct Product = new MProduct(getCtx(), 0, null);
		        	 Product.setValue(VariantId);
		        	 Product.setName(productName + " - " + variantName);
		        	 Product.set_ValueOfColumn("m_parent_product_id", myppid);
		        	 Product.setAD_Org_ID(getAD_Client_ID());
		        	 Product.setC_UOM_ID(100);
		        	Product.setC_TaxCategory_ID(1000000);
		        	 Product.setM_Product_Category_ID(1000000);
		        	 Product.setIsPurchased(true);
		        	 Product.setIsSold(true);
		        	 Product.setIsStocked(true);
		        	 Product.saveEx();
		             
		        } catch (Exception e) {
		        	  e.printStackTrace(); 
		            System.err.println("Error Creating product: " + e.getMessage());
		        }
		    }
		 
		 private void updateProduct(MParentProduct pp, String productName, String variantName) {
		        try {
		          pp.setName(variantName + "-" + productName);
		   		  pp.save();
		   		
		        } catch (Exception e) {
		            System.err.println("Error updating product: " + e.getMessage());
		        }
		    }
		 
		 
	 private void CreateParentProduct(String productId, String productName) {
	        try {
	        	 MParentProduct parentProduct = new MParentProduct(getCtx(), 0, get_TrxName());
	                parentProduct.setValue(productId);
	                parentProduct.setName(productName);
	                parentProduct.setAD_Org_ID(getAD_Client_ID());
	                parentProduct.setIsActive(true);
	                parentProduct.setM_Product_Category_ID(1000000);
	                parentProduct.saveEx();
	             
	        } catch (Exception e) {
	        	  e.printStackTrace(); 
	            System.err.println("Error Creating product: " + e.getMessage());
	        }
	    }
	 
	  private void updateParentProduct(MParentProduct pp, String productName) {
	        try {
	          pp.setName(productName);
	   		  pp.save();
	   		
	        } catch (Exception e) {
	            System.err.println("Error updating product: " + e.getMessage());
	        }
	    }
	
	
	private MParentProduct productExistsInParentProduct(String productId) {
		int m_parent_product_id= 0;
        String sql = "SELECT M_Parent_Product_ID FROM  M_Parent_Product WHERE value = ? ";
        m_parent_product_id = DB.getSQLValue(null, sql, productId);
    
    if (m_parent_product_id > 0) {
        return new MParentProduct(getCtx(),(int) m_parent_product_id, get_TrxName());
    }
    return null;
	}

}
