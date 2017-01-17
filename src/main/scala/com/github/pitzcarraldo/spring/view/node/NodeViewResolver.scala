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

import org.springframework.beans.factory.InitializingBean
import org.springframework.web.servlet.ViewResolver
import org.springframework.web.servlet.view.{AbstractTemplateViewResolver, AbstractUrlBasedView}

import scala.beans.BeanProperty

/**
  * @author Minkyu Cho (pitzcarraldo@gmail.com)
  */
class NodeViewResolver extends AbstractTemplateViewResolver with ViewResolver with InitializingBean {
  @BeanProperty
  var cacheExpireTime: Int = 5
  var renderer: NodeViewRenderer = _

  override def buildView(viewName: String): AbstractUrlBasedView = {
    val view: NodeView = super.buildView(viewName).asInstanceOf[NodeView]
    view.setViewPath(getPrefix + viewName + getSuffix)
    view.setContentType(getContentType)
    view.setRenderer(renderer)
    view
  }

  override def afterPropertiesSet(): Unit = {
    if (isCache)
      renderer = new CacheableNodeViewRenderer(getCacheLimit, cacheExpireTime)
    else
      renderer = new NodeViewRenderer
  }

  override protected def requiredViewClass: Class[_] = classOf[NodeView]
}
