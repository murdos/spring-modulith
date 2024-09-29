/*
 * Copyright 2020-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.modulith.core;

import org.springframework.modulith.NamedInterface;

/**
 * Strategy interface to customize the detection of {@link NamedInterfaces} of an {@link org.springframework.modulith.core.ApplicationModule}.
 *
 * @author Aurelien Mino
 */
public interface NamedInterfacesDetectionStrategy {

	/**
	 * Given the {@link JavaPackage} that represents the base package of a {@link org.springframework.modulith.core.ApplicationModule}, returns the {@link NamedInterfaces}.
	 *
	 * @param moduleBasePackage will never be {@literal null}.
	 * @return must not be {@literal null}.
	 */
	NamedInterfaces getModuleNamedInterfaces(JavaPackage moduleBasePackage);

	/**
	 * A {@link NamedInterfacesDetectionStrategy} that considers packages and classes explicitly annotated with
	 * {@link NamedInterface}.
	 *
	 * @return will never be {@literal null}.
	 */
	static NamedInterfacesDetectionStrategy explicitlyAnnotated() {
		return NamedInterfaces::discoverNamedInterfaces;
	}
}
