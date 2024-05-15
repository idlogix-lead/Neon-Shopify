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

import org.adempiere.exceptions.AdempiereException;
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
	String courierCode="";
	// private final int priceList_ID = 101;
	final String PAYMENT_RULE = "P";
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
		order.setC_DocTypeTarget_ID(POS_ORDER);
		order.setPaymentRule(PAYMENT_RULE);
		order.setDeliveryRule("F");
		order.setInvoiceRule("D");
		order.set_ValueOfColumn("couriercode", courierCode);	
		try {
			order.saveEx();
		} catch (Exception e) {
			String errorMsg = "Error in Order #"+order.getPOReference()+" -> "+e.getLocalizedMessage();
			throw new AdempiereException(errorMsg);
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
		orderLine.setAD_Org_ID(order.getAD_Org_ID());
		orderLine.setM_Product_ID(getProductId(line.get("product_id").toString()));
		orderLine.setM_Warehouse_ID(order.getM_Warehouse_ID());
		long qty = ((Number) line.get("quantity")).longValue();
		orderLine.setQty(BigDecimal.valueOf((long) qty));
		setPrice(orderLine, line);
		System.out.println("*********************Unit Price: " + orderLine.getPriceActual());
		try {
			orderLine.saveEx();
		} catch (Exception e) {
			String errorMsg = "Error in Order #"+orderLine.getC_Order().getPOReference()+" -> "+e.getLocalizedMessage();
			throw new AdempiereException(errorMsg);
			
		}
		
	}

	public int getProductId(String name) {
		int m_Product_ID = DB.getSQLValue(trxName, "select m_product_id " + "from m_product mp " + "where value like ?",
				name);
		
		return m_Product_ID>0?m_Product_ID:sfDefaults.get_ValueAsInt("M_Product_ID");
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
		orderLine.setC_Tax_ID(getTaxRate(orderWc));
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

private void setPrice(MOrderLine orderLine,Map<?, ?> line) {
	double priceList =Double.parseDouble((String) line.get("price")); 
	double priceActual = priceList ;	 
	List<?> lines = (List<?>) line.get("discount_allocations");
	for (int j = 0; j < lines.size(); j++) {
		Map<?, ?> dicountObj = (Map<?, ?>) lines.get(j);
		priceActual = priceActual -  Double.parseDouble((String) dicountObj.get("amount"));
	}
	orderLine.setPriceList(new BigDecimal(Double.parseDouble((String) line.get("price"))));
	orderLine.setPrice(new BigDecimal(priceActual)); 
	orderLine.setDiscount();
}
}