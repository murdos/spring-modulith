/*
 * Copyright 2024 the original author or authors.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Supplier;

/**
 * A factory for the {@link NamedInterfacesDetectionStrategy} to be used when scanning code for
 * {@link NamedInterfaces}s.
 *
 * @author Aurelien Mino
 */
class NamedInterfacesDetectionStrategyLookup {

	private static final String DETECTION_STRATEGY_PROPERTY = "spring.modulith.named-interfaces-detection-strategy";
	private static final Supplier<NamedInterfacesDetectionStrategy> FALLBACK_DETECTION_STRATEGY = NamedInterfacesDetectionStrategy::explicitlyAnnotated;

    /**
	 * Returns the {@link NamedInterfacesDetectionStrategy} to be used to detect {@link NamedInterfaces}. Will use
	 * the following algorithm:
	 * <ol>
	 * <li>Use the prepared strategies if either {@code direct-sub-packages} or {@code explicitly-annotated} is configured
	 * for the {@code spring.modulith.detection-strategy} configuration property.</li>
	 * <li>Interpret the configured value as class if it doesn't match the predefined values just described.</li>
	 * <li>A final fallback on the {@code explicitly-annotated}.</li>
	 * </ol>
	 *
	 * @return will never be {@literal null}.
	 */
	static NamedInterfacesDetectionStrategy getStrategy() {

		var environment = new StandardEnvironment();
		ConfigDataEnvironmentPostProcessor.applyTo(environment);

		var configuredStrategy = environment.getProperty(DETECTION_STRATEGY_PROPERTY, String.class);

		// Nothing configured? Use fallback.
		if (!StringUtils.hasText(configuredStrategy)) {
			return FALLBACK_DETECTION_STRATEGY.get();
		}

		// Any of the prepared ones?
        if (configuredStrategy.equals("explicitly-annotated")) {
            return NamedInterfacesDetectionStrategy.explicitlyAnnotated();
        }

		try {

			// Lookup configured value as class
			var strategyType = ClassUtils.forName(configuredStrategy, ApplicationModules.class.getClassLoader());
			return BeanUtils.instantiateClass(strategyType, NamedInterfacesDetectionStrategy.class);

		} catch (ClassNotFoundException | LinkageError o_O) {
			throw new IllegalStateException(o_O);
		}
	}
}
