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
  def shouldReturnResponseMapSynchronous(): Unit = {
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
  def shouldReturnResponseMapWithHeaderSynchronous(): Unit = {
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
  def shouldReturnArraySynchronous(): Unit = {
    // given
    val path = getPath("syncRenderWithArray.js")
    val model: util.Map[String, AnyRef] = ImmutableMap.of("type", "sync")
    val template = new NodeViewTemplate(path, model)

    // when
    val actual = sut.render(template)

    // then
    val expected = "[\"{\\\"type\\\":\\\"sync\\\"}\"]"
    assertThat(actual.get("body")).isEqualTo(expected)
  }

  @Test
  def shouldReturnNullSynchronous(): Unit = {
    // given
    val path = getPath("syncRenderWithNull.js")
    val model: util.Map[String, AnyRef] = ImmutableMap.of("type", "sync")
    val template = new NodeViewTemplate(path, model)

    // when
    val actual = sut.render(template)

    // then
    val expected = ""
    assertThat(actual.get("body")).isEqualTo(expected)
  }

  @Test
  def shouldReturnResponseMapAsynchronous(): Unit = {
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
  def shouldReturnResponseMapWithHeaderAsynchronous(): Unit = {
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

  @Test
  def shouldReturnResponseMapWithCircularObjectAsynchronous(): Unit = {
    // given
    val path = getPath("asyncRenderWithCircularObject.js")
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

  @Test
  def shouldReturnResponseMapSynchronousFromCache(): Unit = {
    // given
    val sut = new CacheableNodeViewRenderer(1024, 5)
    val path = getPath("syncRender.js")
    val model: util.Map[String, AnyRef] = ImmutableMap.of("type", "sync")
    val templateA = new NodeViewTemplate(path, model)
    val templateB = new NodeViewTemplate(path, model)

    // when
    val actualA = sut.render(templateA)
    val actualB = sut.render(templateB)

    // then
    assertThat(actualA).isEqualTo(actualB)
  }

  @Test
  def shouldReturnFalseWhenNotSameObject(): Unit = {
    // given
    val path = getPath("syncRender.js")
    val model: util.Map[String, AnyRef] = ImmutableMap.of("type", "sync")
    val template = new NodeViewTemplate(path, model)

    // when
    val actual = template.equals(null)

    // then
    assertThat(actual).isFalse
  }
}
