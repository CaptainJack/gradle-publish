plugins {
	`kotlin-dsl`
	id("com.gradle.plugin-publish") version "1.2.0"
	id("nebula.release") version "15.3.1"
}

group = "ru.capjack.gradle"

repositories {
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	implementation(kotlin("gradle-plugin"))
	implementation("ru.capjack.gradle:gradle-reposit:1.1.0")
	implementation("com.netflix.nebula:nebula-release-plugin:15.3.1")
}

gradlePlugin {
	vcsUrl.set("https://github.com/CaptainJack/gradle-publish")
	website.set(vcsUrl.get())
	plugins.create("CapjackPublisher") {
		id = "ru.capjack.publisher"
		implementationClass = "ru.capjack.gradle.publisher.PublisherPlugin"
		displayName = "CapjackPublisher"
		description = "Provides publishing to CaptainJack repositories"
		tags.set(listOf("capjack"))
	}
}

tasks["postRelease"].dependsOn("publishPlugins")