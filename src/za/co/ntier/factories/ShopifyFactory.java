package za.co.ntier.factories;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;
import za.co.ntier.processes.Shopify;

public class ShopifyFactory implements IProcessFactory{

	@Override
	public ProcessCall newProcessInstance(String className) {
		if (className.equals("za.co.ntier.processes.Shopify"))
		return  new Shopify();
		
		return null;
	}

}
