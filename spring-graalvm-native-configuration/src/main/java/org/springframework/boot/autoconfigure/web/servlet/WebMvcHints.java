/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.autoconfigure.web.servlet;

import java.util.concurrent.Callable;

import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration.BeanPostProcessorsRegistrar;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration.EmbeddedTomcat;
import org.springframework.boot.autoconfigure.web.servlet.error.DefaultErrorViewResolver;
import org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader;
import org.springframework.boot.web.server.ErrorPageRegistrarBeanPostProcessor;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.graalvm.extension.NativeImageConfiguration;
import org.springframework.graalvm.extension.NativeImageHint;
import org.springframework.graalvm.extension.ResourcesInfo;
import org.springframework.graalvm.extension.TypeInfo;
import org.springframework.graalvm.type.AccessBits;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.BeanNameViewResolver;



@NativeImageHint(trigger=WebMvcAutoConfiguration.class, typeInfos = {
		@TypeInfo(types= {
				AnnotationConfigServletWebServerApplicationContext.class,
				ConfigurableWebApplicationContext.class,
				TomcatEmbeddedWebappClassLoader.class,
				WebApplicationContext.class,
				ErrorPage.class,
				DefaultErrorViewResolver.class,
				BeanNameViewResolver.class,
				ErrorPageRegistrarBeanPostProcessor.class},
				access=AccessBits.LOAD_AND_CONSTRUCT),
		@TypeInfo(
				typeNames = "org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$EmptyHandler",
				access = AccessBits.LOAD_AND_CONSTRUCT|AccessBits.DECLARED_METHODS),
		@TypeInfo(
				types = Callable.class,
				access = AccessBits.LOAD_AND_CONSTRUCT|AccessBits.DECLARED_METHODS)
}, abortIfTypesMissing = true)
// TODO this is an interesting one as it is hinted at by both flavours of BeanPostProcessorsRegistrar (reactive and servlet)
@NativeImageHint(trigger=BeanPostProcessorsRegistrar.class,typeInfos= {
		@TypeInfo(types= {WebServerFactoryCustomizerBeanPostProcessor.class},access=AccessBits.LOAD_AND_CONSTRUCT)
})
@NativeImageHint(trigger=EmbeddedTomcat.class,resourcesInfos = {
		// Accessed from org.apache.catalina.startup.Tomcat
		@ResourcesInfo(patterns="org/apache/catalina/startup/MimeTypeMappings.properties")
})
public class WebMvcHints implements NativeImageConfiguration {
}
