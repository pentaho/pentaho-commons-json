package org.json;

import junit.framework.Assert;

import org.json.XML;
import org.junit.Test;

public class JSONTest {
  
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
