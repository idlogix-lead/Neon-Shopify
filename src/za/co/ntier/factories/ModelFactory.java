package za.co.ntier.factories;

import java.sql.ResultSet;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

import za.co.ntier.model.MCourierCompany;

public class ModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
		
		
		
		if (tableName.equals(MCourierCompany.Table_Name)) { return MCourierCompany.class; }
		
		
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		
		if (tableName.equals(MCourierCompany.Table_Name)) { return new MCourierCompany(Env.getCtx(), Record_ID, trxName); }
		
		
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		
		if (tableName.equals(MCourierCompany.Table_Name)) { return new MCourierCompany(Env.getCtx(), rs, trxName); }
		

		return null;
	}

}

