package za.co.ntier.factories;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;

import za.co.ntier.processes.SyncShopifyProduct;
import za.co.ntier.processes.UploadShopifyPrice;
import za.co.ntier.processes.UploadShopifyStock;
import za.co.ntier.processes.Shopify;
import za.co.ntier.processes.SyncShopifyLocation;

public class ShopifyFactory implements IProcessFactory{

	@Override
	public ProcessCall newProcessInstance(String className) {
        if (className.equals("za.co.ntier.processes.Shopify")) {
            return new Shopify();
        } else if (className.equals("za.co.ntier.processes.SyncShopifyProduct")) {
            return new SyncShopifyProduct();
        }else if (className.equals("za.co.ntier.processes.UploadShopifyPrice")) {
            return new UploadShopifyPrice();}
            else if (className.equals("za.co.ntier.processes.SyncShopifyLocation")) {
                return new SyncShopifyLocation();
            
        }else if (className.equals("za.co.ntier.processes.UploadShopifyStock")) {
            return new UploadShopifyStock();
            
        } else {
            return null;
        }

	}

}
