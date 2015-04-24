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
		
		String strJsonOut = XML.toString(json);
		JSONObject jsonResult = XML.toJSONObject(strJsonOut);
		assertEquals(jsonResult.get(NULL_VALUE_PARAMETER), null);
		assertEquals(jsonResult.get(NOT_NULL_VALUE_PARAMETER), "null");
		JSONObject inheritedJson = jsonResult.getJSONObject("inherited");
		if(inheritedJson!=null) {
			assertEquals(jsonResult.get(NULL_VALUE_PARAMETER), null);
			assertEquals(jsonResult.get(NOT_NULL_VALUE_PARAMETER), "null");
		} else {
			fail("empty inherited object");
		}
		
	}
	
	@Test
	public void testStringReverseConversion() throws JSONException {
		
		final String STRING_PARAMETER = "string_parameter";
		final JSONObject json = new JSONObject("{\"" + STRING_PARAMETER + "\":\"str\"}");

		String strJsonOut = XML.toString(json);
		JSONObject jsonResult = XML.toJSONObject(strJsonOut);
		assertEquals(jsonResult.get(STRING_PARAMETER), "str");

		
	}
	
	@Test
	public void testNullReverseConversion() throws JSONException {
		
		final String STRING_PARAMETER = "string_null_parameter";
		final JSONObject json = new JSONObject("{\"" + STRING_PARAMETER + "\":null}");

		String strJsonOut = XML.toString(json);
		JSONObject jsonResult = XML.toJSONObject(strJsonOut);
		assertEquals(jsonResult.get(STRING_PARAMETER), null);

		
	}

}
