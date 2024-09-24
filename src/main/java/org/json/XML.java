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
/*!
 * HITACHI VANTARA PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2017 Hitachi Vantara. All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Hitachi Vantara and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Hitachi Vantara and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Hitachi Vantara is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Hitachi Vantara,
 * explicitly covering such access.
 */
package org.json;

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

import java.util.Iterator;



/**
 * This provides static methods to convert an XML text into a JSONObject,
 * and to covert a JSONObject into an XML text.
 * @author JSON.org
 * @version 3
 */
public class XML {


  /** The Character '&'. */
  public static final Character AMP   = new Character( '&' );

  /** The Character '''. */
  public static final Character APOS  = new Character( '\'' );

  /** The Character '!'. */
  public static final Character BANG  = new Character( '!' );

  /** The Character '='. */
  public static final Character EQ  = new Character( '=' );

  /** The Character '>'. */
  public static final Character GT  = new Character( '>' );

  /** The Character '<'. */
  public static final Character LT  = new Character( '<' );

  /** The Character '?'. */
  public static final Character QUEST = new Character( '?' );

  /** The Character '"'. */
  public static final Character QUOT  = new Character( '"' );

  /** The Character '/'. */
  public static final Character SLASH = new Character( '/' );

  /** the attribute name of the JSON type if included **/
  public static final String TYPE_ATTRIB = "jsonType";
  public static final String ARRAY_ATTRIB = "jsonIsArray";
  public static final String ISNULL_ATTRIB = "isNull";

  /**
   * Replace special characters with XML escapes:
   * <pre>
   * &amp; <small>(ampersand)</small> is replaced by &amp;amp;
   * &lt; <small>(less than)</small> is replaced by &amp;lt;
   * &gt; <small>(greater than)</small> is replaced by &amp;gt;
   * &quot; <small>(double quote)</small> is replaced by &amp;quot;
   * </pre>
   * @param string The string to be escaped.
   * @return The escaped string.
   */
  public static String escape( String string ) {
    StringBuffer sb = new StringBuffer();
    for ( int i = 0, len = string.length(); i < len; i++ ) {
      char c = string.charAt( i );
      switch ( c ) {
        case '&':
          sb.append( "&amp;" );
          break;
        case '<':
          sb.append( "&lt;" );
          break;
        case '>':
          sb.append( "&gt;" );
          break;
        case '"':
          sb.append( "&quot;" );
          break;
        default:
          sb.append( c );
      }
    }
    return sb.toString();
  }

  /**
   * Throw an exception if the string contains whitespace.
   * Whitespace is not allowed in tagNames and attributes.
   * @param string
   * @throws JSONException
   */
  public static void noSpace( String string ) throws JSONException {
    int i, length = string.length();
    if ( length == 0 ) {
      throw new JSONException( "Empty string." );
    }
    for ( i = 0; i < length; i += 1 ) {
      if ( Character.isWhitespace( string.charAt( i ) ) ) {
        throw new JSONException( "'" + string
          + "' contains a space character." );
      }
    }
  }

  /**
   * Scan the content following the named tag, attaching it to the context.
   * @param x     The XMLTokener containing the source string.
   * @param context The JSONObject that will include the new material.
   * @param name  The tag name.
   * @return true if the close tag is processed.
   * @throws JSONException
   */
  private static boolean parse( XMLTokener x, JSONObject context,
                 String name, boolean typed ) throws JSONException {
    char     c;
    int    i;
    String   n;
    JSONObject o = null;
    String   s;
    Object   t;

  // Test for and skip past these forms:
  //    <!-- ... -->
  //    <!   ...   >
  //    <![  ... ]]>
  //    <?   ...  ?>
  // Report errors for these forms:
  //    <>
  //    <=
  //    <<

    t = x.nextToken();

  // <!

    if ( t == BANG ) {
      c = x.next();
      if ( c == '-' ) {
        if ( x.next() == '-' ) {
          x.skipPast( "-->" );
          return false;
        }
        x.back();
      } else if ( c == '[' ) {
        t = x.nextToken();
        if ( t.equals( "CDATA" ) ) {
          if ( x.next() == '[' ) {
            s = x.nextCDATA();
            if ( s.length() > 0 ) {
              context.accumulate( "content", s );
            }
            return false;
          }
        }
        throw x.syntaxError( "Expected 'CDATA['" );
      }
      i = 1;
      do {
        t = x.nextMeta();
        if ( t == null ) {
          throw x.syntaxError( "Missing '>' after '<!'." );
        } else if ( t == LT ) {
          i += 1;
        } else if ( t == GT ) {
          i -= 1;
        }
      } while ( i > 0 );
      return false;
    } else if ( t == QUEST ) {

  // <?

      x.skipPast( "?>" );
      return false;
    } else if ( t == SLASH ) {

  // Close tag </

      t = x.nextToken();
      if ( name == null ) {
        throw x.syntaxError( "Mismatched close tag" + t );
      }
      if ( !t.equals( name ) ) {
        throw x.syntaxError( "Mismatched " + name + " and " + t );
      }
      if ( x.nextToken() != GT ) {
        throw x.syntaxError( "Misshaped close tag" );
      }
      return true;

    } else if ( t instanceof Character ) {
      throw x.syntaxError( "Misshaped tag" );

  // Open tag <

    } else {
      n = (String) t;
      t = null;
      o = new JSONObject();
      for ( ;; ) {
        if ( t == null ) {
          t = x.nextToken();
        }

  // attribute = value

        if ( t instanceof String ) {
          s = (String) t;
          t = x.nextToken();
          if ( t == EQ ) {
            t = x.nextToken();
            if ( !( t instanceof String ) ) {
              throw x.syntaxError( "Missing value" );
            }
            o.accumulate( s, t );
            t = null;
          } else {
            o.accumulate( s, "" );
          }

  // Empty tag <.../>

        } else if ( t == SLASH ) {
          if ( x.nextToken() != GT ) {
            throw x.syntaxError( "Misshaped tag" );
          }
          // before adding, clear out TYPE_ATTRIB
          String isNull = (String) o.remove( ISNULL_ATTRIB );
          if ( "true".equals( isNull ) ) {
            context.accumulate( n, JSONObject.NULL );
          } else {
            if ( typed ) {
              String type = (String) o.remove( TYPE_ATTRIB );
              String isArr = (String) o.remove( ARRAY_ATTRIB );

              if ( isArr != null && isArr.equals( "true" ) ) {
                if ( type != null && type.equals( "string" ) ) {
                  context.append( n, "" );
                } else {
                  context.append( n, o );
                }
              } else {
                if ( type != null && type.equals( "string" ) ) {
                  context.accumulate( n, "" );
                } else {
                  context.accumulate( n, o );
                }
              }
            } else {
              context.accumulate( n, o );
            }
          }
          return false;

  // Content, between <...> and </...>

        } else if ( t == GT ) {
          for ( ;; ) {
            t = x.nextContent();
            if ( t == null ) {
              if ( n != null ) {
                throw x.syntaxError( "Unclosed tag " + n );
              }
              return false;
            } else if ( t instanceof String ) {
              s = (String) t;
              if ( s.length() > 0 ) {
                o.accumulate( "content", s );
              }

  // Nested element

            } else if ( t == LT ) {
              if ( parse( x, o, n, typed ) ) {
                String isArr = null;
                if ( typed ) {
                  String type = (String) o.remove( TYPE_ATTRIB );
                  isArr = (String) o.remove( ARRAY_ATTRIB );
                }
                if ( isArr != null && isArr.equals( "true" ) ) {
                  if ( o.length() == 0 ) {
                    context.append( n, "" );
                  } else if ( o.length() == 1
                      && o.opt( "content" ) != null ) {
                    context.append( n, o.opt( "content" ) );
                  } else {
                    context.append( n, o );
                  }
                } else {
                  if ( o.length() == 0 ) {
                    context.accumulate( n, "" );
                  } else if ( o.length() == 1
                     && o.opt( "content" ) != null ) {
                    context.accumulate( n, o.opt( "content" ) );
                  } else {
                    context.accumulate( n, o );
                  }
                }
                return false;
              }
            }
          }
        } else {
          throw x.syntaxError( "Misshaped tag" );
        }
      }
    }
  }

  public static JSONObject toJSONObject( String string ) throws JSONException {
    return toJSONObject( string, false );
  }

  /**
   * Convert a well-formed (but not necessarily valid) XML string into a
   * JSONObject. Some information may be lost in this transformation
   * because JSON is a data format and XML is a document format. XML uses
   * elements, attributes, and content text, while JSON uses unordered
   * collections of name/value pairs and arrays of values. JSON does not
   * does not like to distinguish between elements and attributes.
   * Sequences of similar elements are represented as JSONArrays. Content
   * text may be placed in a "content" member. Comments, prologs, DTDs, and
   * <code>&lt;[ [ ]]></code> are ignored.
   * @param string The source string.
   * @param typed if true, expects typed info in the xml
   * @return A JSONObject containing the structured data from the XML string.
   * @throws JSONException
   */
  public static JSONObject toJSONObject( String string, boolean typed ) throws JSONException {
    JSONObject o = new JSONObject();
    XMLTokener x = new XMLTokener( string );
    while ( x.more() && x.skipPast( "<" ) ) {
      parse( x, o, null, typed );
    }
    return o;
  }

  /**
   * Convert a well-formed (but not necessarily valid) XML string into a
   * JSONObject. Some information may be lost in this transformation
   * because JSON is a data format and XML is a document format. XML uses
   * elements, attributes, and content text, while JSON uses unordered
   * collections of name/value pairs and arrays of values. JSON does not
   * does not like to distinguish between elements and attributes.
   * Sequences of similar elements are represented as JSONArrays. Content
   * text may be placed in a "content" member. Comments, prologs, DTDs, and
   * <code>&lt;[ [ ]]></code> are ignored.
   * @param string The source string.
   * @param typed if true, expects typed info in the xml
   * @param isTrim if true, trim string into xml
   * @return A JSONObject containing the structured data from the XML string.
   * @throws JSONException
   */
  public static JSONObject toJSONObject( String string, boolean typed, boolean isTrim ) throws JSONException {
    JSONObject o = new JSONObject();
    XMLTokener x = new XMLTokener( string, isTrim );
    while ( x.more() && x.skipPast( "<" ) ) {
      parse( x, o, null, typed );
    }
    return o;
  }

  /**
   * Convert a JSONObject into a well-formed, element-normal XML string.
   * @param o A JSONObject.
   * @return  A string.
   * @throws  JSONException
   */
  public static String toString( Object o ) throws JSONException {
    return toString( o, null );
  }

  public static String toString( Object o, String tagName ) throws JSONException {
    return toString( o, tagName, false, false );
  }

  public static String toString( Object o, String tagName, boolean typed ) throws JSONException {
    return toString( o, tagName, typed, false );
  }

  /**
   * Convert a JSONObject into a well-formed, element-normal XML string.
   * @param o A JSONObject.
   * @param tagName The optional name of the enclosing tag.
   * @param typed if true, adds type info to the xml
   * @return A string.
   * @throws JSONException
   */
  private static String toString( Object o, String tagName, boolean typed, boolean isArray )
      throws JSONException {
    StringBuffer b = new StringBuffer();
    int      i;
    JSONArray  ja;
    JSONObject   jo;
    String     k;
    Iterator   keys;
    int      len;
    String     s;
    Object     v;
    if ( o instanceof JSONObject ) {

  // Emit <tagName>

      if ( tagName != null ) {
        b.append( '<' );
        b.append( tagName );
        if ( typed ) {
          b.append( " " + TYPE_ATTRIB + "=\"object\"" );
          if ( isArray ) {
            b.append( " " + ARRAY_ATTRIB + "=\"" + isArray + "\"" );
          }
          b.append( ">" );
        } else {
          b.append( '>' );
        }
      }

  // Loop thru the keys.

      jo = (JSONObject) o;
      keys = jo.keys();
      while ( keys.hasNext() ) {
        k = keys.next().toString();
        v = jo.get( k );
        if ( v instanceof String ) {
          s = (String) v;
        } else {
          s = null;
        }

  // Emit content in body

        if ( k.equals( "content" ) ) {
          if ( v instanceof JSONArray ) {
            ja = (JSONArray) v;
            len = ja.length();
            for ( i = 0; i < len; i += 1 ) {
              if ( i > 0 ) {
                b.append( '\n' );
              }
              b.append( escape( ja.get( i ).toString() ) );
            }
          } else {
            b.append( escape( v.toString() ) );
          }

  // Emit an array of similar keys

        } else if ( v instanceof JSONArray ) {
          ja = (JSONArray) v;
          len = ja.length();
          for ( i = 0; i < len; i += 1 ) {
            b.append( toString( ja.get( i ), k, typed, true  ) );
          }
        } else if ( v.equals( ""  ) ) {
          b.append( '<' );
          b.append( k );
          if ( typed ) {
            b.append( " " + TYPE_ATTRIB + "=\"string\"" );
            if ( isArray ) {
              b.append( " " + ARRAY_ATTRIB + "=\"" + isArray + "\"" );
            }
            b.append( "/>" );
          } else {
            b.append( "/>" );
          }

  // Emit a new tag <k>

        } else {
          b.append( toString( v, k, typed, false ) );
        }
      }
      if ( tagName != null ) {

  // Emit the </tagname> close tag

        b.append( "</" );
        b.append( tagName );
        b.append( '>' );
      }
      return b.toString();

  // XML does not have good support for arrays. If an array appears in a place
  // where XML is lacking, synthesize an <array> element.

    } else if ( o instanceof JSONArray ) {
      ja = (JSONArray) o;
      len = ja.length();
      for ( i = 0; i < len; ++i ) {
        b.append( toString(
          ja.opt( i ), ( tagName == null ) ? "array" : tagName, typed, true ) );
      }
      return b.toString();
    } else {

      boolean valueIsNull = ( o == null || o == JSONObject.NULL );
      s = valueIsNull ? "" : escape( o.toString() );

      if ( typed ) {
        return ( tagName == null ) ? "\"" + s + "\""
          : ( valueIsNull || s.length() == 0 ) ? "<" + tagName + ( valueIsNull ? " " + ISNULL_ATTRIB + "=\"true\"" : "" ) + "/>"
          : "<" + tagName + " " + TYPE_ATTRIB + "=\"string\""
          + ( ( isArray ) ? " " + ARRAY_ATTRIB + "=\"" + isArray + "\"" : "" )
          + ">" + s + "</" + tagName + ">";
      } else {
        return ( tagName == null ) ? "\"" + s + "\""
          : ( valueIsNull || s.length() == 0 ) ? "<" + tagName + ( valueIsNull ? " " + ISNULL_ATTRIB + "=\"true\"" : "" ) + "/>"
          : "<" + tagName + ">" + s + "</" + tagName + ">";
      }
    }
  }
}
