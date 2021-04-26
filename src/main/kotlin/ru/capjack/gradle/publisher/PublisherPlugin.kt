package ru.capjack.gradle.publisher

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.mavenCapjackPrivate
import org.gradle.kotlin.dsl.mavenCapjackPublic
import ru.capjack.gradle.reposit.RepositExtension

class PublisherPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.pluginManager.apply("maven-publish")
		project.extensions.create<PublisherExtension>("publisher", project)
		
		project.rootProject.pluginManager.apply("ru.capjack.reposit")
		project.rootProject.pluginManager.apply("nebula.release")
		project.rootProject.tasks["postRelease"].dependsOn(project.tasks["publish"])
		
		project.afterEvaluate {
			configure<PublishingExtension> {
				val reposit = rootProject.extensions.getByType<RepositExtension>()
				val publish = extensions.getByType<PublisherExtension>()
				when (publish.scope.get()) {
					PublisherExtension.Scope.PUBLIC  -> repositories.mavenCapjackPublic(reposit)
					PublisherExtension.Scope.PRIVATE -> repositories.mavenCapjackPrivate(reposit)
				}
			}
		}
	}
}

