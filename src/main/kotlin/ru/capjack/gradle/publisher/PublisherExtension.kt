package ru.capjack.gradle.publisher

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.property


open class PublisherExtension(private val project: Project) {
	val PUBLIC get() = Scope.PUBLIC
	val PRIVATE get() = Scope.PRIVATE
	
	var scope: Property<Scope> = project.objects.property<Scope>().convention(project.provider {
		if (project == project.rootProject) Scope.PUBLIC
		else project.rootProject.extensions.findByType<PublisherExtension>()?.scope?.get()
			?: Scope.PUBLIC
	})
	
	enum class Scope {
		PUBLIC,
		PRIVATE
	}
}
