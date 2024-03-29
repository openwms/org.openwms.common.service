
// Path where the docToolchain will produce the output files.
// This path is appended to the docDir property specified in gradle.properties
// or in the command line, and therefore must be relative to it.
outputPath = 'build'

inputPath = '.'

inputFiles = [
        [file: 'index.adoc', formats: ['html','pdf','docbook']],
        [file: 'ppt/Demo.pptx.ad', formats: ['revealjs']]
             ]

//folders in which asciidoc will find images.
//these will be copied as resources to ./images
//folders are relative to inputPath
imageDirs = [
        /** imageDirs **/
        "${inputPath}/images"
]

taskInputsDirs = ["${inputPath}/src",
                  "${inputPath}/images",
                 ]

taskInputsFiles = ["${inputPath}/index.adoc"]

confluence = [:]
confluence.with {
    input = [[ file: "build/html5/index.html", ancestorId: '836829841']]
    ancestorId = '836829841'
    api = 'https://openwms.atlassian.net/wiki/rest/api/'
    spaceKey = 'COMMON'
    createSubpages = false
    pagePrefix = ''
    preambleTitle = 'Architecture'
    pageSuffix = ' (COM)'
    credentials = "${System.getenv('ATLASSIAN_USER')}:${System.getenv('ATLASSIAN_PASSWORD')}".bytes.encodeBase64().toString()
//    extraPageContent = '<ac:structured-macro ac:name="warning"><ac:parameter ac:name="title" /><ac:rich-text-body>This is a generated page, do not edit!</ac:rich-text-body></ac:structured-macro>'
}


github = [:]
github.with {
    user = "${System.getenv('GITHUB_USER')}"
    password = "${System.getenv('GITHUB_PASSWORD')}"
    root = "https://api.github.com/"
    organization = "openwms"
    repository = "org.openwms.common.service"
    resultsPerPage = 100
}