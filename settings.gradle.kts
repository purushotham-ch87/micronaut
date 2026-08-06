rootProject.name = "micronaut"

// Add your submodule folder names here
include(":mm-getting-started")
//project(":mm-getting-started").projectDir = file("mm-getting-started")
include(":mm-cloud-native")
//project(":mm-cloud-native").projectDir = file("mm-cloud-native")

include(":mm-cloud-web")
include(":mm-cloud-keyclock")
include(":mm-cloud-openbao")