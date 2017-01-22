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

import java.io.File
import java.util
import java.util.concurrent.TimeUnit

import com.eclipsesource.v8.utils.V8ObjectUtils
import com.eclipsesource.v8.{V8Value, _}
import com.google.common.cache.{CacheBuilder, CacheLoader}
import com.google.common.collect.ImmutableMap

/**
  * @author Minkyu Cho (pitzcarraldo@gmail.com)
  */
class NodeViewRenderer {
  def render(template: NodeViewTemplate): util.Map[String, AnyRef] = {
    val nodeJS = NodeJS.createNodeJS
    val runtime = nodeJS.getRuntime
    val render: V8Function = nodeJS.require(new File(template.path)).asInstanceOf[V8Function]
    val v8Model = V8ObjectUtils.toV8Object(runtime, template.model)
    var asyncResponse: util.Map[String, AnyRef] = null
    val callback = new V8Function(render.getRuntime, new JavaCallback() {
      def invoke(receiver: V8Object, parameters: V8Array): AnyRef = {
        asyncResponse = toResponse(runtime, parameters.get(0))
        receiver.release()
        parameters.release()
        null
      }
    })
    val args = new V8Array(runtime)
    args.push(v8Model)
    args.push(callback)
    val syncResponse = toResponse(runtime, render.call(null, args))
    while (nodeJS.isRunning) {
      nodeJS.handleMessage
    }
    v8Model.release()
    callback.release()
    args.release()
    render.release()
    nodeJS.release()
    if (asyncResponse != null) asyncResponse else syncResponse
  }

  private def toResponse(runtime: V8, value: AnyRef): util.Map[String, AnyRef] = value match {
    case value: String => ImmutableMap.of("body", value)
    case value: V8Object =>
      val builder = new ImmutableMap.Builder[String, AnyRef]()
      if (!value.isUndefined) {
        if (value.contains("headers")) {
          val headers = value.getObject("headers")
          println(V8ObjectUtils.getValue(value, "headers"))
          builder.put("headers", V8ObjectUtils.toMap(headers))
          headers.release()
        }
        if (value.contains("body")) {
          value.get("body") match {
            case body: V8Value =>
              builder.put("body", stringify(runtime, body))
              body.release()
            case body => builder.put("body", body.toString)
          }
        }
        if (!value.contains("header") && !value.contains("body")) {
          builder.put("headers", ImmutableMap.of("Content-Type", "application/json"))
          builder.put("body", stringify(runtime, value))
        }
      }
      value.release()
      builder.build()
    case _ => ImmutableMap.of("body", "")
  }

  private def stringify(runtime: V8, `object`: V8Value): String = {
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

/**
  * @author Minkyu Cho (pitzcarraldo@gmail.com)
  */
class CacheableNodeViewRenderer(limit: Int, expireTime: Int) extends NodeViewRenderer {
  private val cache = CacheBuilder.newBuilder()
    .maximumSize(limit)
    .expireAfterAccess(expireTime, TimeUnit.MINUTES)
    .build(new CacheLoader[NodeViewTemplate, util.Map[String, AnyRef]]() {
      override def load(template: NodeViewTemplate): util.Map[String, AnyRef] = CacheableNodeViewRenderer.super.render(template)
    })

  override def render(template: NodeViewTemplate): util.Map[String, AnyRef] = {
    cache.getUnchecked(template)
  }
}
