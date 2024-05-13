package za.co.ntier.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MCourierCompany extends X_Courier_Company {

	public MCourierCompany(Properties ctx, int Courier_Company_ID, String trxName, String... virtualColumns) {
		super(ctx, Courier_Company_ID, trxName, virtualColumns);
		// TODO Auto-generated constructor stub
	}

	public MCourierCompany(Properties ctx, int Courier_Company_ID, String trxName) {
		super(ctx, Courier_Company_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MCourierCompany(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MCourierCompany(Properties ctx, String Courier_Company_UU, String trxName, String... virtualColumns) {
		super(ctx, Courier_Company_UU, trxName, virtualColumns);
		// TODO Auto-generated constructor stub
	}

	public MCourierCompany(Properties ctx, String Courier_Company_UU, String trxName) {
		super(ctx, Courier_Company_UU, trxName);
		// TODO Auto-generated constructor stub
	}

}
