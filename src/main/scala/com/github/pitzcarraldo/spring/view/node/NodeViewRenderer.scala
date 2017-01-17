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

import com.eclipsesource.v8._
import com.google.common.cache.{CacheBuilder, CacheLoader}

/**
  * @author Minkyu Cho (pitzcarraldo@gmail.com)
  */
class NodeViewRenderer {
  def render(template: NodeViewTemplate): util.Map[String, AnyRef] = {
    val nodeJS = NodeJS.createNodeJS(null)
    val render: V8Function = nodeJS.require(new File(template.path)).asInstanceOf[V8Function]
    val runtime = render.getRuntime
    val objectUtils = new V8ObjectUtils(runtime)
    val v8Model = objectUtils.fromMap(template.model)
    var asyncResponse: util.Map[String, AnyRef] = null
    val callback = new V8Function(render.getRuntime, (receiver: V8Object, parameters: V8Array) => {
      asyncResponse = objectUtils.toResponse(parameters.get(0))
      receiver.release()
      parameters.release()
      null
    })
    val args = new V8Array(runtime)
    args.push(v8Model)
    args.push(callback)
    val syncResponse = objectUtils.toResponse(render.call(null, args))
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
