package za.co.ntier.forms;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListCell;
import org.adempiere.webui.component.ListHead;
import org.adempiere.webui.component.ListHeader;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.window.Dialog;
import org.compiere.model.I_C_Order;
import org.compiere.model.MCharge;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MRMA;
import org.compiere.model.MRMALine;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.model.X_M_RMA;
import org.compiere.model.X_M_RMALine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Vlayout;
public class ReturnForm  extends ADForm {

	Timestamp formTime;
	MOrder order;
	Button completeBtn = new Button("Create Return");
	WListbox orderData;
	Textbox courierCode = new Textbox();
	Textbox barCode = new Textbox();
	Label hdrLabel = new Label("Packing Form");
	Label cnLabel = new Label("CN Number:");
	Label bpLabel = new Label("Business Partner:");
	Label soLabel = new Label("SO Number:");
	Label sodateLabel = new Label("SO Date:");
	ListModelTable listModel;
	MInOut shipment;
	Vlayout mainLayout;
	final String buttonCSS = "background: linear-gradient(to right, #54a13f, #54a13f);color:white;";
	final String hdrLabelCSS = "background-color:#d9e8fa;text-align: center;font-family: 'Brush Script MT', cursive; font-size: 30px; font-weight: bold;display: block;";
	Map<String,Integer> shipLines = new HashMap<String,Integer>();
	List<MInOut> ships;
	@Override
	protected void initForm() {
		


		
		
		
		// TODO Auto-generated method stub
		courierCode.setPlaceholder("Scan CN#");
		barCode.setPlaceholder("Scan Product Code");
		courierCode.addEventListener(Events.ON_OK, this);
		barCode.addEventListener(Events.ON_OK, this);
		completeBtn.addEventListener(Events.ON_CLICK, this);
		
		completeBtn.setStyle(buttonCSS);
		hdrLabel.setStyle(hdrLabelCSS);
		hdrLabel.setHflex("1");
		
		
		
		 mainLayout = new Vlayout();
		this.appendChild(mainLayout);
		
		Hlayout inputElements = new Hlayout();
		inputElements.appendChild(courierCode);
		inputElements.appendChild(barCode);
		inputElements.appendChild(completeBtn);
		
		mainLayout.appendChild(hdrLabel);
		mainLayout.appendChild(inputElements);
		
		Grid title = new Grid();
		Rows rows = new Rows();
		title.appendChild(rows);
		Row row = rows.newRow();
		rows.appendChild(row);
		row.appendCellChild(cnLabel);
		row.appendCellChild(soLabel);
		row = rows.newRow();
		rows.appendChild(row);
		row.appendChild(bpLabel);
		row.appendChild(sodateLabel);
		mainLayout.appendChild(title);
		
		
		
//		orderData = new WListbox();
//		orderData.setWidth("100%");
//		mainLayout.appendChild(orderData);
//		ListHead head = new ListHead();
//		orderData.appendChild(head);
//		orderData.setId("detailbox");
//		ListHeader header =  new ListHeader("Sr#");
//		header.setWidth("5%");
//		header.setStyle("background-color:#375363;color:white;");
//		head.appendChild(header);
//		header =  new ListHeader("Product Code");
//		header.setWidth("25%");
//		head.appendChild(header);
//		header =  new ListHeader("Product Name");
//		header.setWidth("50%");
//		head.appendChild(header);
//		header =  new ListHeader("Order Qty");
//		header.setWidth("10%");
//		head.appendChild(header);
//		header =  new ListHeader("Return Qty");
//		header.setWidth("10%");
//		head.appendChild(header);
//		header =  new ListHeader("Amount");
//		header.setWidth("10%");
//		head.appendChild(header);
//		
//		for(Object o:head.getChildren()) {
//			ListHeader h = (ListHeader)o;
//			h.setStyle("background-color:#d9e8fa;");
//		}
		
	insertListbox();
		
		
		
		
	}
	
	private void insertListbox() {

		
		

		for(Object o:mainLayout.getChildren()) {
			if(o instanceof WListbox)
			{
				
				mainLayout.removeChild((Component)o);
				o = null;
			}
		}

		orderData = new WListbox();
		orderData.setWidth("100%");
		mainLayout.appendChild(orderData);
		ListHead head = new ListHead();
		orderData.appendChild(head);
		orderData.setId("detailbox");
		ListHeader header =  new ListHeader("Sr#");
		header.setWidth("5%");
		header.setStyle("background-color:#375363;color:white;");
		head.appendChild(header);
		header =  new ListHeader("Product Code");
		header.setWidth("25%");
		head.appendChild(header);
		header =  new ListHeader("Product Name");
		header.setWidth("50%");
		head.appendChild(header);
		header =  new ListHeader("Order Qty");
		header.setWidth("10%");
		head.appendChild(header);
		header =  new ListHeader("Return Qty");
		header.setWidth("10%");
		head.appendChild(header);
		header =  new ListHeader("Amount");
		header.setWidth("10%");
		head.appendChild(header);
		
		for(Object o:head.getChildren()) {
			ListHeader h = (ListHeader)o;
			h.setStyle("background-color:#d9e8fa;");
		}
		
	}
	
	
	
	@Override
	public void onPageDetached(Page page) {
		// TODO Auto-generated method stub
		if(order!=null) {
			order.set_ValueOfColumn("isScanning", false);
			order.save();
		}
		super.onPageDetached(page);
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		if(event.getTarget()==courierCode && event.getName().equals(Events.ON_OK)) {
			
			
			String value = courierCode.getRawValue().toString().trim();
			if(order!=null) {
				String oldCn = order.get_ValueAsString("CourierCode");
				String oldporeference = order.getPOReference();
				if(List.of(oldCn,oldporeference).contains(value)) {
					reset();
					
				}
				else {
					reset();
				}
			}
//			orderData.removeAllItems();
			updateTitleSection("", "", "", "");
			getOrderData(value);
			barCode.setFocus(true);
			
		}
		if(event.getTarget()==barCode && event.getName().equals(Events.ON_OK)) {
			if(order!=null) { 
					String answer = DB.getSQLValueStringEx(null, "select isscanning from c_order where (case when CourierCode is null then poreference=? else CourierCode=? end) and scannedhash <> ? ", order.getPOReference(),order.get_ValueAsString("couriercode"),42);
				order = new MOrder(Env.getCtx(), order.getC_Order_ID(),null);
				if(answer !=null &&	 answer.equals("Y"))
				{
					String scannerName = (new MUser(Env.getCtx(), order.get_ValueAsInt("ScannedBy"), null)).getName();
					showError("Order is currently Scanned by | "+scannerName+" |");
					return;
				}
			}
			
			String value = barCode.getRawValue().toString().trim();
			updateScanQty(value);
			barCode.setRawValue("");
			
		}
		if(event.getTarget()==completeBtn && event.getName().equals(Events.ON_CLICK)) {
//			
			completeOrder();
//			reset();	
//			courierCode.setRawValue("");
		}
	}
	
	
	private  void getOrderData(String cn) {

			List<MOrder> list = new Query(Env.getCtx(), I_C_Order.Table_Name, " (case when CourierCode is null then poreference=? else CourierCode=? end)  AND docstatus IN ('CO')", null)
					.setParameters(cn,cn).setOrderBy(" created")
					.list();
			if(list!=null && list.size()>0) {
				order = list.get(0);
				ships = new Query(Env.getCtx(), MInOut.Table_Name, " c_order_id = ? ", null)
						.setParameters(order.getC_Order_ID()).setOrderBy(" created")
						.list();
				if(list!=null && list.size()>0)
					shipment = ships.get(0);
				String cnNumber = order.get_ValueAsString("CourierCode");
				String poReference = order.getPOReference();
				String bpartner = order.getC_BPartner().getName();
				Timestamp orderDate =order.getDateOrdered();
				SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
				String soDate = format.format(orderDate);
				updateTitleSection(cnNumber,poReference,bpartner,soDate);
//				setOrderDetail(order);
				populateTable(ships);
			}
			
			
		
		
	}
	private void reset() {
		order=null;
		insertListbox();
//		orderData = new WListbox();
		
//		orderData.removeAllItems();
//		((WListbox)orderData).getModel().clear();

//		for(int i=0;i<orderData.getModel().getSize();i++) {
//			orderData.removeItemAt(i);
//		}
//		orderData.getChildren().clear();
		
//		for(Component c: orderData.getChildren()) {
//			if(c instanceof Listitem)
//				orderData.removeChild(c);
//		}
//		listModel = null;
		updateTitleSection("", "", "", "");
		
		courierCode.setFocus(true);
	}
	private void updateTitleSection(String CN,String SO,String BP,String SODate) {
		cnLabel.setValue("CN Number : "+CN);		
		bpLabel.setValue("Business Partner : "+BP);
		soLabel.setValue("SO Number : "+SO);
		sodateLabel.setValue("SO Date : "+SODate);
		
		
		
	}
	
	
	
	
	private void updateScanQty(String prodCode) {
		boolean productExist = false;
		int orderSum = 0;
		int scanSum=0;
		for(Listitem item:orderData.getItems()) {
			Listcell cell = (Listcell)item.getChildren().get(1);
			String itemCode = cell.getLabel();
			if(itemCode.equals(prodCode)) {
				cell = (Listcell)item.getChildren().get(3);
				String orderqty = cell.getLabel().endsWith(".0")?cell.getLabel().substring(0, cell.getLabel().length() - 2):cell.getLabel();
				int orderQty = Integer.parseInt(orderqty);
				orderSum+=orderQty;
				cell = (Listcell)item.getChildren().get(4);
				int scanQty = Integer.parseInt(cell.getLabel());
				scanSum+=scanQty;
			}
				
		}
		for(Listitem item:orderData.getItems()) {
			Listcell cell = (Listcell)item.getChildren().get(1);
			String itemCode = cell.getLabel();
			
			if(itemCode.equals(prodCode)) {
				productExist = true;
				cell = (Listcell)item.getChildren().get(3);
				String orderqty = cell.getLabel().endsWith(".0")?cell.getLabel().substring(0, cell.getLabel().length() - 2):cell.getLabel();
				int orderQty = Integer.parseInt(orderqty);
				cell = (Listcell)item.getChildren().get(4);
				int scanQty = Integer.parseInt(cell.getLabel());
				
				if(scanQty<orderQty) {
					scanQty+=1;
					cell.setLabel(String.valueOf(scanQty));
					updateScanQtyModel(itemCode);
					break;
				}
				if(scanSum>=orderSum) {
					showError("Extra Qty Scanned!");
					break;
				}
				
			}
		}
		if(!productExist)
			showError("Wrong Product Scanned!");
		if(isFullyScanned()) {
			completeBtn.setVisible(true);
		}
	}
	
	private boolean isFullyScanned() {
		int orderQty=0;
		int scanQty = 0;
		for(Listitem item:orderData.getItems()) {
			Listcell cell = (Listcell)item.getChildren().get(1);
			cell = (Listcell)item.getChildren().get(3);
			String orderqty = cell.getLabel().endsWith(".0")?cell.getLabel().substring(0, cell.getLabel().length() - 2):cell.getLabel();
			orderQty = Integer.parseInt(orderqty);
			cell = (Listcell)item.getChildren().get(4);
			scanQty += Integer.parseInt(cell.getLabel());
		}
		return orderQty==scanQty && (orderQty>0);
	}
	
	private void completeOrder() {
		
		createCustomerRMA(ships);
		createReturn(ships);
		createInvoice(ships);
		reset();
		
	}
	

	private void showError(String message) 
	{
		Dialog.error(m_WindowNo, message, "");
	}



	
	private void updateScanQtyModel(String prodCode) {
		ListModelTable model = ((WListbox)orderData).getModel();
		for(int i = 0;i<model.getSize();i++) {
			DataObject obj = (DataObject) ((WListbox)orderData).getModel().getDataAt(i,6);
			String code = obj.getProdCode();
			BigDecimal OrderQty = obj.getOrderQty();
			BigDecimal ScanQty = obj.getScanQty();
			if(code.equalsIgnoreCase(prodCode) && OrderQty.compareTo(ScanQty)==1) {
				obj.setScanQty(obj.getScanQty().add(Env.ONE));
				return;
			}
		}
	}

	
	void createCustomerRMA(List<MInOut> shipments) {
		
		if(getScannedQty().compareTo(Env.ZERO)<=0)
			return;
		List<X_M_RMA> rmas = new ArrayList<X_M_RMA>();
		for(MInOut shipment:shipments) {
			MRMA RMA = new MRMA(Env.getCtx(), 0,null);
			RMA.setName("RMA for Order# "+order.getDocumentNo());
			RMA.setC_DocType_ID(1000031);
			RMA.setM_RMAType_ID(1000000);
			RMA.setInOut_ID(shipment.getM_InOut_ID());
			RMA.setSalesRep_ID(Env.getAD_User_ID(Env.getCtx()));
			RMA.setC_BPartner_ID(shipment.getC_BPartner_ID());
			RMA.setDocAction("CO");
			RMA.setIsSOTrx(true);
			RMA.setDocStatus("DR");
			RMA.save();
			rmas.add(RMA);
			ListModelTable model = ((WListbox)orderData).getModel();
			for(int i = 0;i<model.getSize();i++) {
				DataObject obj = (DataObject) ((WListbox)orderData).getModel().getDataAt(i,6);
				if(shipment.getM_InOut_ID()!=obj.getShipment().getM_InOut_ID())
					continue;
				BigDecimal amount = obj.getAmount();
				BigDecimal qty = obj.getScanQty();
				int prodId = obj.getProduct().getM_Product_ID();
				if(prodId>0 && amount.compareTo(Env.ZERO)>=0 && qty.compareTo(Env.ZERO)==1) {
					System.out.println(prodId);
					MRMALine line  = new MRMALine(Env.getCtx(), 0, null);
					line.setAD_Org_ID(RMA.getAD_Org_ID());
					line.setM_RMA_ID(RMA.get_ID());
					line.setM_InOutLine_ID(obj.getShipLine().get_ID());
					line.setM_Product_ID(prodId);
					line.setQty(obj.getScanQty());
					line.setAmt(amount.divide(qty).setScale(0, BigDecimal.ROUND_DOWN));
					line.setC_Tax_ID(1000000);
					line.setLineNetAmt(amount.multiply(qty).setScale(0, BigDecimal.ROUND_DOWN));
					line.save();
					obj.setRMALine(line);
				}
				
			}
			List<MRMALine> list = new Query(Env.getCtx(), MRMALine.Table_Name, " m_rma_id = ? ", RMA.get_TrxName())
					.setParameters(RMA.get_ID()).setOrderBy(" created ")
					.list();
			if(list==null || list.size()==0) {
				RMA.delete(true);
				throw new AdempiereException("Customer RMA lines were not Created!");
			}
			
			RMA.setDocAction("CO");
			if (RMA.processIt("CO"))
			{
				RMA.saveEx();	
			} else {	
				throw new IllegalStateException("RMA Process Failed: " + order + " - " + RMA.getProcessMsg());
			}
		}	
	}

	private void createReturn(List<MInOut> shipments) {
		if(getScannedQty().compareTo(Env.ZERO)<=0)
			return;
		for(MInOut shipment:shipments) {
			MInOut cReturn = new MInOut(Env.getCtx(), 0, null);
			cReturn.setAD_Org_ID(Env.getAD_Org_ID(Env.getCtx()));
			cReturn.setC_DocType_ID(1000015);
			cReturn.setC_BPartner_ID(shipment.getC_BPartner_ID());
			String loc = Env.getContext(Env.getCtx(), getWindowNo(), Env.TAB_INFO, "C_BPartner_Location_ID");
			int locationId = 1000036;
			cReturn.setC_BPartner_Location_ID(locationId);
			cReturn.setMovementDate(new Timestamp(System.currentTimeMillis()));
			cReturn.setDateAcct(new Timestamp(System.currentTimeMillis()));
			cReturn.setM_Warehouse_ID(1000000);
			cReturn.setPriorityRule("5");
			cReturn.setFreightCostRule("I");
			cReturn.setSalesRep_ID(Env.getAD_User_ID(Env.getCtx()));
			cReturn.setDocAction("CO");
			cReturn.setDocStatus("DR");
			cReturn.save();
			ListModelTable model = ((WListbox)orderData).getModel();
			for(int i = 0;i<model.getSize();i++) {
				DataObject obj = (DataObject) ((WListbox)orderData).getModel().getDataAt(i,6);
				if(shipment.getM_InOut_ID()!=obj.getShipment().getM_InOut_ID())
					continue;
				BigDecimal amount = obj.getAmount();
				BigDecimal qty = obj.getScanQty();
				int prodId = obj.getProduct().getM_Product_ID();
				if(prodId>0 && amount.compareTo(Env.ZERO)>=0 && qty.compareTo(Env.ZERO)==1) {
					System.out.println(prodId);
				MProduct p = new MProduct(Env.getCtx(),obj.getProduct().getM_Product_ID(),null);
				MInOutLine line  = new MInOutLine(Env.getCtx(), 0, null);
				line.setAD_Org_ID(cReturn.getAD_Org_ID());
				line.setM_InOut_ID(cReturn.getM_InOut_ID());
				line.setM_Product_ID(obj.getProduct().getM_Product_ID());
				line.setQty(qty);
				line.setM_Locator_ID(1000001);
				line.setM_RMALine_ID(obj.getRMALine().get_ID());
				line.setC_UOM_ID(p.getC_UOM_ID());
				line.save();
				obj.setReturnLine(line);
				}
			}
			
			
			
			cReturn.setDocAction("CO");
			if (cReturn.processIt("CO"))
			{
				order.saveEx();	
			} else {
				throw new IllegalStateException("Return Process Failed: " + order + " - " + cReturn.getProcessMsg());
			}
			}
	}
	private void createInvoice(List<MInOut> shipments) {
		if(getScannedQty().compareTo(Env.ZERO)<=0)
			return;
            for(MInOut shipment:shipments) {
			
			MInvoice invoice  = new MInvoice(Env.getCtx(),0,null);
			invoice.setIsSOTrx(true);
			invoice.setAD_Org_ID(shipment.getAD_Org_ID());
			invoice.setC_Order_ID(shipment.getC_Order_ID());
			invoice.setDateOrdered(shipment.getC_Order().getDateOrdered());
			invoice.setPOReference(shipment.getC_Order().getPOReference());
			invoice.setC_DocTypeTarget_ID(1000004);
			invoice.setDateInvoiced(new Timestamp(System.currentTimeMillis()));
			invoice.setDateAcct(new Timestamp(System.currentTimeMillis()));
			invoice.setBPartner(shipment.getBPartner());	
			invoice.setM_PriceList_ID(1000001);
			invoice.setC_Currency_ID(306);
			invoice.setPaymentRule("P");
			invoice.setC_PaymentTerm_ID(1000000);
			invoice.setDocAction("CO");
			invoice.setDocStatus("DR");
			invoice.save();
			
			ListModelTable model = ((WListbox)orderData).getModel();
			for(int i = 0;i<model.getSize();i++) {
				DataObject obj = (DataObject) ((WListbox)orderData).getModel().getDataAt(i,6);
				if(shipment.getM_InOut_ID()!=obj.getShipment().getM_InOut_ID())
					continue;
				if(shipment==null || shipment.getM_InOut_ID()<=0)
					return;
				BigDecimal amount = obj.getAmount();
				BigDecimal qty = obj.getScanQty();
				int prodId = obj.getProduct().getM_Product_ID();
				if(prodId>0 && amount.compareTo(Env.ZERO)>=0 && qty.compareTo(Env.ZERO)==1) {
					MInvoiceLine line = new MInvoiceLine(Env.getCtx(),0,null);
					line.setInvoice(invoice);
					line.setC_Invoice_ID(invoice.get_ID());
					line.setM_InOutLine_ID(obj.getReturnLine().get_ID());
					line.setM_Product_ID(obj.getShipLine().getM_Product_ID());
					line.setC_UOM_ID(obj.getProduct().getC_UOM_ID());
					line.setQty(qty);
//					line.setPriceEntered(amount.divide(qty).setScale(0, BigDecimal.ROUND_DOWN));
					line.setPrice(amount.divide(qty).setScale(0, BigDecimal.ROUND_DOWN));
					line.setC_Tax_ID(1000000);
					line.save();
					
					}
				}
			invoice.setDocAction("CO");
			if (invoice.processIt("CO"))
			{
				order.saveEx();	
			} else {
				throw new IllegalStateException("Invoice Process Failed: " + order + " - " + invoice.getProcessMsg());
			}
		}
	}
	
	
	
	
	private void populateTable(List<MInOut> shipments ) {
		
		String sql = "select \r\n"
				+ "iol.m_inoutline_id,io.m_inout_id,coalesce(p.m_product_id,0)m_product_id,coalesce(iol.c_charge_id,0)c_charge_id,coalesce(p.value,c.name)value,\r\n"
				+ "coalesce(p.name,c.name)name,iol.movementqty-coalesce((select SUM(qty) from m_rmaline where m_inoutline_id = iol.m_inoutline_id),0) qty,\r\n"
				+ "coalesce(ivl.priceentered*(iol.movementqty-coalesce((select SUM(qty) from m_rmaline where m_inoutline_id = iol.m_inoutline_id),0)),0) amount\r\n"
				+ "\r\n"
				+ "from c_invoiceline ivl\r\n"
				+ "join m_inoutline iol ON ivl.m_inoutline_id = iol.m_inoutline_id\r\n"
				+ "join m_inout io ON iol.m_inout_id = io.m_inout_id\r\n"
				+ "left join m_product p ON iol.m_product_id =p.m_product_id\r\n"
				+ "left join c_charge c ON iol.c_charge_id =c.c_charge_id\r\n"
				+ "where io.c_order_id = "+ order.getC_Order_ID() +" and io.issotrx = 'Y'  and iol.movementqty <> 0\r\n"
				+ "and iol.movementqty-coalesce((select SUM(qty) from m_rmaline where m_inoutline_id = iol.m_inoutline_id),0)>0\r\n"
				+ "order by io.documentno,p.value\r\n"
				+ "\r\n"
				+ "";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				
				pstmt = DB.prepareStatement (sql.toString(), null);
				rs = pstmt.executeQuery ();
				int sr=1;
				List<List<Object>> model = new ArrayList<List<Object>>();
				while (rs.next ())		
				{
				MProduct product = new MProduct(Env.getCtx(),rs.getInt("m_product_id"),null);
				MInOutLine line = new MInOutLine(Env.getCtx(),rs.getInt("m_inoutline_id"),null);
				MInOut shipment = new MInOut(Env.getCtx(),rs.getInt("m_inout_id"),null);
				MCharge charge = new MCharge(Env.getCtx(),rs.getInt("c_charge_id"),null);
				DataObject object = new DataObject(rs.getString("value"), rs.getString("name"),
						rs.getBigDecimal("qty"), Env.ZERO, rs.getBigDecimal("amount"), product,charge,
							line,(MOrderLine)line.getC_OrderLine(),shipment);
				List<Object> list =new ArrayList<Object>();
				list.add(sr);
				list.add(object.getProdCode());
				list.add(object.getProdName());
				list.add(object.getOrderQty());
				list.add("0");
				list.add(object.getAmount());
				list.add(object);
				model.add(list);
				sr += 1;
				}

				listModel = new ListModelTable(model);
				orderData.setModel(listModel);
				
				
				orderData.setItemRenderer((item,data,index)->{
					List<Object> list  = (List<Object>)data;
					ListCell cell = new ListCell(list.get(0).toString());
					item.appendChild(cell);
					cell = new ListCell(list.get(1).toString());
					item.appendChild(cell);
					cell = new ListCell(list.get(2).toString());
					item.appendChild(cell);
					cell = new ListCell(list.get(3).toString());
					item.appendChild(cell);
					cell = new ListCell(list.get(4).toString());
					item.appendChild(cell);
					cell = new ListCell();
					cell = new ListCell(list.get(5).toString());
					item.appendChild(cell);
				});
			}
			catch (Exception e)
			{
				throw new AdempiereException(e);
			}
	}
	
	private BigDecimal getScannedQty() {
		BigDecimal qty = Env.ZERO;
		ListModelTable model = ((WListbox)orderData).getModel();
		for(int i = 0;i<model.getSize();i++) {
			DataObject obj = (DataObject) ((WListbox)orderData).getModel().getDataAt(i,6);
			qty = qty.add(obj.getScanQty());
		}
		return qty;
	}
	
	public class DataObject {
		String prodCode;
		String prodName;
		BigDecimal orderQty;
		BigDecimal scanQty;
		BigDecimal amount;
		MProduct product;
		MCharge charge;
		MInOut shipment;
		MInOutLine shipLine;
		MInOutLine returnLine;
		MOrderLine orderLine;
		X_M_RMALine rmaLine;
		public DataObject(String prodCode, String prodName, BigDecimal orderQty, BigDecimal scanQty, BigDecimal amount,
				MProduct product,MCharge charge, MInOutLine shipLine, MOrderLine orderLine,MInOut shipment) {
			super();
			this.prodCode = prodCode;
			this.prodName = prodName;
			this.orderQty = orderQty;
			this.scanQty = scanQty;
			this.amount = amount;
			this.product = product;
			this.charge = charge;
			this.shipLine = shipLine;
			this.orderLine = orderLine;
			this.shipment = shipment;
		}
		public String getProdCode() {
			return prodCode;
		}
		public void setProdCode(String prodCode) {
			this.prodCode = prodCode;
		}
		public String getProdName() {
			return prodName;
		}
		public void setProdName(String prodName) {
			this.prodName = prodName;
		}
		public BigDecimal getOrderQty() {
			return orderQty;
		}
		public void setOrderQty(BigDecimal orderQty) {
			this.orderQty = orderQty;
		}
		public BigDecimal getScanQty() {
			return scanQty;
		}
		public void setScanQty(BigDecimal scanQty) {
			this.scanQty = scanQty;
		}
		public BigDecimal getAmount() {
			return amount;
		}
		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}
		public MProduct getProduct() {
			return product;
		}
		public void setProduct(MProduct product) {
			this.product = product;
		}
		public MCharge getCharge() {
			return charge;
		}
		public void setCharge(MCharge charge) {
			this.charge = charge;
		}
		public MInOutLine getShipLine() {
			return shipLine;
		}
		public void setShipLine(MInOutLine shipLine) {
			this.shipLine = shipLine;
		}
		public MOrderLine getOrderLine() {
			return orderLine;
		}
		public void setOrderLine(MOrderLine orderLine) {
			this.orderLine = orderLine;
		}
		public MInOut getShipment() {
			return shipment;
		}
		public void setShipment(MInOut shipment) {
			this.shipment = shipment;
		}
		public X_M_RMALine getRMALine() {
			return rmaLine;
		}
		public void setRMALine(X_M_RMALine line) {
			this.rmaLine = line;
		}
		public MInOutLine getReturnLine() {
			return returnLine;
		}
		public void setReturnLine(MInOutLine returnLine) {
			this.returnLine = returnLine;
		}
		
	}
}
