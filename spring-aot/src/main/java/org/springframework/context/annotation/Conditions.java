package org.springframework.context.annotation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.ConfigurationCondition.ConfigurationPhase;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;

/**
 * Abstractions for {@link Conditions} read on an annotated type.
 *
 * @author Stephane Nicoll
 */
final class Conditions {

	private final AnnotatedTypeMetadata metadata;

	private final List<ConditionDefinition> conditionDefinitions;

	private Conditions(@Nullable AnnotatedTypeMetadata metadata, @Nullable List<ConditionDefinition> conditionDefinitions) {
		this.metadata = metadata;
		this.conditionDefinitions = (conditionDefinitions != null)
				? new ArrayList<>(conditionDefinitions) : new ArrayList<>();
	}

	AnnotatedTypeMetadata getMetadata() {
		return this.metadata;
	}

	List<ConditionDefinition> getConditionDefinitions() {
		return this.conditionDefinitions;
	}

	ConfigurationPhase getRequiredPhase() {
		if (this.metadata instanceof AnnotationMetadata &&
				ConfigurationClassUtils.isConfigurationCandidate((AnnotationMetadata) metadata)) {
			return ConfigurationPhase.PARSE_CONFIGURATION;
		}
		return ConfigurationPhase.REGISTER_BEAN;
	}

	String determineAnnotatedTypeId() {
		if (this.metadata instanceof ClassMetadata) {
			return ((ClassMetadata) this.metadata).getClassName();
		}
		if (this.metadata instanceof MethodMetadata) {
			MethodMetadata methodMetadata = (MethodMetadata) this.metadata;
			return String.format("%s#%s", methodMetadata.getDeclaringClassName(), methodMetadata.getMethodName());
		}
		return this.metadata.toString();
	}

	/**
	 * Create an instance based on the specified {@link AnnotatedTypeMetadata metadata}.
	 * @param metadata the meta data
	 * @return the conditions
	 */
	static Conditions from(@Nullable AnnotatedTypeMetadata metadata) {
		if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
			return new Conditions(metadata, null);
		}
		return new Conditions(metadata, getConditionClasses(metadata));
	}

	private static List<ConditionDefinition> getConditionClasses(AnnotatedTypeMetadata metadata) {
		List<ConditionDefinition> definitions = new ArrayList<>();
		metadata.getAnnotations().stream(Conditional.class).forEach((annotation) -> {
			for (String className : annotation.getStringArray("value")) {
				definitions.add(new ConditionDefinition(annotation, className));
			}
		});
		return definitions;
	}

}