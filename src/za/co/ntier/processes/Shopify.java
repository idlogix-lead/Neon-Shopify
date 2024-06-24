package za.co.ntier.processes;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;

import org.apache.http.client.utils.URIBuilder;
import org.compiere.model.MOrder;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import com.google.gson.Gson;
import com.icoderman.shopify.ApiVersionType;
import com.icoderman.shopify.DefaultHttpClient;
import com.icoderman.shopify.EndpointBaseType;
import com.icoderman.shopify.HttpClient;
import com.icoderman.shopify.ShopifyAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;

import za.co.ntier.model.X_zz_shopify;
import za.co.ntier.shopify.SfOrder;

/**
 *
 * Start a thread to collect unsynchronised orders from WooCommerce website
 *
 * @author yogan naidoo
 */

public class Shopify extends SvrProcess {

	String order_ID="";
	Timestamp StartDate;
	Timestamp EndDate;
	com.icoderman.shopify.Shopify shopify;
	PO sfDefaults;
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("OrderID"))
				order_ID = para[i].getParameterAsString();
			else if (name.equals("StartDate"))
				StartDate = para[i].getParameterAsTimestamp();
			else if (name.equals("EndDate"))
				EndDate = para[i].getParameterAsTimestamp();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			
		}
		
		
		
	}

	@Override
	protected String doIt() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	    TimeZone timeZone = TimeZone.getTimeZone("GMT+05:00");
        dateFormat.setTimeZone(timeZone);
		StartDate = Timestamp.valueOf(StartDate.toLocalDateTime().withHour(00).withMinute(00).withSecond(00));
		EndDate = Timestamp.valueOf(EndDate.toLocalDateTime().withHour(23).withMinute(59).withSecond(59));
		String after = dateFormat.format(StartDate);
		String before = dateFormat.format(EndDate);
		
		String whereClause = " isactive = 'Y' AND AD_Client_ID = ?";
		sfDefaults = new Query(getCtx(), X_zz_shopify.Table_Name, whereClause, null)
				.setParameters(new Object[] { Env.getAD_Client_ID(getCtx()) }).firstOnly();
		if (sfDefaults == null)
			throw new IllegalStateException("/nShopify Defaults need to be set on iDempiere /n");
		DefaultHttpClient client = new DefaultHttpClient((String) sfDefaults.get_Value("consumerkey"),
				(String) sfDefaults.get_Value("consumerSecret"));
		URIBuilder builder = null;
		try {
			builder = new URIBuilder((String) sfDefaults.get_Value("url") + "/admin/api/2024-01/orders.json");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		OAuthConfig config = new OAuthConfig((String) sfDefaults.get_Value("url"),
				(String) sfDefaults.get_Value("consumerkey"), (String) sfDefaults.get_Value("consumersecret"));
		shopify = new ShopifyAPI(config, ApiVersionType.V1);
		
		

        List<String> fetchedOrders = new ArrayList<>();
		
		if(order_ID.length()>0) {
			if(!isValidOrderID(order_ID))
				return "Enter Order ID in proper Format";
//			try {
				Map<?,?> order = shopify.get(EndpointBaseType.ORDER.getValue(), order_ID);
				processOrder((Map<?,?>)order.get("order"));
//			} catch (Exception e) {
//				return e.getMessage();
//			}
			
		}
		else {

			 builder.setParameter("created_at_min", after)
				 	.setParameter("created_at_max", before)
				 	.setParameter("fulfillment_status", "shipped")
					.setParameter("status", "any");
			String page_info=null;
			do {
				try {
					page_info=null;
					LinkedHashMap<?, ?> mapWcOrders = client.getAll(builder);
					List<?> wcOrders = (List<?>) mapWcOrders.get("orders"); 
//					for (int i = 0; i < wcOrders.size(); i++) {
//						Map<?, ?> order = (Map<?, ?>) wcOrders.get(i);
//						System.out.println(order.get("name"));
//					}
					 for (Object wcOrderObj : wcOrders) {
			                Map<?, ?> order = (Map<?, ?>) wcOrderObj;
			                
			                // Check if the order already exists in the database
			                String orderNumber = (String) order.get("name");
			                MOrder existingOrder = ExistingOrder(String.valueOf(orderNumber).replace("#", "")); // Implement this method to find order by order number
			                
			                if (existingOrder != null) {
			                 	
			                	 addBufferLog(existingOrder.getC_Order_ID(), existingOrder.getDateOrdered(),
				                            null, "Existing Order : " + existingOrder.getDocumentNo(),
				                            MOrder.Table_ID, existingOrder.getC_Order_ID());
			                    System.out.println("Order already exists in the database: " + existingOrder.getDocumentNo());
			                
			                } else {
			                    SfOrder wcOrder = new SfOrder(getCtx(), get_TrxName(), sfDefaults);
			                    MOrder morder = wcOrder.createOrder(order);
			                    
			                    List<?> lines = (List<?>) order.get("line_items");
			                    for (Object lineObj : lines) {
			                        Map<?, ?> line = (Map<?, ?>) lineObj;
			                        wcOrder.createOrderLine(line, order);
			                    }
			                    
			                    addBufferLog(morder.getC_Order_ID(), morder.getDateOrdered(),
			                            null, "New Order created: " + morder.getDocumentNo(),
			                            MOrder.Table_ID, morder.getC_Order_ID());
			                }
			            }
					 page_info = client.getNextPageLink();
					builder.removeQuery();
					if(page_info!=null && page_info.length()!=0)
						builder.addParameter("page_info", page_info);
				} catch (Exception e) {
					return e.getMessage();
				}
				
			}while(page_info!=null);
								
		}
		  Gson gson = new Gson();
	        return gson.toJson(fetchedOrders);
	}
	
	private void processOrder(Map<?,?> order) {
		
//		  Map<Long, Double> variantPrices = fetchVariantPrices(order);
		 String fulfillmentStatus = (String) order.get("fulfillment_status");

		    if (fulfillmentStatus == null || !fulfillmentStatus.equalsIgnoreCase("fulfilled")) {
		        String orderId = (String) order.get("name");
		        String message = (fulfillmentStatus == null) ? "Order status is unfulfilled" : "Order is not fulfilled";
		        addBufferLog(0, null, null, "Order " + orderId + ": " + message, MOrder.Table_ID, 0);
		        return;
		    }
		boolean isdeleted = false;
		String id = (String) order.get("name");
		MOrder oldOrder = ExistingOrder(String.valueOf(id).replace("#", ""));
		if(oldOrder != null ) {
			if(oldOrder!=null) {
				try {
					String docStatus = oldOrder.get_ValueAsString(MOrder.COLUMNNAME_DocStatus);
					if(List.of("IN","CO","CL").contains(docStatus)) {
						System.out.println("Print # 2");
						return;
					}
						if(oldOrder.delete(false)==false) {
						addBufferLog(oldOrder.getC_Order_ID(), oldOrder.getDateOrdered(),
								null, "Could not update Order ---------------->"+oldOrder.getDocumentNo(),
								MOrder.Table_ID, oldOrder.getC_Order_ID());
					return;
					}
					else
						isdeleted=true;
				} catch (Exception e) {
					// TODO: handle exception
					addBufferLog(oldOrder.getC_Order_ID(), oldOrder.getDateOrdered(),
							null, e.getMessage()+" "+oldOrder.getDocumentNo(),
							MOrder.Table_ID, oldOrder.getC_Order_ID());
					return;
				}
			}
			
			}
		
		System.out.println("Order- " + order.get("name") + ": ");
		SfOrder wcOrder = new SfOrder(getCtx(), get_TrxName(), sfDefaults);
		MOrder morder=wcOrder.createOrder(order);
		addBufferLog(morder.getC_Order_ID(), morder.getDateOrdered(),
				null, (isdeleted?"Updated Order ---------------->":"")+ morder.getDocumentNo(),
				MOrder.Table_ID, morder.getC_Order_ID());
		
		morder.saveEx();

		// Iterate through each order Line
		List<?> lines = (List<?>) order.get("line_items");
//		wcOrder.filterbundles(lines);
		for (int j = 0; j < lines.size(); j++) {
			Map<?, ?> line = (Map<?, ?>) lines.get(j);
			wcOrder.createOrderLine(line, order);
			Object name = line.get("name");
			System.out.println("Name of Product = " + name.toString());
		

		}
		
//		   List<?> variantline = (List<?>) order.get("line_items");
//		    for (int j = 0; j < variantline.size(); j++) {
//		        Map<?, ?> line = (Map<?, ?>) variantline.get(j);
//		        Object variantIdObj = line.get("variant_id");
//		        if (variantIdObj != null) {
//		            String variantId = variantIdObj.toString();
//		            // Construct the URL for fetching the variant information
//		            String variantUrl = sfDefaults.get_Value("url") + "/admin/api/2024-01/variants/" + variantId + ".json";
//		            try {
//		                // Make an API call to Shopify to retrieve the variant information
//		                Map<?, ?> variantResponse = shopify.get(EndpointBaseType.VARIANT.getValue(), variantId);
//		                // Parse the response to extract the price of the variant
//		                Map<?, ?> variantData = (Map<?, ?>) variantResponse.get("variant");
//		                Object variantPriceObj = variantData.get("price");
//		                if (variantPriceObj != null) {
//		                    double variantPrice = Double.parseDouble(variantPriceObj.toString());
//		                    // Use or store the variant price as needed
//		                    System.out.println("Variant Price: " + variantPrice);
//		                    System.out.println("Variant Price: " + variantData);
//		                    
//		                    Object productIdObj = line.get("product_id");
//		                    if (productIdObj != null) {
//		                        int m_Product_ID = Integer.parseInt(productIdObj.toString());
//		                        wcOrder.createProductPrice(m_Product_ID, line, variantPrice);
//		                    } else {
//		                        System.err.println("Product ID not found for line item: " + line);
//		                    }
//		                }
//		            } catch (Exception e) {
//		                e.printStackTrace();
//		                // Handle any exceptions
//		            
//		            }}
		}
		
//		wcOrder.addShippingCharges(order);
//		wcOrder.addCoupon(order);
//		Map<String, Object> body = new HashMap<>();
//		List<Map<String, String>> listOfMetaData = new ArrayList();
//		Map<String, String> metaData = new HashMap<>();
//		metaData.put("value", "yes");
//		listOfMetaData.add(metaData);
//
//		body.put("meta_data", listOfMetaData);
//		Map<?, ?> response = wooCommerce.update(EndpointBaseType.ORDERS.getValue(), id, body);
//		System.out.println(response.toString());
		
//	public Map<Long, Double> fetchVariantPrices(Map<?, ?> orderSf) {
//	    Map<Long, Double> variantPrices = new HashMap<>();
//	    List<?> variantline = (List<?>) orderSf.get("line_items");
//	    for (int j = 0; j < variantline.size(); j++) {
//	        Map<?, ?> line = (Map<?, ?>) variantline.get(j);
//	        Object variantIdObj = line.get("variant_id");
//	        if (variantIdObj != null) {
//	            String variantId = variantIdObj.toString();
//	            String variantUrl = sfDefaults.get_Value("url") + "/admin/api/2024-01/variants/" + variantId + ".json";
//	            try {
//	                Map<?, ?> variantResponse = shopify.get(EndpointBaseType.VARIANT.getValue(), variantId);
//	                Map<?, ?> variantData = (Map<?, ?>) variantResponse.get("variant");
//	                Object variantPriceObj = variantData.get("price");
//	                if (variantPriceObj != null) {
//	                    double variantPrice = Double.parseDouble(variantPriceObj.toString());
//	                    variantPrices.put(Long.parseLong(variantId), variantPrice);
//	                }
//	            } catch (Exception e) {
//	                e.printStackTrace();
//	            }
//	        }
//	    }
//	    return variantPrices;
//	}
	
	
	
	
	

	private MOrder ExistingOrder(String id) {
		int c_order_id = 0;
		String sql = "SELECT c_order_id FROM c_order "
				+ "WHERE poreference=? AND Docstatus IN ('DR','IN','CO') AND issotrx = 'Y' "
				+ "ORDER BY c_order_id DESC limit 1";
		c_order_id = DB.getSQLValue(get_TrxName(), sql,id);
			if (c_order_id>0)
				return new MOrder(getCtx(),c_order_id,get_TrxName());
		return null;
	}
	private boolean isValidOrderID(String id) {
		return id.matches("\\d+");
	}
	
}
