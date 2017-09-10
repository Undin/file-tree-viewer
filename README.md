## File Tree Viewer 
[![Build Status](https://travis-ci.org/Undin/file-tree-viewer.svg?branch=master)](https://travis-ci.org/Undin/file-tree-viewer)
[![Build status](https://ci.appveyor.com/api/projects/status/iwrdhes90qq96e8l/branch/master?svg=true)](https://ci.appveyor.com/project/Undin/file-tree-viewer/branch/master)

Swing application to display file tree.

Features:
* Shows file tree of: 
    * local file system
    * archives (zip and jar)
    * FTP servers 
* Allows to add/remove FTP servers
* Shows preview of image and text files
* Filters displaying files by their extensions
 
## Build and test
Run `./gradlew shadowJar` to build project (without launching tests). It'll create fat jar in `build/libs/`.
To run test launch `./gradlew check`.

## Run
Run `java -jar build/libs/file-tree-viewer.jar`.
