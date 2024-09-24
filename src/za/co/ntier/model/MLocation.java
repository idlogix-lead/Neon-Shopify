package za.co.ntier.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MLocation extends X_M_Location {

	public MLocation(Properties ctx, int M_Location_ID, String trxName) {
		super(ctx, M_Location_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	

	public MLocation(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MLocation(Properties ctx, String M_Location_UU, String trxName, String... virtualColumns) {
		super(ctx, M_Location_UU, trxName, virtualColumns);
		// TODO Auto-generated constructor stub
	}

	public MLocation(Properties ctx, String M_Location_UU, String trxName) {
		super(ctx, M_Location_UU, trxName);
		// TODO Auto-generated constructor stub
	}
}
