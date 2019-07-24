/**
     Copyright Checker
     Copyright (C) 2019 Orange SA

     MIT License
     Permission is hereby granted, free of charge, to any person obtaining a copy
     of this software and associated documentation files (the "Software"), to deal
     in the Software without restriction, including without limitation the rights
     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     copies of the Software, and to permit persons to whom the Software is
     furnished to do so, subject to the following conditions:
     The above copyright notice and this permission notice shall be included in all
     copies or substantial portions of the Software.
     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
     SOFTWARE.
 */

package com.orange.labs.copyrightchecker

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Defines the Gradle task for the copyright checker.
 * Will trigger the service which deals with the images and the metadata.
 *
 * @author Pierre-Yves Lapersonne
 * @since 14/06/2019
 * @version 1.0.0
 */
class CopyrightCheckerTask extends DefaultTask {

    /**
     * Prepares the service which will look in the target folder, find recursively the images and verify
     * the metadata to look for copyright.
     * This method is the task to call of the plugin.
     */
    @TaskAction
    def copyrightchecker() {

        println ">>> Running 'copyrightchecker' plugin task"
        println(">>> Preparing options for the service")

        def opts = [:]

        // Get the folder containing resources to check
        opts.folderToCheck = project.copyrightchecker.folderToCheck
        if (opts.folderToCheck == "" || opts.folderToCheck == null) {
            throw new IllegalArgumentException("The folder to look in is not defined")
        }

        // Get the credit line to look for file to process
        opts.creditLineToFind = project.copyrightchecker.creditLineToFind
        if (opts.creditLineToFind == "" || opts.creditLineToFind == null){
            throw new IllegalArgumentException("The credits to look for are not defined")
        }

        // Get the verbose flag
        def verboseFlag = project.copyrightchecker.verbose
        opts.isVerbose = !(verboseFlag == null || verboseFlag == "" || !verboseFlag)

        // Run the plugin logic
        def service = new CopyrightCheckerService((Boolean) opts.isVerbose,
                opts.creditLineToFind.toString(),
                opts.folderToCheck.toString())
        println ">>> Will look for line '${service.creditsToLookFor}' in folder '${service.folderToProcess}' with verbose mode to '${service.isVerbose}'"
        service.lookForCreditsInFolder()

    }

}