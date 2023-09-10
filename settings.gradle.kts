pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://s01.oss.sonatype.org/content/groups/public")  }
    }
}

rootProject.name = "MVVMUtils"
include(":app")
 