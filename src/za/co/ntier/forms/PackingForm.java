package za.co.ntier.forms;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListCell;
import org.adempiere.webui.component.ListHead;
import org.adempiere.webui.component.ListHeader;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.window.Dialog;
import org.compiere.model.I_C_Order;
import org.compiere.model.MOrder;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Vlayout;
public class PackingForm  extends ADForm {

	Timestamp formTime;
	MOrder order;
	Button completeBtn = new Button("Complete Order");
	Listbox orderData;
	Textbox courierCode = new Textbox();
	Textbox barCode = new Textbox();
	Label hdrLabel = new Label("Packing Form");
	Label cnLabel = new Label("CN Number:");
	Label bpLabel = new Label("Business Partner:");
	Label soLabel = new Label("SO Number:");
	Label sodateLabel = new Label("SO Date:");
	
	final String buttonCSS = "background: linear-gradient(to right, #54a13f, #54a13f);color:white;";
	final String hdrLabelCSS = "background-color:#d9e8fa;text-align: center;font-family: 'Brush Script MT', cursive; font-size: 30px; font-weight: bold;display: block;";
	
	
	@Override
	protected void initForm() {
		


		
		
		
		// TODO Auto-generated method stub
		courierCode.setPlaceholder("Scan CN#");
		barCode.setPlaceholder("Scan Product Code");
		courierCode.addEventListener(Events.ON_OK, this);
		barCode.addEventListener(Events.ON_OK, this);
		completeBtn.addEventListener(Events.ON_CLICK, this);
		completeBtn.setVisible(false);
		completeBtn.setStyle(buttonCSS);
		hdrLabel.setStyle(hdrLabelCSS);
		hdrLabel.setHflex("1");
		
		
		
		Vlayout mainLayout = new Vlayout();
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
		
		
		
		orderData = new Listbox();
		orderData.setWidth("100%");
		mainLayout.appendChild(orderData);
		ListHead head = new ListHead();
		orderData.appendChild(head);
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
		header =  new ListHeader("Scanned Qty");
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
					order.set_ValueOfColumn("isScanning", false);
					order.save();
				}
			}
			orderData.removeAllItems();
			updateTitleSection("", "", "", "");
			getOrderData(value);
			barCode.setFocus(true);
			completeBtn.setVisible(false);
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
//			completeBtn.setVisible(false);
			completeOrder();
			reset();	
			courierCode.setRawValue("");
		}
	}
	
	
	private  void getOrderData(String cn) {

			List<MOrder> list = new Query(Env.getCtx(), I_C_Order.Table_Name, " (case when CourierCode is null then poreference=? else CourierCode=? end)  AND docstatus IN ('DR','IN')", null)
					.setParameters(cn,cn).setOrderBy(" created")
					.list();
			if(list!=null && list.size()>0) {
				order = list.get(0);
				if(order.get_ValueAsBoolean("isScanning"))
				{
					String scannerName = (new MUser(Env.getCtx(), order.get_ValueAsInt("ScannedBy"), null)).getName();
					showError("Order is currently Scanned by | "+scannerName+" |");
					return;
				}
				order.set_ValueOfColumn("isScanning", true);
				order.set_ValueOfColumn("ScannedBy", Env.getAD_User_ID(Env.getCtx()));
				order.set_ValueOfColumn("ScannedAt", (new Timestamp(System.currentTimeMillis())));
				order.set_ValueOfColumn("ScannedHash", order.hashCode());
				order.saveEx();
				
				String cnNumber = order.get_ValueAsString("CourierCode");
				String poReference = order.getPOReference();
				String bpartner = order.getC_BPartner().getName();
				Timestamp orderDate =order.getDateOrdered();
				SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
				String soDate = format.format(orderDate);
				updateTitleSection(cnNumber,poReference,bpartner,soDate);
				setOrderDetail(order);
			}
			
			
		
		
	}
	private void reset() {
		order=null;
		orderData.removeAllItems();
		updateTitleSection("", "", "", "");
		completeBtn.setVisible(false);
		courierCode.setFocus(true);
	}
	private void updateTitleSection(String CN,String SO,String BP,String SODate) {
		cnLabel.setValue("CN Number : "+CN);		
		bpLabel.setValue("Business Partner : "+BP);
		soLabel.setValue("SO Number : "+SO);
		sodateLabel.setValue("SO Date : "+SODate);
		
		
		
	}
	
	private void setOrderDetail(MOrder order) {
		
		String sql = "select p.value,p.name,SUM(qtyordered) qty \n"
				+ "from c_orderline ol\n"
				+ "join m_product p ON ol.m_product_id = p.m_product_id\n"
				+ "where ol.m_product_id is not null and  c_order_id = "+order.getC_Order_ID()+" \n"
				+ "group by p.value,p.name";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			
			pstmt = DB.prepareStatement (sql.toString(), null);
			rs = pstmt.executeQuery ();
			int sr=0;
			while (rs.next ())		
			{
				sr += 1;
				String prodCode = rs.getString("value");
				String prodName = rs.getString("name");
				BigDecimal orderQty = rs.getBigDecimal("qty");
				String scanQty = "0";
				ListItem item = new ListItem();
				orderData.appendChild(item);
				ListCell cell = new ListCell(String.valueOf(sr));
				item.appendChild(cell);
				cell = new ListCell(prodCode);
				item.appendChild(cell);
				cell = new ListCell(prodName);
				item.appendChild(cell);
				cell = new ListCell(String.valueOf(orderQty.setScale(0, BigDecimal.ROUND_DOWN)));
				item.appendChild(cell);
				cell = new ListCell(scanQty);
				item.appendChild(cell);
			}
		}
		catch (Exception e)
		{
			throw new AdempiereException(e);
		}
		
	}
	
	
	private void updateScanQty(String prodCode) {
		boolean productExist = false;
		for(Listitem item:orderData.getItems()) {
			Listcell cell = (Listcell)item.getChildren().get(1);
			String itemCode = cell.getLabel();
			
			if(itemCode.equals(prodCode)) {
				productExist = true;
				cell = (Listcell)item.getChildren().get(3);
				int orderQty = Integer.parseInt(cell.getLabel());
				cell = (Listcell)item.getChildren().get(4);
				int scanQty = Integer.parseInt(cell.getLabel());
				if(scanQty<orderQty)
				scanQty+=1;
				else {
					showError("Extra Qty Scanned!");
				break;
				}
				cell.setLabel(String.valueOf(scanQty));
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
			orderQty += Integer.parseInt(cell.getLabel());
			cell = (Listcell)item.getChildren().get(4);
			scanQty += Integer.parseInt(cell.getLabel());
		}
		return orderQty==scanQty && (orderQty>0);
	}
	
	private void completeOrder() {
	
		
			if(order==null || order.getC_Order_ID()<=0 )
				return;
			
				
				order.setDocAction("CO");
				
			if (order.processIt("CO"))
			{
				order.set_ValueOfColumn("isScanning", false);
				order.saveEx();
				
				
			} else {
				order.set_ValueOfColumn("isScanning", false);
				order.save();
				throw new IllegalStateException("Order Process Failed: " + order + " - " + order.getProcessMsg());
				
			}
		
		
	}
	

	private void showError(String message) 
	{
		Dialog.error(m_WindowNo, message, "");
	}
	

	
	
}
