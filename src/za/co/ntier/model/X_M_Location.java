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

/** Generated Model for M_Location
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="M_Location")
public class X_M_Location extends PO implements I_M_Location, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240912L;

    /** Standard Constructor */
    public X_M_Location (Properties ctx, int M_Location_ID, String trxName)
    {
      super (ctx, M_Location_ID, trxName);
      /** if (M_Location_ID == 0)
        {
			setIsDefault (false);
// N
			setM_Location_ID (0);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_M_Location (Properties ctx, int M_Location_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, M_Location_ID, trxName, virtualColumns);
      /** if (M_Location_ID == 0)
        {
			setIsDefault (false);
// N
			setM_Location_ID (0);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_M_Location (Properties ctx, String M_Location_UU, String trxName)
    {
      super (ctx, M_Location_UU, trxName);
      /** if (M_Location_UU == null)
        {
			setIsDefault (false);
// N
			setM_Location_ID (0);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_M_Location (Properties ctx, String M_Location_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, M_Location_UU, trxName, virtualColumns);
      /** if (M_Location_UU == null)
        {
			setIsDefault (false);
// N
			setM_Location_ID (0);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_M_Location (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_M_Location[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set Address 1.
		@param Address1 Address line 1 for this location
	*/
	public void setAddress1 (String Address1)
	{
		set_ValueNoCheck (COLUMNNAME_Address1, Address1);
	}

	/** Get Address 1.
		@return Address line 1 for this location
	  */
	public String getAddress1()
	{
		return (String)get_Value(COLUMNNAME_Address1);
	}

	/** Set City.
		@param City Identifies a City
	*/
	public void setCity (String City)
	{
		set_Value (COLUMNNAME_City, City);
	}

	/** Get City.
		@return Identifies a City
	  */
	public String getCity()
	{
		return (String)get_Value(COLUMNNAME_City);
	}

	/** Set Country.
		@param CountryName Country Name
	*/
	public void setCountryName (String CountryName)
	{
		set_Value (COLUMNNAME_CountryName, CountryName);
	}

	/** Get Country.
		@return Country Name
	  */
	public String getCountryName()
	{
		return (String)get_Value(COLUMNNAME_CountryName);
	}

	/** Set Default.
		@param IsDefault Default value
	*/
	public void setIsDefault (boolean IsDefault)
	{
		set_Value (COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
	}

	/** Get Default.
		@return Default value
	  */
	public boolean isDefault()
	{
		Object oo = get_Value(COLUMNNAME_IsDefault);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Location.
		@param M_Location_ID Location
	*/
	public void setM_Location_ID (int M_Location_ID)
	{
		if (M_Location_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_Location_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_Location_ID, Integer.valueOf(M_Location_ID));
	}

	/** Get Location.
		@return Location	  */
	public int getM_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set M_Location_UU.
		@param M_Location_UU M_Location_UU
	*/
	public void setM_Location_UU (String M_Location_UU)
	{
		set_ValueNoCheck (COLUMNNAME_M_Location_UU, M_Location_UU);
	}

	/** Get M_Location_UU.
		@return M_Location_UU	  */
	public String getM_Location_UU()
	{
		return (String)get_Value(COLUMNNAME_M_Location_UU);
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

	/** Set Phone.
		@param Phone Identifies a telephone number
	*/
	public void setPhone (String Phone)
	{
		set_ValueNoCheck (COLUMNNAME_Phone, Phone);
	}

	/** Get Phone.
		@return Identifies a telephone number
	  */
	public String getPhone()
	{
		return (String)get_Value(COLUMNNAME_Phone);
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