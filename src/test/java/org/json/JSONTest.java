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

import junit.framework.Assert;

import org.json.XML;
import org.junit.Test;

public class JSONTest {

  @Test
  public void testSingleArray() throws Exception {
      String data = null;
      JSONObject obj = null;
      String xml = null;
      JSONObject obj2 = null;
      
      data = "{\"cols\":[{\"id\":\"BC_OFFICES_TERRITORY\"}]}";
      obj = new JSONObject(data);
      xml = XML.toString(obj, "widget", true);
      obj2 = XML.toJSONObject(xml, true);
      Assert.assertEquals("{\"widget\":{\"cols\":[{\"id\":\"BC_OFFICES_TERRITORY\"}]}}", obj2.toString());
      
      data = "{\"cols\":[\"id\"]}";
      obj = new JSONObject(data);
      xml = XML.toString(obj, "widget", true);
      obj2 = XML.toJSONObject(xml, true);
      Assert.assertEquals("{\"widget\":{\"cols\":[\"id\"]}}", obj2.toString());
  }
  
  @Test
  public void testXML() {
    try {
      String json = "{\"path\":\"test\"}";
      JSONObject obj = new JSONObject(json);
      Assert.assertNotNull(obj);
      String xml = XML.toString(obj, "widget", true);
      Assert.assertNotNull(xml);
      JSONObject results = XML.toJSONObject(xml, true);
      JSONObject jsonObj = (JSONObject)results.get("widget");
      String jsonResults = jsonObj.toString();
      Assert.assertEquals(json, jsonResults);
      
      json = "{\"path\":\"\"}";
      obj = new JSONObject(json);
      Assert.assertNotNull(obj);
      xml = XML.toString(obj, "widget", true);
      Assert.assertNotNull(xml);
      System.out.println(xml);
      results = XML.toJSONObject(xml, true);
      jsonObj = (JSONObject)results.get("widget");
      jsonResults = jsonObj.toString();
      Assert.assertEquals(json, jsonResults);
      
      /* We need better support for arrays
      json = "{\"path\":[]}";
      obj = new JSONObject(json);
      Assert.assertNotNull(obj);
      xml = XML.toString(obj, "widget");
      Assert.assertNotNull(xml);
      results = XML.toJSONObject(xml);
      jsonObj = (JSONObject)results.get("widget");
      jsonResults = jsonObj.toString();
      Assert.assertEquals(json, jsonResults);
      
      */
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
}
