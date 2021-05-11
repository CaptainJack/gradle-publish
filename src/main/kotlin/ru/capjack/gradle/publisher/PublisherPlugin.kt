package ru.capjack.gradle.publisher

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
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
				configurePublishingRepositories(this@afterEvaluate, this)
				configurePublishingPublications(this@afterEvaluate, this)
			}
		}
	}
	
	private fun configurePublishingRepositories(project: Project, publishing: PublishingExtension) {
		if (publishing.repositories.isEmpty()) {
			val reposit = project.rootProject.extensions.getByType<RepositExtension>()
			val publish = project.extensions.getByType<PublisherExtension>()
			when (publish.scope.get()) {
				PublisherExtension.Scope.PUBLIC  -> publishing.repositories.mavenCapjackPublic(reposit)
				PublisherExtension.Scope.PRIVATE -> publishing.repositories.mavenCapjackPrivate(reposit)
			}
		}
	}
	
	private fun configurePublishingPublications(project: Project, publishing: PublishingExtension) {
		if (publishing.publications.isEmpty()) {
			val publicationName = project.name
				.split('_', '-')
				.joinToString("", transform = String::capitalize)
				.decapitalize()
			
			if (project.pluginManager.hasPlugin("org.gradle.java")) {
				var sourcesJar = project.tasks.findByName("sourcesJar")
				
				if (sourcesJar == null) {
					sourcesJar = project.tasks.create<Jar>("sourcesJar") {
						archiveClassifier.set("sources")
						from(project.extensions.getByName<SourceSetContainer>("sourceSets")["main"].allSource)
					}
				}
				
				publishing.publications.create<MavenPublication>(publicationName) {
					artifactId = project.name
					groupId = project.group.toString()
					version = project.version.toString()
					
					from(project.components["java"])
					artifact(sourcesJar)
				}
			}
		}
	}
}

