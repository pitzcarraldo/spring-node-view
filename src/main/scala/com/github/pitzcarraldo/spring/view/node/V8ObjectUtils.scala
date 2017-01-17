/*
 * Copyright (c) 2017 Minkyu Cho
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.github.pitzcarraldo.spring.view.node

import java.util

import com.eclipsesource.v8.{V8, V8Array, V8Object, V8Value}
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableMap

/**
  * @author Minkyu Cho (pitzcarraldo@gmail.com)
  */
class V8ObjectUtils(runtime: V8) {
  private val mapper = new ObjectMapper

  object JSON {
    def parse(string: String): V8Object = {
      val json = runtime.getObject("JSON")
      val parameters = new V8Array(runtime).push(string)
      try {
        json.executeObjectFunction("parse", parameters)
      } catch {
        case _: Exception => new V8Object(runtime)
      } finally {
        parameters.release()
        json.release()
      }
    }

    def stringify(`object`: V8Value): String = {
      val json = runtime.getObject("JSON")
      val parameters = new V8Array(runtime).push(`object`)
      try {
        json.executeStringFunction("stringify", parameters)
      } catch {
        case _: Exception => `object`.toString
      } finally {
        parameters.release()
        json.release()
      }
    }
  }

  def fromMap(model: util.Map[String, AnyRef]): V8Object = {
    JSON.parse(mapper.writeValueAsString(model))
  }

  def toMap(response: V8Object): util.Map[String, String] = {
    mapper.readValue(JSON.stringify(response), classOf[util.Map[String, String]])
  }

  def toResponse(value: AnyRef): util.Map[String, AnyRef] = value match {
    case value: String => ImmutableMap.of("body", value)
    case value: V8Object =>
      val builder = new ImmutableMap.Builder[String, AnyRef]()
      if (!value.isUndefined) {
        if (value.contains("headers")) {
          val headers = value.getObject("headers")
          builder.put("headers", toMap(headers))
          headers.release()
        }
        if (value.contains("body")) {
          value.get("body") match {
            case body: V8Value =>
              builder.put("body", JSON.stringify(body))
              body.release()
            case body => builder.put("body", body.toString)
          }
        }
        if (!value.contains("header") && !value.contains("body")) {
          builder.put("headers", ImmutableMap.of("Content-Type", "application/json"))
          builder.put("body", JSON.stringify(value))
        }
      }
      value.release()
      builder.build()
    case _ => ImmutableMap.of("body", "")
  }
}