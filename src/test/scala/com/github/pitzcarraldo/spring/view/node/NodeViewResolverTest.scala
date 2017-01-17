package com.github.pitzcarraldo.spring.view.node

import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
  * @author Minkyu Cho (pitzcarraldo@gmail.com)
  */
@RunWith(classOf[JUnit4])
class NodeViewResolverTest {

  @Test
  def shouldBuildViewWithOutCache(): Unit = {
    // given
    val resolver = new NodeViewResolver
    resolver.setPrefix("/")
    resolver.setSuffix(".js")
    resolver.setViewClass(classOf[NodeView])
    resolver.setContentType("text/html;charset=UTF-8")
    resolver.setCache(false)

    // when
    resolver.afterPropertiesSet()
    val view = resolver.buildView("view").asInstanceOf[NodeView]

    // then
    assertThat(view.getViewPath).isEqualTo("/view.js")
    assertThat(view.getContentType).isEqualTo("text/html;charset=UTF-8")
    assertThat(view.getRenderer).isInstanceOf(classOf[NodeViewRenderer])
  }

  @Test
  def shouldBuildViewWithCache(): Unit = {
    // given
    val resolver = new NodeViewResolver
    resolver.setPrefix("/")
    resolver.setSuffix(".js")
    resolver.setViewClass(classOf[NodeView])
    resolver.setContentType("text/html;charset=UTF-8")
    resolver.setCache(true)
    resolver.setCacheLimit(2048)
    resolver.setCacheExpireTime(10)

    // when
    resolver.afterPropertiesSet()
    val view = resolver.buildView("view").asInstanceOf[NodeView]

    // then
    assertThat(view.getViewPath).isEqualTo("/view.js")
    assertThat(view.getContentType).isEqualTo("text/html;charset=UTF-8")
    assertThat(view.getRenderer).isInstanceOf(classOf[CacheableNodeViewRenderer])
  }
}
