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

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Base class for the Gradle plugin.
 * Will trigger a task which will check look recursively in a folder to get image files and read from them metadata details
 * so as to check if copyright mention has been defined.
 *
 * @author Pierre-Yves Lapersonne
 * @since 14/06/2019
 * @see Plugin, Project
 * @version 1.0.0
 */
class CopyrightCheckerPlugin implements Plugin<Project> {

    /**
     * The name of the plugin and task
     */
    static final String NAME = 'copyrightchecker'

    /**
     *
     * @param project
     */
    @Override
    void apply(Project project) {

        println ">>> Running plugin CopyrightChecker"

        project.extensions.create(NAME, CopyrightCheckerPluginExtension)
        project.task(NAME, type: CopyrightCheckerTask).doLast {
            println ">>> Plugin execution completed!"
        }

    }

}
