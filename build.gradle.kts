plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
	`maven-publish`
	id("com.gradle.plugin-publish") version "0.15.0"
	id("nebula.release") version "15.3.1"
}

group = "ru.capjack.gradle"

repositories {
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	implementation(kotlin("gradle-plugin"))
	implementation("ru.capjack.gradle:gradle-reposit:1.0.0")
	implementation("com.netflix.nebula:nebula-release-plugin:15.3.1")
}

gradlePlugin {
	plugins.create("CapjackPublisher") {
		id = "ru.capjack.publisher"
		implementationClass = "ru.capjack.gradle.publisher.PublisherPlugin"
		displayName = "CapjackPublisher"
	}
}

pluginBundle {
	vcsUrl = "https://github.com/CaptainJack/gradle-publish"
	website = vcsUrl
	description = "Provides publishing to CaptainJack repositories"
	tags = listOf("capjack")
}

tasks["postRelease"].dependsOn("publishPlugins")