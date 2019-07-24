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

/**
 * Defines the extension of the plugin with the attributes to define in the Gradle file using this plugin.
 * With this definition the task for the plugin will seem like:
     <pre>
            copyrightchecker {
                folderToCheck "src/main/resources"
                creditLineToFind "Copyright 1970 - 2007 Foo Bar wizz"
                verbose true
            }
     </pre>
 *
 * @author Pierre-Yves Lapersonne
 * @since 14/06/2019
 * @version 1.0.0
 */
class CopyrightCheckerPluginExtension {

    /**
     * The folder containing the resources to analyse.
     */
    String folderToCheck

    /**
     * The credit string to check in metadata fields
     */
    String creditLineToFind

    /**
     * Flag indicating if the plugin should be verbose or not (i.e. display more traces).
     * If not defined, wil be defined to false.
     */
    Boolean verbose

}