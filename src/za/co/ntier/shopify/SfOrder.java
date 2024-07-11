package za.co.ntier.shopify;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.base.Core;
import org.adempiere.base.IProductPricing;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_C_Order;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

import com.icoderman.shopify.ApiVersionType;
import com.icoderman.shopify.EndpointBaseType;
import com.icoderman.shopify.ShopifyAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;

import za.co.ntier.model.MCourierCompany;

/**
 *
 * Create Order and lines on iDempiere as received from WooCommerce
 *
 * @author yogan naidoo
 */

public final class SfOrder {
	private final Properties ctx;
	private final String trxName;
	private final int POSTENDERTYPE_ID = 1000000;
	private final int CREDIT_ORDER = 1000033;
	String courierCode="";
	// private final int priceList_ID = 101;
	final String PAYMENT_RULE = "P";
	// final String PAYMENT_RULE = "P";
	private final MOrder order;
	com.icoderman.shopify.Shopify shopify;
	private Boolean isTaxInclusive;
	private static CLogger log = CLogger.getCLogger(SfOrder.class);
	private PO sfDefaults;
	ArrayList<MetaDataObject> metadata = new ArrayList<>();
	public SfOrder(Properties ctx, String trxName, PO sfDefaults) {
		this.ctx = ctx;
		this.trxName = trxName;
		this.sfDefaults = sfDefaults;
		order = new MOrder(ctx, 0, trxName);
	}

	public MOrder createOrder(Map<?, ?> orderSf) {

		order.setClientOrg(Env.getAD_Client_ID(ctx), Env.getAD_Org_ID(Env.getCtx()));
		order.setAD_Org_ID((int) sfDefaults.get_Value("ad_org_id"));
		String poreference = ((String)orderSf.get("name")).replace("#", "");
		order.setPOReference(poreference);
		int BP_Id =getCBPartner(orderSf);
		order.setC_BPartner_ID(BP_Id);
		int BPLocationId = getBPLocationId(BP_Id);
		order.setC_BPartner_Location_ID(BPLocationId); 
		order.setBill_BPartner_ID(BP_Id);
		order.setBill_Location_ID(BPLocationId);
		isTaxInclusive = (orderSf.get("taxes_included").toString().equals("true")) ? true : false;
		order.setM_PriceList_ID(getPriceList(orderSf));
		order.setIsSOTrx(true);
		order.setM_Warehouse_ID((int) sfDefaults.get_Value("m_warehouse_id"));
		order.setDateOrdered(getDate(orderSf));
		order.setDateAcct(getDate(orderSf));
		order.setC_DocTypeTarget_ID(CREDIT_ORDER);
		order.setPaymentRule(PAYMENT_RULE);
		order.setDeliveryRule("F");
		order.setInvoiceRule("D");
		order.set_ValueOfColumn("couriercode", courierCode);	
		try {
			System.out.println("Date ordered: " + order.getDateOrdered());
			order.saveEx();
		} catch (Exception e) {
			String errorMsg = "Error in Order #"+order.getPOReference()+" -> "+e.getLocalizedMessage();
			throw new AdempiereException(errorMsg);
		}
		
		return order;
	}

	private java.sql.Timestamp getDate(Map<?, ?> orderSf) {
//		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//		
//		String date = orderSf.get("created_at").toString();
//		OffsetDateTime odt = OffsetDateTime.parse(date, dateTimeFormatter);
//		Instant instant = odt.toInstant();
//		return java.sql.Timestamp.from(instant);
		String date = (String) orderSf.get("created_at");
	    if (date == null) {
	        return new java.sql.Timestamp(System.currentTimeMillis());
	    }
	    
	    if (date.contains("+")) {
	        date = date.substring(0, date.indexOf('+'));
	    } else if (date.contains("-")) {
	        date = date.substring(0, date.lastIndexOf('-'));
	    }
	
	    LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	    
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	    String formattedDate = localDateTime.format(formatter);
	    LocalDateTime parsedLocalDateTime = LocalDateTime.parse(formattedDate, formatter);
	    
	    // Convert to java.sql.Timestamp
	    ZoneId zoneId = ZoneId.of("Asia/Karachi");
	    Timestamp timestamp = Timestamp.valueOf(parsedLocalDateTime.atZone(zoneId).toLocalDateTime());
	    
	    return timestamp;
	}

	private int getPriceList(Map<?, ?> orderSf) {
		String wcCurrency = (String) orderSf.get("currency");
		String localCurrency = DB.getSQLValueString(trxName,
				"select iso_code from C_Currency " + "where C_Currency_ID = " + "(select C_Currency_ID "
						+ "from M_PriceList " + "where M_PriceList_id = ?) ",
				(int) sfDefaults.get_Value("local_incl_pricelist_id"));

		Boolean local = (wcCurrency.equals(localCurrency)) ? true : false;

		int priceList;
		if (local) {
			priceList = (isTaxInclusive) ? (int) sfDefaults.get_Value("local_incl_pricelist_id")
					: (int) sfDefaults.get_Value("local_excl_pricelist_id");
		} else {
			priceList = (isTaxInclusive) ? (int) sfDefaults.get_Value("intl_incl_pricelist_id")
					: (int) sfDefaults.get_Value("intl_excl_pricelist_id");
		}
		return (priceList);
	}
	public int getBPLocationId(int bp_Id) {
		int c_bpartner_location_id = DB.getSQLValue(trxName,
				"select c_bpartner_location_id " + "from C_BPartner_Location " + "where c_bpartner_id = ?", bp_Id);
		if (c_bpartner_location_id < 0) {
			log.severe("BP with id : " + bp_Id + " does not have a C_BPartner_Location on iDempiere");
			int c_bpartner_id = (int) sfDefaults.get_Value("c_bpartner_id");
			c_bpartner_location_id = DB.getSQLValue(trxName,
					"select c_bpartner_location_id " + "from C_BPartner_Location " + "where c_bpartner_id = ?",
					c_bpartner_id);
		}
		return c_bpartner_location_id;
	}
	
	
	
	
	public void createOrderLine(Map<?, ?> line, Map<?, ?> orderSf) {
		MOrderLine orderLine = new MOrderLine(order);

		double priceList =Double.parseDouble((String) line.get("price")); 
	    int m_Product_ID = getProductId(line);
		orderLine.setAD_Org_ID(order.getAD_Org_ID());
		orderLine.setM_Product_ID(m_Product_ID);
		orderLine.setC_Tax_ID(1000001);
		orderLine.setM_Warehouse_ID(order.getM_Warehouse_ID());
		long qty = ((Number) line.get("quantity")).longValue();
		orderLine.setQty(BigDecimal.valueOf((long) qty));
		setLinePricing(orderLine,line);
//		orderLine.setPrice(BigDecimal.valueOf(priceList));
		
		
//		setPrice(orderLine, line);
		System.out.println("*********************Unit Price: " + orderLine.getPriceActual());
		try {
			orderLine.saveEx();
		} catch (Exception e) {
			String errorMsg = "Error in Order #"+orderLine.getC_Order().getPOReference()+" -> "+e.getLocalizedMessage();
			throw new AdempiereException(errorMsg);
			
		}
		
	}
	
	
void setLinePricing(MOrderLine oline,Map<?, ?> line) {
		oline.set_TrxName(order.get_TrxName());
		MOrder order = (MOrder)oline.getC_Order();
		MBPartner customer = (MBPartner) order.getC_BPartner();
		int id = oline.getM_Product_ID();
		IProductPricing pp = Core.getProductPricing();
		double LinePriceList =Double.parseDouble((String) line.get("price")); 
		pp.setInitialValues(id, customer.getC_BPartner_ID(), Env.ONE,true, order.get_TrxName());
		Timestamp orderDate = (Timestamp)order.getDateOrdered();
		pp.setPriceDate(orderDate);
		pp.setOrderLine(oline, null);
		int M_PriceList_ID = order.getM_PriceList_ID();
		pp.setM_PriceList_ID(M_PriceList_ID);
		String sql = "SELECT plv.M_PriceList_Version_ID "
				+ "FROM M_PriceList_Version plv "
				+ "WHERE plv.M_PriceList_ID=? "						//	1
				+ " AND plv.ValidFrom <= ? "
				+ "ORDER BY plv.ValidFrom DESC";			
		int M_PriceList_Version_ID = DB.getSQLValueEx(null, sql, M_PriceList_ID, orderDate);

		if (M_PriceList_Version_ID > 0) {
	        pp.setM_PriceList_Version_ID(M_PriceList_Version_ID);
	        BigDecimal priceList = pp.getPriceList();
//	        BigDecimal priceLimit = pp.getPriceLimit();
//	        BigDecimal priceStd = pp.getPriceStd();
	       BigDecimal LinePricing = (BigDecimal.valueOf(LinePriceList));
	        if (priceList.compareTo(LinePricing) <= 0 && priceList.compareTo(LinePricing) <= 0) {
	            oline.setPriceList(priceList);
	            oline.setPriceLimit(priceList); 
	            oline.setPriceActual(priceList);           
	            oline.setPriceEntered(priceList); 
	        } else {
	        	oline.setPriceList(priceList);
	    		oline.setPriceLimit(LinePricing);
	    		oline.setPriceActual(LinePricing);
	    		oline.setPriceEntered(LinePricing);
	            
	        }
	        oline.setC_Currency_ID(Integer.valueOf(pp.getC_Currency_ID()));
	        oline.setDiscount(pp.getDiscount());
	        oline.setC_UOM_ID(Integer.valueOf(pp.getC_UOM_ID()));
	    } else {
	        System.out.println("Price list version not found for order: " + order.getDocumentNo());
	    }
}
		
	private String getProductID(Map<?, ?> line) {
		Object prodID = line.get("variant_id");
		if(prodID == null) 
			 prodID = line.get("product_id");
	        return String.valueOf((Long)prodID); 
	}
	
	
	

	private int getProductId(Map<?, ?> line) {
	    String productId = getProductID(line);
	    int m_Product_ID = DB.getSQLValue(trxName, 
	        "SELECT m_product_id FROM m_product WHERE value LIKE ?", String.valueOf(productId));
	    
	    if (m_Product_ID > 0) {
	    	return m_Product_ID;
	    }else{
	        m_Product_ID = createProduct(line);
	    }
	    return m_Product_ID;
	} 
	 
	 private int createProduct(Map<?, ?> line) {
		 
		 
		 OAuthConfig config = new OAuthConfig((String) sfDefaults.get_Value("url"),
					(String) sfDefaults.get_Value("consumerkey"), (String) sfDefaults.get_Value("consumersecret"));
			shopify = new ShopifyAPI(config, ApiVersionType.V1);
		 Object variantIdObj =  getProductID(line);
		    double variantPrice = 0.0;
		    
		    if (variantIdObj != null) {
		        String variantId = variantIdObj.toString();
		        try {
		            Map<?, ?> variantResponse = shopify.get(EndpointBaseType.VARIANT.getValue(), variantId);
		            Map<?, ?> variantData = (Map<?, ?>) variantResponse.get("variant");
		            Object variantPriceObj = variantData.get("price");
		            if (variantPriceObj != null) {
		                variantPrice = Double.parseDouble(variantPriceObj.toString());
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
	        MProduct product = new MProduct(ctx, 0, trxName);
	        product.setValue(String.valueOf(line.get("variant_id")));
	        product.setName((String) line.get("name"));
	        product.setM_Product_Category_ID(1000000);  
	        product.setC_TaxCategory_ID(1000000);  
	        product.setC_UOM_ID(100);  
	        product.setIsPurchased(true);
	        product.setIsSold(true);
	        product.setIsStocked(true);
	        product.saveEx();
	        
	        int m_Product_ID = product.getM_Product_ID();
	        if (!productPriceExists(m_Product_ID)) {
	            createProductPrice(m_Product_ID, line,variantPrice);
	        }
	        
	        return m_Product_ID;
	    }

	    private boolean productPriceExists(int m_Product_ID) {
	       
	    	
	    	    	
	    	int versionid = getPriceVersionID();
	        return DB.getSQLValue(trxName, "SELECT COUNT(*) FROM M_ProductPrice WHERE m_pricelist_version_id = ? and  M_Product_ID = ?",versionid, m_Product_ID) > 0;
	    }
	    
	    private int getPriceVersionID() {
	    	MPriceListVersion version = new Query(Env.getCtx(), MPriceListVersion.Table_Name, " m_pricelist_id = ? ", null)
					.setParameters(order.getM_PriceList_ID()).setOrderBy(" validfrom desc")
					.first();
	    	return version!=null?version.get_ID():0;
	    }
	    
	    public void createProductPrice(int m_Product_ID,Map<?, ?> line,double variantPrice) {	    	
	    	int versionid = getPriceVersionID();
	    	BigDecimal variantPriceBigDecimal = BigDecimal.valueOf(variantPrice);
	    	
	        MProductPrice productPrice = new MProductPrice(ctx, 0, trxName);
	        productPrice.setM_Product_ID(m_Product_ID);
	        productPrice.setM_PriceList_Version_ID(versionid);
	        productPrice.setPriceStd(variantPriceBigDecimal);
	        productPrice.setPriceList(variantPriceBigDecimal);
	        productPrice.setPriceLimit(variantPriceBigDecimal);
	        productPrice.saveEx();
	    }

	public void createShippingCharge(Map<?, ?> orderWc) {
		BigDecimal shippingCost = getShippingCost(orderWc);
		if (shippingCost.compareTo(BigDecimal.ZERO) == 0) {
		return; // no need to create a shipping charge
		}
		
		MOrderLine orderLine = new MOrderLine(order);
		orderLine.setAD_Org_ID(order.getAD_Org_ID());
		orderLine.setC_Charge_ID((int) sfDefaults.get_Value("c_charge_id"));
		orderLine.setM_Warehouse_ID(order.getM_Warehouse_ID());
		orderLine.setC_Tax_ID(1000001);
		orderLine.setQty(BigDecimal.ONE);
		orderLine.setPrice(shippingCost);
		System.out.println("*********************Shipping Cost: " + shippingCost);

		if (!orderLine.save()) {
			throw new IllegalStateException("Could not create Order Line");
		}
	}

	public int getTaxRate(Map<?, ?> orderSf) {
		List<?> taxLines = (List<?>) orderSf.get("tax_lines");
		Map<?, ?> taxLine = (Map<?, ?>) taxLines.get(0);
		String sfTaxName = (String) taxLine.get("title");
		String idTaxName = DB.getSQLValueString(trxName, "select name " + "from c_tax " + "where c_tax_id = ?",
				(int) sfDefaults.get_Value("standard_tax_id"));
		return (sfTaxName.equals(idTaxName) ? (int) sfDefaults.get_Value("standard_tax_id")
				: (int) sfDefaults.get_Value("zero_tax_id"));
	}

	public BigDecimal getShippingCost(Map<?, ?> orderWc) {
		List<?> shippingLines = (List<?>) orderWc.get("shipping_lines");
		Map<?, ?> shippingLine = (Map<?, ?>) shippingLines.get(0);
		Double total = Double.parseDouble((String) shippingLine.get("discounted_price"));
		BigDecimal shippingCost = BigDecimal.valueOf((Double) total);
		return (shippingCost.setScale(4, RoundingMode.HALF_EVEN));
	}




private int getCBPartner(Map<?, ?> orderWc) 
{
	List<MCourierCompany> companies = new Query(ctx, MCourierCompany.Table_Name, " isactive = 'Y' ", null).setOrderBy(" lineno ").list();
	
	String companyName="";
	List<?> lines = (List<?>) orderWc.get("fulfillments");
	for(int i=0;i<lines.size();i++) 
	{
		Map<?, ?> line = (Map<?, ?>) lines.get(i);
		companyName = (String)line.get("tracking_company");
		courierCode = (String)line.get("tracking_number");
		break;
	}
	for(MCourierCompany company:companies) {
		if(company.getValue().trim().equalsIgnoreCase(companyName))
			if(company.getC_BPartner_ID()>0) {
				return company.getC_BPartner_ID();
			}
	}
	return (Integer) sfDefaults.get_Value("C_BPartner_ID");
}

//private void setPrice(MOrderLine orderLine,Map<?, ?> line,double variantPrice) {
//	double priceActual = variantPrice;
//	double priceList =Double.parseDouble((String) line.get("price")); 
//    // Calculate total discount
//    List<?> discountAllocations = (List<?>) line.get("discount_allocations");
//    double totalDiscount = 0.0;
//    if (discountAllocations != null) {
//        for (int j = 0; j < discountAllocations.size(); j++) {
//            Map<?, ?> discountObj = (Map<?, ?>) discountAllocations.get(j);
//            totalDiscount += Double.parseDouble(discountObj.get("amount").toString());
//        }
//    }
//
//    priceActual -= totalDiscount;
//
//    orderLine.setPriceList(BigDecimal.valueOf(variantPrice));
//    orderLine.setPriceActual(BigDecimal.valueOf(priceActual));
//    orderLine.setPrice(BigDecimal.valueOf(priceList));
//    orderLine.setDiscount(BigDecimal.valueOf(totalDiscount));
//
//    System.out.println("Variant Price: " + variantPrice);
//    System.out.println("Total Discount: " + totalDiscount);
//    System.out.println("Discounted Price: " + priceActual);
//}
	
}