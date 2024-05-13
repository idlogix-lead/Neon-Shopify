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

/** Generated Model for Courier_Company
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="Courier_Company")
public class X_Courier_Company extends PO implements I_Courier_Company, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240401L;

    /** Standard Constructor */
    public X_Courier_Company (Properties ctx, int Courier_Company_ID, String trxName)
    {
      super (ctx, Courier_Company_ID, trxName);
      /** if (Courier_Company_ID == 0)
        {
			setC_BPartner_ID (0);
			setCourier_Company_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_Courier_Company (Properties ctx, int Courier_Company_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, Courier_Company_ID, trxName, virtualColumns);
      /** if (Courier_Company_ID == 0)
        {
			setC_BPartner_ID (0);
			setCourier_Company_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_Courier_Company (Properties ctx, String Courier_Company_UU, String trxName)
    {
      super (ctx, Courier_Company_UU, trxName);
      /** if (Courier_Company_UU == null)
        {
			setC_BPartner_ID (0);
			setCourier_Company_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_Courier_Company (Properties ctx, String Courier_Company_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, Courier_Company_UU, trxName, virtualColumns);
      /** if (Courier_Company_UU == null)
        {
			setC_BPartner_ID (0);
			setCourier_Company_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_Courier_Company (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org
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
      StringBuilder sb = new StringBuilder ("X_Courier_Company[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getC_BPartner_ID(), get_TrxName());
	}

	/** Set Business Partner.
		@param C_BPartner_ID Identifies a Business Partner
	*/
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner.
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Courier Company.
		@param Courier_Company_ID Courier Company
	*/
	public void setCourier_Company_ID (int Courier_Company_ID)
	{
		if (Courier_Company_ID < 1)
			set_ValueNoCheck (COLUMNNAME_Courier_Company_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_Courier_Company_ID, Integer.valueOf(Courier_Company_ID));
	}

	/** Get Courier Company.
		@return Courier Company	  */
	public int getCourier_Company_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Courier_Company_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Courier_Company_UU.
		@param Courier_Company_UU Courier_Company_UU
	*/
	public void setCourier_Company_UU (String Courier_Company_UU)
	{
		set_ValueNoCheck (COLUMNNAME_Courier_Company_UU, Courier_Company_UU);
	}

	/** Get Courier_Company_UU.
		@return Courier_Company_UU	  */
	public String getCourier_Company_UU()
	{
		return (String)get_Value(COLUMNNAME_Courier_Company_UU);
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Line.
		@param LineNo Line No
	*/
	public void setLineNo (int LineNo)
	{
		set_Value (COLUMNNAME_LineNo, Integer.valueOf(LineNo));
	}

	/** Get Line.
		@return Line No
	  */
	public int getLineNo()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LineNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set trackingmetakey.
		@param trackingmetakey trackingmetakey
	*/
	public void settrackingmetakey (String trackingmetakey)
	{
		set_Value (COLUMNNAME_trackingmetakey, trackingmetakey);
	}

	/** Get trackingmetakey.
		@return trackingmetakey	  */
	public String gettrackingmetakey()
	{
		return (String)get_Value(COLUMNNAME_trackingmetakey);
	}

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}