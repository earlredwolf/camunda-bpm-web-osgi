import groovy.io.FileType

def list = []
File manifestFile = new File(project.basedir,"target/MANIFEST.MF")
manifestFile << "Bundle-Classpath: .,WEB-INF/classes,\n"
def dir = new File(project.basedir,"target/unpacked-classes/WEB-INF/lib")
dir.eachFileRecurse (FileType.FILES) { file ->
    if (!file.name.startsWith("camunda-engine-7")) {
        manifestFile << " WEB-INF/lib/" + file.name + ",\n"
    }
}


new File(project.basedir,"target/deps").eachFileRecurse (FileType.FILES) { file ->
        manifestFile << " WEB-INF/lib/" + file.name + ",\n"
}

