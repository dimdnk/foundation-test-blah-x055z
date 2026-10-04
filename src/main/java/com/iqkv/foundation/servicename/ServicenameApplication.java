/*
 * Copyright 2026 iQKV Foundation Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iqkv.foundation.servicename;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point for the Servicename service.
 *
 * <p>When scaffolding a new service from this template, replace:
 * <ul>
 *   <li>{@code servicename} → your service slug (e.g. {@code reporting})</li>
 *   <li>{@code Servicename} → CamelCase name (e.g. {@code Reporting})</li>
 *   <li>Package {@code com.iqkv.foundation.servicename} → {@code com.iqkv.foundation.reportingservice}</li>
 * </ul>
 *
 * <p>{@link ConfigurationPropertiesScan} auto-discovers all {@code @ConfigurationProperties}
 * records in this package tree — no manual {@code @EnableConfigurationProperties} needed.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ServicenameApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    SpringApplication.run(ServicenameApplication.class, args);
  }
}
