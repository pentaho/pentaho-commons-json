/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2017 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */
package org.json;

import junit.framework.Assert;

import org.junit.Test;

public class XMLTest {

  @Test
  public void testReverseConversion() throws JSONException {

    final String NOT_NULL_VALUE_PARAMETER = "not_null_value_parameter";
    final String NULL_VALUE_PARAMETER = "null_value_parameter";
    final JSONObject json = new JSONObject( "{"                         // {
      + "\"" + NULL_VALUE_PARAMETER + "\":null,"                //  null_value_parameter : null,
      + "\"" + NOT_NULL_VALUE_PARAMETER + "\":\"null\","         //  not_null_value_parameter : "null",
      + "\"inherited\":{\"" + NULL_VALUE_PARAMETER + "\":null ," //  inherited: {
      + "\"" + NOT_NULL_VALUE_PARAMETER + "\":\"null\" }"        //   null_value_parameter : null,
      + "}" );                                                      //   not_null_value_parameter : "null" } }

    String strJsonOut = XML.toString( json );
    JSONObject jsonResult = XML.toJSONObject( strJsonOut, true, false );
    Assert.assertEquals( jsonResult.get( NULL_VALUE_PARAMETER ), null );
    Assert.assertEquals( jsonResult.get( NOT_NULL_VALUE_PARAMETER ), "null" );
    JSONObject inheritedJson = jsonResult.getJSONObject( "inherited" );
    if ( inheritedJson != null ) {
      Assert.assertEquals( jsonResult.get( NULL_VALUE_PARAMETER ), null );
      Assert.assertEquals( jsonResult.get( NOT_NULL_VALUE_PARAMETER ), "null" );
    } else {
      Assert.fail( "empty inherited object" );
    }
  }

  @Test
  public void testStringReverseConversion() throws JSONException {

    final String STRING_PARAMETER = "string_parameter";
    final JSONObject json = new JSONObject( "{\"" + STRING_PARAMETER + "\":\"str\"}" );
    String strJsonOut = XML.toString( json );
    JSONObject jsonResult = XML.toJSONObject( strJsonOut );
    Assert.assertEquals( jsonResult.get( STRING_PARAMETER ), "str" );
  }

  @Test
  public void testNullReverseConversion() throws JSONException {

    final String STRING_PARAMETER = "string_null_parameter";
    final JSONObject json = new JSONObject( "{\"" + STRING_PARAMETER + "\":null}" );

    String strJsonOut = XML.toString( json );
    JSONObject jsonResult = XML.toJSONObject( strJsonOut );
    Assert.assertEquals( jsonResult.get( STRING_PARAMETER ), null );
  }

  @Test
  public void testValueWithSpace() throws JSONException {
    final String json = "<parameter0 jsonType=\"object\">\n"
      + "        <const jsonType=\"string\">APAC </const>\n"
      + "        <var jsonType=\"string\"/>\n"
      + "        <name jsonType=\"string\">test_prompt</name>\n"
      + "      </parameter0>";
    JSONObject jsonResult = XML.toJSONObject( json, true, false );
    Assert.assertEquals( jsonResult.getJSONObject( "parameter0" ).get( "const" ), "APAC " );
    jsonResult = XML.toJSONObject( json, true );
    Assert.assertEquals( jsonResult.getJSONObject( "parameter0" ).get( "const" ), "APAC" );
  }
}
