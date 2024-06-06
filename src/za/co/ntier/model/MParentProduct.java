package za.co.ntier.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MParentProduct extends X_M_Parent_Product {

	public MParentProduct(Properties ctx, int M_Parent_Product_ID, String trxName, String[] virtualColumns) {
		super(ctx, M_Parent_Product_ID, trxName, virtualColumns);
		// TODO Auto-generated constructor stub
	}
	public MParentProduct(Properties ctx, int M_Parent_Product_ID, String trxName) {
		super(ctx, M_Parent_Product_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MParentProduct(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MParentProduct(Properties ctx, String M_Parent_Product_UU, String trxName, String... virtualColumns) {
		super(ctx, M_Parent_Product_UU, trxName, virtualColumns);
		// TODO Auto-generated constructor stub
	}

	public MParentProduct(Properties ctx, String M_Parent_Product_UU, String trxName) {
		super(ctx, M_Parent_Product_UU, trxName);
		// TODO Auto-generated constructor stub
	}
	

}
