## File Tree Viewer 
[![Build Status](https://travis-ci.org/Undin/file-tree-viewer.svg?branch=master)](https://travis-ci.org/Undin/file-tree-viewer)

Swing application to display file tree.

Features:
* Shows file tree of: 
    * local file system
    * zip archives: zip and jar
    * FTP servers 
* Allows to add/remove FTP servers
* Shows preview of image and text files
* Filtering displaying files by their extension
 
## Build and test
Run `./gradlew shadowJar` to build project (without launching tests). It'll create fat jar in `build/libs/`.
To run test launch `./gradlew check`.

## Run
Run `java -jar build/libs/file-tree-viewer.jar`.
