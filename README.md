# Copyright Checker Gradle Plugin

## Purpose of this plugin

This plugin can be used to sensure some assets contain in their metadata credits or legal mentions.  
Indeed we may use in our apps resources like images, and these files in most of cases must contain copyright or other notices.
In order to prevent developers to push to production apps with images without these credits, this plugin can be used.

The behavior of the plugin in quite simple: for each file look in metadata some expected fields, and compare to an expected string.
If a file does not contain the expected fields or the credits, an exception is thrown.
The plugin looks recursively in the folder and its subfolders.

**This version can only deal with PNG files**


## The Gradle task to add to build script

You can include this task on your Gradle plugin

```groovy
copyrightchecker {

	// Mandatory attribute
	folderToCheck "path/to/folder/with/resources"

	// Mandatory attribute
	// This is the line we want to find in resources, i.e. the line your designers include in images
	creditLineToFind "My Project, (c) Copyright Me-MySelf-And-I- SA 2020, CC-BY-SA-NC 4.0"

	// Optional attribute, if undefined the value will be seen as false
	// Displays more logs
	verbose true

}
```
