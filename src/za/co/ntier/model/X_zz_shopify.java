/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package za.co.ntier.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for zz_shopify
 *  @author iDempiere (generated) 
 *  @version Release 8.1 - $Id$ */
public class X_zz_shopify extends PO implements I_zz_shopify, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20210212L;

    /** Standard Constructor */
    public X_zz_shopify (Properties ctx, int zz_shopify_ID, String trxName)
    {
      super (ctx, zz_shopify_ID, trxName);
      /** if (zz_shopify_ID == 0)
        {
			setAddress1 (null);
			setC_BPartner_ID (0);
			setC_Charge_ID (0);
			setC_Country_ID (0);
			setCity (null);
			setconsumerkey (null);
			setconsumersecret (null);
			setIntl_Excl_PriceList_ID (0);
			setIntl_Incl_PriceList_ID (0);
			setLocal_Excl_PriceList_ID (0);
			setLocal_Incl_PriceList_ID (0);
			setM_Product_ID (0);
			setM_Warehouse_ID (0);
			setStandard_Tax_ID (0);
			setsyncfrom (null);
			setURL (null);
			setZero_Tax_ID (0);
			setzz_shopify_ID (0);
        } */
    }

    /** Load Constructor */
    public X_zz_shopify (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 7 - System - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_zz_shopify[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Address 1.
		@param Address1 
		Address line 1 for this location
	  */
	public void setAddress1 (String Address1)
	{
		set_ValueNoCheck (COLUMNNAME_Address1, Address1);
	}

	/** Get Address 1.
		@return Address line 1 for this location
	  */
	public String getAddress1 () 
	{
		return (String)get_Value(COLUMNNAME_Address1);
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_Name)
			.getPO(getC_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner .
		@param C_BPartner_ID 
		Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner .
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException
    {
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_Name)
			.getPO(getC_Charge_ID(), get_TrxName());	}

	/** Set Charge.
		@param C_Charge_ID 
		Additional document charges
	  */
	public void setC_Charge_ID (int C_Charge_ID)
	{
		if (C_Charge_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Charge_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Charge_ID, Integer.valueOf(C_Charge_ID));
	}

	/** Get Charge.
		@return Additional document charges
	  */
	public int getC_Charge_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Charge_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Country getC_Country() throws RuntimeException
    {
		return (org.compiere.model.I_C_Country)MTable.get(getCtx(), org.compiere.model.I_C_Country.Table_Name)
			.getPO(getC_Country_ID(), get_TrxName());	}

	/** Set Country.
		@param C_Country_ID 
		Country 
	  */
	public void setC_Country_ID (int C_Country_ID)
	{
		if (C_Country_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Country_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Country_ID, Integer.valueOf(C_Country_ID));
	}

	/** Get Country.
		@return Country 
	  */
	public int getC_Country_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Country_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set City.
		@param City 
		Identifies a City
	  */
	public void setCity (String City)
	{
		set_ValueNoCheck (COLUMNNAME_City, City);
	}

	/** Get City.
		@return Identifies a City
	  */
	public String getCity () 
	{
		return (String)get_Value(COLUMNNAME_City);
	}

	/** Set consumerkey.
		@param consumerkey consumerkey	  */
	public void setconsumerkey (String consumerkey)
	{
		set_ValueNoCheck (COLUMNNAME_consumerkey, consumerkey);
	}

	/** Get consumerkey.
		@return consumerkey	  */
	public String getconsumerkey () 
	{
		return (String)get_Value(COLUMNNAME_consumerkey);
	}

	/** Set consumersecret.
		@param consumersecret consumersecret	  */
	public void setconsumersecret (String consumersecret)
	{
		set_ValueNoCheck (COLUMNNAME_consumersecret, consumersecret);
	}

	/** Get consumersecret.
		@return consumersecret	  */
	public String getconsumersecret () 
	{
		return (String)get_Value(COLUMNNAME_consumersecret);
	}

	/** Set Comment/Help.
		@param Help 
		Comment or Hint
	  */
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp () 
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	public org.compiere.model.I_M_PriceList getIntl_Excl_PriceList() throws RuntimeException
    {
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_Name)
			.getPO(getIntl_Excl_PriceList_ID(), get_TrxName());	}

	/** Set Intl_Excl_PriceList_ID.
		@param Intl_Excl_PriceList_ID Intl_Excl_PriceList_ID	  */
	public void setIntl_Excl_PriceList_ID (int Intl_Excl_PriceList_ID)
	{
		if (Intl_Excl_PriceList_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Intl_Excl_PriceList_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Intl_Excl_PriceList_ID, Integer.valueOf(Intl_Excl_PriceList_ID));
	}

	/** Get Intl_Excl_PriceList_ID.
		@return Intl_Excl_PriceList_ID	  */
	public int getIntl_Excl_PriceList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Intl_Excl_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_PriceList getIntl_Incl_PriceList() throws RuntimeException
    {
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_Name)
			.getPO(getIntl_Incl_PriceList_ID(), get_TrxName());	}

	/** Set Intl_Incl_PriceList_ID.
		@param Intl_Incl_PriceList_ID Intl_Incl_PriceList_ID	  */
	public void setIntl_Incl_PriceList_ID (int Intl_Incl_PriceList_ID)
	{
		if (Intl_Incl_PriceList_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Intl_Incl_PriceList_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Intl_Incl_PriceList_ID, Integer.valueOf(Intl_Incl_PriceList_ID));
	}

	/** Get Intl_Incl_PriceList_ID.
		@return Intl_Incl_PriceList_ID	  */
	public int getIntl_Incl_PriceList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Intl_Incl_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_PriceList getLocal_Excl_PriceList() throws RuntimeException
    {
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_Name)
			.getPO(getLocal_Excl_PriceList_ID(), get_TrxName());	}

	/** Set Local_Excl_PriceList_ID.
		@param Local_Excl_PriceList_ID Local_Excl_PriceList_ID	  */
	public void setLocal_Excl_PriceList_ID (int Local_Excl_PriceList_ID)
	{
		if (Local_Excl_PriceList_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Local_Excl_PriceList_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Local_Excl_PriceList_ID, Integer.valueOf(Local_Excl_PriceList_ID));
	}

	/** Get Local_Excl_PriceList_ID.
		@return Local_Excl_PriceList_ID	  */
	public int getLocal_Excl_PriceList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Local_Excl_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_PriceList getLocal_Incl_PriceList() throws RuntimeException
    {
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_Name)
			.getPO(getLocal_Incl_PriceList_ID(), get_TrxName());	}

	/** Set Local_Incl_PriceList_ID.
		@param Local_Incl_PriceList_ID Local_Incl_PriceList_ID	  */
	public void setLocal_Incl_PriceList_ID (int Local_Incl_PriceList_ID)
	{
		if (Local_Incl_PriceList_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Local_Incl_PriceList_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Local_Incl_PriceList_ID, Integer.valueOf(Local_Incl_PriceList_ID));
	}

	/** Get Local_Incl_PriceList_ID.
		@return Local_Incl_PriceList_ID	  */
	public int getLocal_Incl_PriceList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Local_Incl_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1) 
			set_Value (COLUMNNAME_M_Product_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Warehouse getM_Warehouse() throws RuntimeException
    {
		return (org.compiere.model.I_M_Warehouse)MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_Name)
			.getPO(getM_Warehouse_ID(), get_TrxName());	}

	/** Set Warehouse.
		@param M_Warehouse_ID 
		Storage Warehouse and Service Point
	  */
	public void setM_Warehouse_ID (int M_Warehouse_ID)
	{
		if (M_Warehouse_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_Warehouse_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
	}

	/** Get Warehouse.
		@return Storage Warehouse and Service Point
	  */
	public int getM_Warehouse_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Warehouse_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Tax getStandard_Tax() throws RuntimeException
    {
		return (org.compiere.model.I_C_Tax)MTable.get(getCtx(), org.compiere.model.I_C_Tax.Table_Name)
			.getPO(getStandard_Tax_ID(), get_TrxName());	}

	/** Set Standard_Tax_ID.
		@param Standard_Tax_ID Standard_Tax_ID	  */
	public void setStandard_Tax_ID (int Standard_Tax_ID)
	{
		if (Standard_Tax_ID < 1) 
			set_Value (COLUMNNAME_Standard_Tax_ID, null);
		else 
			set_Value (COLUMNNAME_Standard_Tax_ID, Integer.valueOf(Standard_Tax_ID));
	}

	/** Get Standard_Tax_ID.
		@return Standard_Tax_ID	  */
	public int getStandard_Tax_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Standard_Tax_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set syncfrom.
		@param syncfrom syncfrom	  */
	public void setsyncfrom (String syncfrom)
	{
		set_Value (COLUMNNAME_syncfrom, syncfrom);
	}

	/** Get syncfrom.
		@return syncfrom	  */
	public String getsyncfrom () 
	{
		return (String)get_Value(COLUMNNAME_syncfrom);
	}

	/** Set URL.
		@param URL 
		Full URL address - e.g. http://www.idempiere.org
	  */
	public void setURL (String URL)
	{
		set_ValueNoCheck (COLUMNNAME_URL, URL);
	}

	/** Get URL.
		@return Full URL address - e.g. http://www.idempiere.org
	  */
	public String getURL () 
	{
		return (String)get_Value(COLUMNNAME_URL);
	}

	public org.compiere.model.I_C_Tax getZero_Tax() throws RuntimeException
    {
		return (org.compiere.model.I_C_Tax)MTable.get(getCtx(), org.compiere.model.I_C_Tax.Table_Name)
			.getPO(getZero_Tax_ID(), get_TrxName());	}

	/** Set Zero_Tax_ID.
		@param Zero_Tax_ID Zero_Tax_ID	  */
	public void setZero_Tax_ID (int Zero_Tax_ID)
	{
		if (Zero_Tax_ID < 1) 
			set_Value (COLUMNNAME_Zero_Tax_ID, null);
		else 
			set_Value (COLUMNNAME_Zero_Tax_ID, Integer.valueOf(Zero_Tax_ID));
	}

	/** Get Zero_Tax_ID.
		@return Zero_Tax_ID	  */
	public int getZero_Tax_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Zero_Tax_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Shopify Default Settings.
		@param zz_shopify_ID Shopify Default Settings	  */
	public void setzz_shopify_ID (int zz_shopify_ID)
	{
		if (zz_shopify_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_zz_shopify_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_zz_shopify_ID, Integer.valueOf(zz_shopify_ID));
	}

	/** Get Shopify Default Settings.
		@return Shopify Default Settings	  */
	public int getzz_shopify_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_zz_shopify_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set zz_shopify_UU.
		@param zz_shopify_UU zz_shopify_UU	  */
	public void setzz_shopify_UU (String zz_shopify_UU)
	{
		set_ValueNoCheck (COLUMNNAME_zz_shopify_UU, zz_shopify_UU);
	}

	/** Get zz_shopify_UU.
		@return zz_shopify_UU	  */
	public String getzz_shopify_UU () 
	{
		return (String)get_Value(COLUMNNAME_zz_shopify_UU);
	}
}