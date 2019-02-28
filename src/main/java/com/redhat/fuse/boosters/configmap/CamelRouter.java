/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package com.redhat.fuse.boosters.configmap;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

/**
 * A simple Camel REST DSL route that implements the greetings service.
 * 
 */
@Component
public class CamelRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // @formatter:off
        restConfiguration()
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Greeting REST API")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiProperty("base.path", "camel/")
                .apiProperty("api.path", "/")
                .apiProperty("host", "")
                .apiContextRouteId("doc-api")
            .component("servlet")
            .bindingMode(RestBindingMode.json);
        
        rest("/greetings-config").description("Greeting to configured name")
            .get("/").outType(Greetings.class)
                .route().routeId("greeting-config-api")
                .setHeader("name", simple("${properties:booster.nameToGreet}"))
                .to("direct:greetingsImpl");

        from("direct:greetingsImpl").description("Greetings REST service implementation route")
            .streamCaching()
            .to("bean:greetingsService?method=getGreetings");

        rest("/greetings-name").description("Greeting to {name}")
                .get("/{name}").outType(Greetings.class)
                .route().routeId("greeting-name-api")
                .to("direct:greetingsImpl");
        // @formatter:on
    }

}