package za.co.ntier.shopify;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

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
	private final int POS_ORDER = 1000041;
	String courierCompany="";
	// private final int priceList_ID = 101;
	final String PAYMENT_RULE = "M";
	// final String PAYMENT_RULE = "P";
	private final MOrder order;
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
		inflateMetaData(orderSf);
//		int existingOrder_id = DB.getSQLValue(trxName,
//				"select c_order_id from c_order " + "where documentno = ? and ad_client_id = ? ",
//				orderSf.get("order_number").toString(), Env.getAD_Client_ID(ctx));
//		if (existingOrder_id > 0) {
//			System.out.println("!!!! Order : " + orderSf.get("order_number").toString() + " already exists ");
//			return null;
//		}
		order.setClientOrg(Env.getAD_Client_ID(ctx), Env.getAD_Org_ID(Env.getCtx()));
//		order.setDocumentNo((orderSf.get("order_number").toString()));
		order.setAD_Org_ID((int) sfDefaults.get_Value("ad_org_id"));
		int BP_Id =getCBPartner(orderSf,order);
		order.setC_BPartner_ID(BP_Id);
		int BPLocationId = getBPLocationId(BP_Id);
		order.setC_BPartner_Location_ID(BPLocationId); // order.setAD_User_ID(101);
		order.setBill_BPartner_ID(BP_Id);
		order.setBill_Location_ID(BPLocationId);
		// order.setBill_User_ID(); order.setSalesRep_ID(101);
		order.set_ValueOfColumn("couriercode", getCN(orderSf));	
		isTaxInclusive = (orderSf.get("taxes_included").toString().equals("true")) ? true : false;
		order.setM_PriceList_ID(getPriceList(orderSf));
		// order.setM_PriceList_ID(101);
		order.setIsSOTrx(true);
		order.setM_Warehouse_ID((int) sfDefaults.get_Value("m_warehouse_id"));
		order.setDateOrdered(getDate(orderSf));
		order.setDateAcct(getDate(orderSf));
		order.setC_DocTypeTarget_ID(POS_ORDER);
		order.setPaymentRule(PAYMENT_RULE);
		order.setDeliveryRule("F");
		order.setInvoiceRule("D");

		if (!order.save()) {
			throw new IllegalStateException("Could not create order");
		}
		return order;
	}

	private java.sql.Timestamp getDate(Map<?, ?> orderSf) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		String date = orderSf.get("created_at").toString();
		OffsetDateTime odt = OffsetDateTime.parse(date, dateTimeFormatter);
		Instant instant = odt.toInstant();
		return java.sql.Timestamp.from(instant);
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

	public int getBPId(Map<?, ?> orderSf) {
		String email = (String) orderSf.get("email");
		String phone = (String) orderSf.get("phone");
		int c_bpartner_id = DB.getSQLValue(trxName, "select c_bpartner_id from ad_user " + "where email like ?", email);
		if (c_bpartner_id < 0) {
			//log.severe("BP with email : " + email + " does not exist on iDempiere");
			c_bpartner_id = DB.getSQLValue(trxName, "select c_bpartner_id from ad_user " + "where phone like ?", phone);
			if (c_bpartner_id < 0)
			c_bpartner_id = createBP(orderSf);
		}
		return (int)sfDefaults.get_ValueAsInt("C_BPartner_ID");
//		return c_bpartner_id;
	}

	int createBP(Map<?, ?> orderSf) {
		Map<?, ?> customer = (Map<?, ?>) orderSf.get("customer");
		Map<?, ?> defaultAddress = (Map<?, ?>) customer.get("default_address");
		String name = (String) defaultAddress.get("first_name");
		String name2 = (String) defaultAddress.get("last_name");
		String phone = (String) customer.get("phone");
		String email = (String) customer.get("email");
		MBPartner businessPartner = new MBPartner(ctx, -1, trxName);
		businessPartner.setClientOrg(Env.getAD_Client_ID(ctx), 0);
		businessPartner.setValue((String) defaultAddress.get("name"));
		businessPartner.setName(name);
		businessPartner.setName2(name2);
		businessPartner.setIsCustomer(true);
		businessPartner.setIsProspect(false);
		businessPartner.setIsVendor(false);
		businessPartner.saveEx();
		int C_Location_ID = createLocation(orderSf);
		int C_BPartner_Location_ID = createBPLocation(businessPartner.getC_BPartner_ID(), C_Location_ID);
		createUser(businessPartner, email, phone, C_BPartner_Location_ID);

		return businessPartner.get_ID();

	}

	private void createUser(MBPartner businessPartner, String email, String phone, int C_BPartner_Location_ID) {
		MUser user = new MUser(ctx, 0, trxName);
		user.setAD_Org_ID(0);
		user.setC_BPartner_ID(businessPartner.getC_BPartner_ID());
		user.setC_BPartner_Location_ID(C_BPartner_Location_ID);
		user.setName(businessPartner.getName());
		user.setEMail(email);
		user.setPhone(phone);
		user.saveEx();
	}

	private int createLocation(Map<?, ?> orderSf) {
		Map<?, ?> customer = (Map<?, ?>) orderSf.get("customer");
		Map<?, ?> defaultAddress = (Map<?, ?>) customer.get("default_address");
		String countryCode = (String) defaultAddress.get("country_code");
		int c_country_id;
		if (isBlankOrNull(countryCode))
			c_country_id = (int) sfDefaults.get_Value("c_country_id");
		else
			c_country_id = DB.getSQLValue(trxName, "select c_country_id " + "from c_country " + "where countrycode = ?",
					countryCode);
		String address1 = (String) defaultAddress.get("address1");
		if (isBlankOrNull(address1))
			address1 = (String) sfDefaults.get_Value("address1");
		String address2 = (String) defaultAddress.get("address2");
		String city = (String) defaultAddress.get("city");
		if (isBlankOrNull(city))
			city = (String) sfDefaults.get_Value("city");
		String postal = (String) defaultAddress.get("zip");
		MLocation location = new MLocation(ctx, c_country_id, 0, city, trxName);
		location.setAD_Org_ID(0);
		location.setAddress1(address1);
		location.setAddress2(address2);
		location.setPostal(postal);
		location.saveEx();
		return location.get_ID();
	}

	private int createBPLocation(int C_BPartner_ID, int C_Location_ID) {
		MBPartnerLocation BPartnerLocation = new MBPartnerLocation(ctx, 0, trxName);
		BPartnerLocation.setAD_Org_ID(0);
		BPartnerLocation.setC_BPartner_ID(C_BPartner_ID);
		BPartnerLocation.setC_Location_ID(C_Location_ID);
		BPartnerLocation.setIsBillTo(true);
		BPartnerLocation.setIsShipTo(true);
		BPartnerLocation.saveEx();
		return BPartnerLocation.getC_BPartner_Location_ID();
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

	public void completeOrder() {
		order.setDocAction(DocAction.ACTION_Complete);
		if (order.processIt(DocAction.ACTION_Complete)) {
			if (log.isLoggable(Level.FINE))
				log.fine("Order: " + order.getDocumentNo() + " completed fine");
		} else
			throw new IllegalStateException("Order: " + order.getDocumentNo() + " Did not complete");

		order.saveEx();
	}

	public void createOrderLine(Map<?, ?> line, Map<?, ?> orderSf) {
		MOrderLine orderLine = new MOrderLine(order);
		orderLine.setAD_Org_ID(order.getAD_Org_ID());
		// orderLine.setAD_Org_ID(11);
		orderLine.setM_Product_ID(getProductId(line.get("name").toString()));
		// orderLine.setC_UOM_ID(originalOLine.getC_UOM_ID());
		// orderLine.setC_Tax_ID(originalOLine.getC_Tax_ID());
		orderLine.setM_Warehouse_ID(order.getM_Warehouse_ID());
		orderLine.setC_Tax_ID(getTaxRate(orderSf));
		// orderLine.setC_Currency_ID(originalOLine.getC_Currency_ID());
		long qty = ((Number) line.get("quantity")).longValue();
		orderLine.setQty(BigDecimal.valueOf((long) qty));
		// orderLine.setC_Project_ID(originalOLine.getC_Project_ID());
		// orderLine.setC_Activity_ID(originalOLine.getC_Activity_ID());
		// orderLine.setC_Campaign_ID(originalOLine.getC_Campaign_ID());
		orderLine.setPrice(new BigDecimal(Double.parseDouble((String) line.get("price"))));
		System.out.println("*********************Unit Price: " + orderLine.getPriceActual());

		if (!orderLine.save()) {
			throw new IllegalStateException("Could not create Order Line");
		}
	}

	public int getProductId(String name) {
		int m_Product_ID = DB.getSQLValue(trxName, "select m_product_id " + "from m_product mp " + "where name like ?",
				name);
		if (m_Product_ID < 0) {
			log.severe("Product : " + name + " does not exist on iDempiere");
			m_Product_ID = (int) sfDefaults.get_Value("m_product_id");
		}
		return sfDefaults.get_ValueAsInt("M_Product_ID");
	}

	public void createShippingCharge(Map<?, ?> orderWc) {
		BigDecimal shippingCost = getShippingCost(orderWc);
		if (shippingCost.compareTo(BigDecimal.ZERO) == 0) {
		return; // no need to create a shipping charge
		}
		
		MOrderLine orderLine = new MOrderLine(order);
		orderLine.setAD_Org_ID(order.getAD_Org_ID());
		orderLine.setC_Charge_ID((int) sfDefaults.get_Value("c_charge_id"));
		// orderLine.setC_UOM_ID(originalOLine.getC_UOM_ID());
		orderLine.setM_Warehouse_ID(order.getM_Warehouse_ID());
		orderLine.setC_Tax_ID(getTaxRate(orderWc));
		// orderLine.setC_Currency_ID(originalOLine.getC_Currency_ID());
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
		// Need to compare to the name of standard_tax_id and not "Standard" return
		return (sfTaxName.equals(idTaxName) ? (int) sfDefaults.get_Value("standard_tax_id")
				: (int) sfDefaults.get_Value("zero_tax_id"));
	}

	public BigDecimal getShippingCost(Map<?, ?> orderWc) {
		List<?> shippingLines = (List<?>) orderWc.get("shipping_lines");
		Map<?, ?> shippingLine = (Map<?, ?>) shippingLines.get(0);
		Double total = Double.parseDouble((String) shippingLine.get("price"));
		
/*		List<?> taxLines = (List<?>) shippingLine.get("tax_lines");
		Map<?, ?> taxLine = (Map<?, ?>) taxLines.get(0);
		Double totalTax = Double.parseDouble((String) taxLine.get("price"));
		//Double totalTax = Double.parseDouble((String) shippingLine.get("total_tax"));
		BigDecimal shippingCost = (!isTaxInclusive) ? BigDecimal.valueOf((Double) total + totalTax)
				: BigDecimal.valueOf((Double) total); */
		BigDecimal shippingCost = BigDecimal.valueOf((Double) total);
		return (shippingCost.setScale(4, RoundingMode.HALF_EVEN));
	}

//	public void createPosPayment(Map<?, ?> orderSf) {
//		X_C_POSPayment posPayment = new X_C_POSPayment(ctx, null, trxName);
//		posPayment.setC_Order_ID(order.getC_Order_ID());
//		posPayment.setAD_Org_ID(order.getAD_Org_ID());
//		posPayment.setPayAmt(new BigDecimal(orderSf.get("total_price").toString()));
//		posPayment.setC_POSTenderType_ID(POSTENDERTYPE_ID); // credit card
//		posPayment.setTenderType(X_C_Payment.TENDERTYPE_CreditCard); // credit card
//		if (!posPayment.save())
//			throw new IllegalStateException("Could not create POSPayment");
//	}

	public static boolean isBlankOrNull(String str) {
		return (str == null || "".equals(str.trim()));
	}
private void inflateMetaData(Map<?, ?> orderWc) {
		
		for(Object item:(ArrayList)orderWc.get("note_attributes")) {
				if(((Map<?,?>)item).get("value").toString().length()>0) {
				String id ="";
				String key = ((Map<?,?>)item).get("name").toString();
				String value = ((Map<?,?>)item).get("value").toString();
				MetaDataObject object = new MetaDataObject(id,key,value);
				metadata.add(object);
			}
		}
		
		
	}
private String getCN(Map<?, ?> orderWc) 
{
	
	
	String CN ="";
	for(MetaDataObject obj :metadata) {
		if(obj.getKey().equalsIgnoreCase(courierCompany))
			CN= obj.getValue();
	}
	
	
	return CN;
}
private int getCBPartner(Map<?, ?> orderWc,MOrder order) 
{
	
	
	
	List<MCourierCompany> companies = new Query(ctx, MCourierCompany.Table_Name, " isactive = 'Y' ", null).setOrderBy(" lineno ").list();
	
	for(MetaDataObject obj :metadata) {
		for(MCourierCompany company:companies) {
			if(company.getValue().trim().equalsIgnoreCase(obj.getKey()))
				if(company.getC_BPartner_ID()>0) {
//					courierCompany = obj.getKey();
					courierCompany = company.getValue();
					return company.getC_BPartner_ID();
					
				}
		}
	}
	
	

	return (Integer) sfDefaults.get_Value("C_BPartner_ID");
}
}