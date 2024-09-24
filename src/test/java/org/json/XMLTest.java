/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
package org.json;

import static junit.framework.Assert.*;

import org.json.XML;
import org.junit.Test;

public class XMLTest {
	
	
	@Test
	public void testReverseConversion() throws JSONException {
		
		final String NOT_NULL_VALUE_PARAMETER = "not_null_value_parameter";
		final String NULL_VALUE_PARAMETER = "null_value_parameter";
		final JSONObject json = new JSONObject("{"                         // {
				+"\"" + NULL_VALUE_PARAMETER + "\":null," +                //  null_value_parameter : null,
				"\"" + NOT_NULL_VALUE_PARAMETER + "\":\"null\"," +         //  not_null_value_parameter : "null",
				"\"inherited\":{\"" + NULL_VALUE_PARAMETER + "\":null ," + //  inherited: {
				"\"" + NOT_NULL_VALUE_PARAMETER + "\":\"null\" }" +        //   null_value_parameter : null,
				"}");                                                      //   not_null_value_parameter : "null" } }
		
		String strJsonOut = XML.toString( json );
		JSONObject jsonResult = XML.toJSONObject( strJsonOut, true, false );
		assertEquals( jsonResult.get( NULL_VALUE_PARAMETER), null );
		assertEquals( jsonResult.get( NOT_NULL_VALUE_PARAMETER ), "null" );
		JSONObject inheritedJson = jsonResult.getJSONObject( "inherited" );
		if( inheritedJson!=null ) {
			assertEquals( jsonResult.get( NULL_VALUE_PARAMETER ), null );
			assertEquals( jsonResult.get( NOT_NULL_VALUE_PARAMETER ), "null" );
		} else {
			fail( "empty inherited object" );
		}
		
	}
	
	@Test
	public void testStringReverseConversion() throws JSONException {
		
		final String STRING_PARAMETER = "string_parameter";
		final JSONObject json = new JSONObject( "{\"" + STRING_PARAMETER + "\":\"str\"}" );

		String strJsonOut = XML.toString( json );
		JSONObject jsonResult = XML.toJSONObject( strJsonOut );
		assertEquals(jsonResult.get( STRING_PARAMETER), "str" );

		
	}
	
	@Test
	public void testNullReverseConversion() throws JSONException {
		
		final String STRING_PARAMETER = "string_null_parameter";
		final JSONObject json = new JSONObject( "{\"" + STRING_PARAMETER + "\":null}" );

		String strJsonOut = XML.toString( json );
		JSONObject jsonResult = XML.toJSONObject( strJsonOut );
		assertEquals( jsonResult.get( STRING_PARAMETER ), null );

		
	}

	@Test
	public void testValueWithSpace() throws JSONException {
		final String json = "<parameter0 jsonType=\"object\">\n" +
			"        <const jsonType=\"string\">APAC </const>\n" +
			"        <var jsonType=\"string\"/>\n" +
			"        <name jsonType=\"string\">test_prompt</name>\n" +
			"      </parameter0>";
		JSONObject jsonResult = XML.toJSONObject( json, true, false );
		assertEquals(jsonResult.getJSONObject( "parameter0" ).get( "const" ), "APAC " );
		jsonResult = XML.toJSONObject( json, true );
		assertEquals(jsonResult.getJSONObject( "parameter0" ).get( "const" ), "APAC" );
	}
}
