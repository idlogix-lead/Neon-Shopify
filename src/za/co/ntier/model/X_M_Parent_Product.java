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

/** Generated Model for M_Parent_Product
 *  @author iDempiere (generated)
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="M_Parent_Product")
public class X_M_Parent_Product extends PO implements I_M_Parent_Product, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240606L;

    /** Standard Constructor */
    public X_M_Parent_Product (Properties ctx, int M_Parent_Product_ID, String trxName)
    {
      super (ctx, M_Parent_Product_ID, trxName);
      /** if (M_Parent_Product_ID == 0)
        {
			setM_Parent_Product_ID (0);
			setM_Product_Category_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_M_Parent_Product (Properties ctx, int M_Parent_Product_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, M_Parent_Product_ID, trxName, virtualColumns);
      /** if (M_Parent_Product_ID == 0)
        {
			setM_Parent_Product_ID (0);
			setM_Product_Category_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_M_Parent_Product (Properties ctx, String M_Parent_Product_UU, String trxName)
    {
      super (ctx, M_Parent_Product_UU, trxName);
      /** if (M_Parent_Product_UU == null)
        {
			setM_Parent_Product_ID (0);
			setM_Product_Category_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_M_Parent_Product (Properties ctx, String M_Parent_Product_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, M_Parent_Product_UU, trxName, virtualColumns);
      /** if (M_Parent_Product_UU == null)
        {
			setM_Parent_Product_ID (0);
			setM_Product_Category_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_M_Parent_Product (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_M_Parent_Product[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
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

	/** Set Parent Product.
		@param M_Parent_Product_ID Parent Product
	*/
	public void setM_Parent_Product_ID (int M_Parent_Product_ID)
	{
		if (M_Parent_Product_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_Parent_Product_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_Parent_Product_ID, Integer.valueOf(M_Parent_Product_ID));
	}

	/** Get Parent Product.
		@return Parent Product	  */
	public int getM_Parent_Product_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Parent_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set M_Parent_Product_UU.
		@param M_Parent_Product_UU M_Parent_Product_UU
	*/
	public void setM_Parent_Product_UU (String M_Parent_Product_UU)
	{
		set_ValueNoCheck (COLUMNNAME_M_Parent_Product_UU, M_Parent_Product_UU);
	}

	/** Get M_Parent_Product_UU.
		@return M_Parent_Product_UU	  */
	public String getM_Parent_Product_UU()
	{
		return (String)get_Value(COLUMNNAME_M_Parent_Product_UU);
	}

	public org.compiere.model.I_M_Product_Category getM_Product_Category() throws RuntimeException
	{
		return (org.compiere.model.I_M_Product_Category)MTable.get(getCtx(), org.compiere.model.I_M_Product_Category.Table_ID)
			.getPO(getM_Product_Category_ID(), get_TrxName());
	}

	/** Set Product Category.
		@param M_Product_Category_ID Category of a Product
	*/
	public void setM_Product_Category_ID (int M_Product_Category_ID)
	{
		if (M_Product_Category_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_Product_Category_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_Product_Category_ID, Integer.valueOf(M_Product_Category_ID));
	}

	/** Get Product Category.
		@return Category of a Product
	  */
	public int getM_Product_Category_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_Category_ID);
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