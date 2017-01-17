package com.github.pitzcarraldo.spring.view.node

import java.util

import com.google.common.collect.ImmutableMap
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
  * @author Minkyu Cho (pitzcarraldo@gmail.com)
  */
@RunWith(classOf[JUnit4])
class NodeViewRendererTest {
  val sut = new NodeViewRenderer

  def getPath(filename: String): String = getClass.getClassLoader.getResource(filename).getPath

  @Test
  def testSyncRender(): Unit = {
    // given
    val path = getPath("syncRender.js")
    val model: util.Map[String, AnyRef] = ImmutableMap.of("type", "sync")
    val template = new NodeViewTemplate(path, model)

    // when
    val actual = sut.render(template)

    // then
    val expected = "{\"type\":\"sync\"}"
    assertThat(actual.get("body")).isEqualTo(expected)
  }

  @Test
  def testSyncRenderWithResponse(): Unit = {
    // given
    val path = getPath("syncRenderWithResponse.js")
    val model: util.Map[String, AnyRef] = ImmutableMap.of("type", "sync")
    val template = new NodeViewTemplate(path, model)

    // when
    val actual = sut.render(template)
    val headers = actual.get("headers").asInstanceOf[util.Map[String, String]]
    val body = actual.get("body").asInstanceOf[String]

    // then
    val expectedContentType = "application/json"
    val expectedBody = "{\"type\":\"sync\"}"
    assertThat(headers.get("Content-Type")).isEqualTo(expectedContentType)
    assertThat(body).isEqualTo(expectedBody)
  }

  @Test
  def testAsyncRender(): Unit = {
    // given
    val path = getPath("asyncRender.js")
    val model: util.Map[String, AnyRef] = ImmutableMap.of("type", "async")
    val template = new NodeViewTemplate(path, model)

    // when
    val actual = sut.render(template)

    // then
    val expected = "{\"type\":\"async\"}"
    assertThat(actual.get("body")).isEqualTo(expected)
  }

  @Test
  def testAsyncRenderWithResponse(): Unit = {
    // given
    val path = getPath("asyncRenderWithResponse.js")
    val model: util.Map[String, AnyRef] = ImmutableMap.of("type", "async")
    val template = new NodeViewTemplate(path, model)

    // when
    val actual = sut.render(template)
    val headers = actual.get("headers").asInstanceOf[util.Map[String, String]]
    val body = actual.get("body").asInstanceOf[String]

    // then
    val expectedContentType = "application/json"
    val expectedBody = "{\"type\":\"async\"}"
    assertThat(headers.get("Content-Type")).isEqualTo(expectedContentType)
    assertThat(body).isEqualTo(expectedBody)
  }
}
