pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "PaymentDivider"

data class Module(
    val name: String,
    val path: String
)

val modules = mutableListOf<Module>()

fun module(name: String, path: String) {
    modules.add(Module(name, "$rootDir/$path"))
}

module(":app", "architecture/app")
module(":domain", "architecture/domain")

modules.forEach {
    include(it.name)
    project(it.name).projectDir = file(it.path)
}
