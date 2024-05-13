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
package za.co.ntier.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for zz_shopify
 *  @author iDempiere (generated) 
 *  @version Release 8.1
 */
@SuppressWarnings("all")
public interface I_zz_shopify 
{

    /** TableName=zz_shopify */
    public static final String Table_Name = "zz_shopify";

    /** AD_Table_ID=1000002 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 7 - System - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(7);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name Address1 */
    public static final String COLUMNNAME_Address1 = "Address1";

	/** Set Address 1.
	  * Address line 1 for this location
	  */
	public void setAddress1 (String Address1);

	/** Get Address 1.
	  * Address line 1 for this location
	  */
	public String getAddress1();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner .
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner .
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException;

    /** Column name C_Charge_ID */
    public static final String COLUMNNAME_C_Charge_ID = "C_Charge_ID";

	/** Set Charge.
	  * Additional document charges
	  */
	public void setC_Charge_ID (int C_Charge_ID);

	/** Get Charge.
	  * Additional document charges
	  */
	public int getC_Charge_ID();

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException;

    /** Column name C_Country_ID */
    public static final String COLUMNNAME_C_Country_ID = "C_Country_ID";

	/** Set Country.
	  * Country 
	  */
	public void setC_Country_ID (int C_Country_ID);

	/** Get Country.
	  * Country 
	  */
	public int getC_Country_ID();

	public org.compiere.model.I_C_Country getC_Country() throws RuntimeException;

    /** Column name City */
    public static final String COLUMNNAME_City = "City";

	/** Set City.
	  * Identifies a City
	  */
	public void setCity (String City);

	/** Get City.
	  * Identifies a City
	  */
	public String getCity();

    /** Column name consumerkey */
    public static final String COLUMNNAME_consumerkey = "consumerkey";

	/** Set consumerkey	  */
	public void setconsumerkey (String consumerkey);

	/** Get consumerkey	  */
	public String getconsumerkey();

    /** Column name consumersecret */
    public static final String COLUMNNAME_consumersecret = "consumersecret";

	/** Set consumersecret	  */
	public void setconsumersecret (String consumersecret);

	/** Get consumersecret	  */
	public String getconsumersecret();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name Help */
    public static final String COLUMNNAME_Help = "Help";

	/** Set Comment/Help.
	  * Comment or Hint
	  */
	public void setHelp (String Help);

	/** Get Comment/Help.
	  * Comment or Hint
	  */
	public String getHelp();

    /** Column name Intl_Excl_PriceList_ID */
    public static final String COLUMNNAME_Intl_Excl_PriceList_ID = "Intl_Excl_PriceList_ID";

	/** Set Intl_Excl_PriceList_ID	  */
	public void setIntl_Excl_PriceList_ID (int Intl_Excl_PriceList_ID);

	/** Get Intl_Excl_PriceList_ID	  */
	public int getIntl_Excl_PriceList_ID();

	public org.compiere.model.I_M_PriceList getIntl_Excl_PriceList() throws RuntimeException;

    /** Column name Intl_Incl_PriceList_ID */
    public static final String COLUMNNAME_Intl_Incl_PriceList_ID = "Intl_Incl_PriceList_ID";

	/** Set Intl_Incl_PriceList_ID	  */
	public void setIntl_Incl_PriceList_ID (int Intl_Incl_PriceList_ID);

	/** Get Intl_Incl_PriceList_ID	  */
	public int getIntl_Incl_PriceList_ID();

	public org.compiere.model.I_M_PriceList getIntl_Incl_PriceList() throws RuntimeException;

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name Local_Excl_PriceList_ID */
    public static final String COLUMNNAME_Local_Excl_PriceList_ID = "Local_Excl_PriceList_ID";

	/** Set Local_Excl_PriceList_ID	  */
	public void setLocal_Excl_PriceList_ID (int Local_Excl_PriceList_ID);

	/** Get Local_Excl_PriceList_ID	  */
	public int getLocal_Excl_PriceList_ID();

	public org.compiere.model.I_M_PriceList getLocal_Excl_PriceList() throws RuntimeException;

    /** Column name Local_Incl_PriceList_ID */
    public static final String COLUMNNAME_Local_Incl_PriceList_ID = "Local_Incl_PriceList_ID";

	/** Set Local_Incl_PriceList_ID	  */
	public void setLocal_Incl_PriceList_ID (int Local_Incl_PriceList_ID);

	/** Get Local_Incl_PriceList_ID	  */
	public int getLocal_Incl_PriceList_ID();

	public org.compiere.model.I_M_PriceList getLocal_Incl_PriceList() throws RuntimeException;

    /** Column name M_Product_ID */
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";

	/** Set Product.
	  * Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID);

	/** Get Product.
	  * Product, Service, Item
	  */
	public int getM_Product_ID();

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException;

    /** Column name M_Warehouse_ID */
    public static final String COLUMNNAME_M_Warehouse_ID = "M_Warehouse_ID";

	/** Set Warehouse.
	  * Storage Warehouse and Service Point
	  */
	public void setM_Warehouse_ID (int M_Warehouse_ID);

	/** Get Warehouse.
	  * Storage Warehouse and Service Point
	  */
	public int getM_Warehouse_ID();

	public org.compiere.model.I_M_Warehouse getM_Warehouse() throws RuntimeException;

    /** Column name Standard_Tax_ID */
    public static final String COLUMNNAME_Standard_Tax_ID = "Standard_Tax_ID";

	/** Set Standard_Tax_ID	  */
	public void setStandard_Tax_ID (int Standard_Tax_ID);

	/** Get Standard_Tax_ID	  */
	public int getStandard_Tax_ID();

	public org.compiere.model.I_C_Tax getStandard_Tax() throws RuntimeException;

    /** Column name syncfrom */
    public static final String COLUMNNAME_syncfrom = "syncfrom";

	/** Set syncfrom	  */
	public void setsyncfrom (String syncfrom);

	/** Get syncfrom	  */
	public String getsyncfrom();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name URL */
    public static final String COLUMNNAME_URL = "URL";

	/** Set URL.
	  * Full URL address - e.g. http://www.idempiere.org
	  */
	public void setURL (String URL);

	/** Get URL.
	  * Full URL address - e.g. http://www.idempiere.org
	  */
	public String getURL();

    /** Column name Zero_Tax_ID */
    public static final String COLUMNNAME_Zero_Tax_ID = "Zero_Tax_ID";

	/** Set Zero_Tax_ID	  */
	public void setZero_Tax_ID (int Zero_Tax_ID);

	/** Get Zero_Tax_ID	  */
	public int getZero_Tax_ID();

	public org.compiere.model.I_C_Tax getZero_Tax() throws RuntimeException;

    /** Column name zz_shopify_ID */
    public static final String COLUMNNAME_zz_shopify_ID = "zz_shopify_ID";

	/** Set Shopify Default Settings	  */
	public void setzz_shopify_ID (int zz_shopify_ID);

	/** Get Shopify Default Settings	  */
	public int getzz_shopify_ID();

    /** Column name zz_shopify_UU */
    public static final String COLUMNNAME_zz_shopify_UU = "zz_shopify_UU";

	/** Set zz_shopify_UU	  */
	public void setzz_shopify_UU (String zz_shopify_UU);

	/** Get zz_shopify_UU	  */
	public String getzz_shopify_UU();
}
