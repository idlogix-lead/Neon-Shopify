package za.co.ntier.factories;

import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;

import za.co.ntier.forms.PackingForm;
import za.co.ntier.forms.ReturnForm;

public class ShopifyFormFactory implements IFormFactory{

	@Override
	public ADForm newFormInstance(String formName) {
		// TODO Auto-generated method stub
		if(formName.equalsIgnoreCase("PackingForm"))
			return new PackingForm();
		if(formName.equalsIgnoreCase("ReturnForm"))
			return new ReturnForm();

		return null;
	}

}