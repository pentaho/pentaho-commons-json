/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

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
