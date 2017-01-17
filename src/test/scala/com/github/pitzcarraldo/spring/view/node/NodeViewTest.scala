package com.github.pitzcarraldo.spring.view.node

import java.io.PrintWriter
import java.net.URL
import java.util
import javax.servlet.ServletContext
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.google.common.collect.{ImmutableMap, Maps}
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers._
import org.mockito.Mock
import org.mockito.Mockito._
import org.mockito.runners.MockitoJUnitRunner

/**
  * @author Minkyu Cho (pitzcarraldo@gmail.com)
  */
@RunWith(classOf[MockitoJUnitRunner])
class NodeViewTest {
  @Mock
  private val request: HttpServletRequest = null

  @Mock
  private val response: HttpServletResponse = null

  @Mock
  private val servletContext: ServletContext = null

  @Mock
  private val writer: PrintWriter = null

  def getPath(filename: String): String = getClass.getClassLoader.getResource(filename).getPath

  @Test
  def shouldWriteResponseWithoutCache(): Unit = {
    // given
    val resolver = new NodeViewResolver
    resolver.setPrefix("/")
    resolver.setSuffix(".js")
    resolver.setViewClass(classOf[NodeView])
    resolver.setContentType("text/html;charset=UTF-8")
    resolver.setCache(false)
    resolver.afterPropertiesSet()
    val model: util.Map[String, AnyRef] = Maps.newHashMap(ImmutableMap.of("type", "sync"))
    val renderer = new NodeViewRenderer
    val view = resolver.buildView("view").asInstanceOf[NodeView]
    val url = new URL("file://" + getPath("syncRenderWithResponse.js"))
    view.setRenderer(renderer)
    when(request.getServletContext).thenReturn(servletContext)
    when(response.getWriter).thenReturn(writer)
    when(servletContext.getResource(anyString)).thenReturn(url)

    // when
    val actual = view.renderMergedTemplateModel(model, request, response)

    //then
    verify(response).setHeader(anyString, anyString)
    verify(writer).append(anyString)
    verify(writer).flush()
  }

  @Test
  def shouldWriteResponseWithCache(): Unit = {
    // given
    val resolver = new NodeViewResolver
    resolver.setPrefix("/")
    resolver.setSuffix(".js")
    resolver.setViewClass(classOf[NodeView])
    resolver.setContentType("text/html;charset=UTF-8")
    resolver.setCache(true)
    resolver.setCacheLimit(2048)
    resolver.setCacheExpireTime(10)
    resolver.afterPropertiesSet()
    val model: util.Map[String, AnyRef] = Maps.newHashMap(ImmutableMap.of("type", "sync"))
    val renderer = new NodeViewRenderer
    val view = resolver.buildView("view").asInstanceOf[NodeView]
    val url = new URL("file://" + getPath("syncRenderWithResponse.js"))
    view.setRenderer(renderer)
    when(request.getServletContext).thenReturn(servletContext)
    when(response.getWriter).thenReturn(writer)
    when(servletContext.getResource(anyString)).thenReturn(url)

    // when
    view.renderMergedTemplateModel(model, request, response)

    //then
    verify(response).setHeader(anyString, anyString)
    verify(writer).append(anyString)
    verify(writer).flush()
  }
}
