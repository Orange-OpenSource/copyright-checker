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

import org.w3c.dom.NamedNodeMap

import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.metadata.IIOMetadata
import javax.imageio.stream.ImageInputStream
import org.w3c.dom.Node


/**
 * Defines the logic of the plugin ran by the task..
 * This version deals only with PNG files, the most used format of images in used yet.
 * Future releases may include support for JPG and SVG formats.
 *
 * PNG assets must contain at least in their metadata "iTXtEntry" or "TextEntry" fields with the credits.
 * A PNG without these fields, or without credits mentioned in these fields, will be considered as an unsuitable file.
 *
 * @author Pierre-Yves Lapersonne
 * @since 14/06/2019
 * @version 1.1.0
 */
class CopyrightCheckerService {

    /**
     * Flag indicating if more traces can be written or not
     */
    public final Boolean isVerbose

    /**
     * The credit to look inside images
     */
    public final String creditsToLookFor

    /**
     * The path to the folder which contains the images to process
     */
    public final String folderToProcess

    /**
     * The current file name in test
     */
    private String currentFileName

    /**
     * Flag indicating ig the expected fields ("iTXtEntry" or "TextEntry") have been found or not for the current file.
     */
    private Boolean haveExpectedFieldsBeenFound

    /**
     * Default constructor
     *
     * @param isVerbose True if more traces must be displayed, false otherwise
     * @param credits The credits to look for in the files included in the folder
     * @param folder The folder containing the images to process
     */
    CopyrightCheckerService(Boolean isVerbose, String credits, String folder) {
        super()
        this.isVerbose = isVerbose
        creditsToLookFor = credits
        folderToProcess = folder
        currentFileName = ""
        haveExpectedFieldsBeenFound = false
    }

    /**
     * Looks for credits recursively in images located in the folder.
     * If there is at least one file without credits in metadata, throws an exception.
     * This version can deal only with .png files.
     * If all the metadata have been parsed but the expected fields have not been found ("iTXtEntry" or "TextEntry"),
     *  throws an exception.
     *
     * @throws IllegalArgumentException If parameters are not defined
     * @throws MissingMetadataFieldsInResourceException If expected fields not found in metadata
     */
    def lookForCreditsInFolder() {

        // Check arguments
        if (folderToProcess == null || folderToProcess.isEmpty()){
            throw new IllegalArgumentException("Folder is not defined")
        }

        // Look recursively inside folders
        def targetFileFilterRegex = ".*.png"
        new File(folderToProcess).traverse(type: groovy.io.FileType.FILES, nameFilter: ~/$targetFileFilterRegex/) { file ->
            currentFileName = file.canonicalPath
            haveExpectedFieldsBeenFound = false
            verbose "Found file '$currentFileName', reading it"
            Tuple2<IIOMetadata, String[]> bundle = extractMetadataForFile(file)
            readMetadataDetails(bundle.first, bundle.second)
            // No expected fields found and all fields parsed
            if (!haveExpectedFieldsBeenFound) {
                throw new MissingMetadataFieldsInResourceException(
                        "The expected fields iTXtEntry or TextEntry for the file '$currentFileName' have not been found")
            }
        }

        println ">>> WOOP WOOP! If you see this message, it means all files have been processed and contain credits :)"

    }

    /**
     * Reads from the file the available metadata
     *
     * @param file The file to parse
     * @return Tuple2<IIOMetadata, String[]> A bundle containing metadata and its keys
     * @throws Exception If something bad occurred during file parsing
     */
    private Tuple2<IIOMetadata, String[]> extractMetadataForFile(File file) throws Exception {

        ImageInputStream imageInputStream = ImageIO.createImageInputStream(file)
        Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream)

        if (imageReaders.hasNext()) {
            ImageReader imageReader = imageReaders.next()
            imageReader.setInput(imageInputStream, true)
            IIOMetadata metadata = imageReader.getImageMetadata(0)
            return new Tuple2<IIOMetadata, String[]>(metadata, metadata.getMetadataFormatNames())
        }

    }

    /**
     * Reads from the node all the values with these names
     *
     * @param metadataNode The node to parse
     * @param metadataNames The metadata entries to read
     */
    private def readMetadataDetails(IIOMetadata metadataNode, String[] metadataNames) {
        for (int i = 0; i < metadataNames.length; i++) {
            //verbose "Looking in metadata bundle ${metadataNames[i]}"
            readMetadata(metadataNode.getAsTree(metadataNames[i]), 0)
        }
    }

    /**
     * Reads recursively starting from this node at this level the metadata entries.
     *
     * <b>We should refactor this method which seems to be optimizable</b>
     *
     * @param node The node to start with
     * @param level The level to start with
     */
    private def readMetadata(Node node, int level) {
        def nodeName = node.getNodeName()
        // Found the expected metadata field
        if (nodeName == "iTXtEntry" || nodeName == "TextEntry") {
            haveExpectedFieldsBeenFound = true
            readForPngFile(node)
        // Go to other metadata fields tot ry to find the expected ones
        } else {
            Node child = node.getFirstChild()
            while (child != null) {
                readMetadata(child, ++level)
                child = child.getNextSibling()
            }
        }
    }

    /**
     * Reads metadata for this node with the hypothesis we are dealing with a PNG file.
     *
     * For PNG files, will look for nodes with name "iTXtEntry" and "TextEntry".
     * For "iTXtEntry" will look fro attribute "text".
     * For "TextEntry" will look fro attribute "value".
     *
     * @param node The node to deal with
     * @throws MissingCreditsInResourceException If a resource does not have the expected credits
     */
    private def readForPngFile(Node node) {

        def nodeName = node.getNodeName()

        // The API of nodes is based on org.w3c classes which do not inherit from Collections.
        // Thus no stream nor filtering :'(

        NamedNodeMap nodeMap = node.getAttributes()
        if (nodeMap != null) {
            for (int i = 0; i < nodeMap.length; i++) {

                Node attr = nodeMap.item(i)
                String attributeName = attr.getNodeName()

                if (nodeName == "iTXtEntry" && attributeName == "text") {
                    if (checkIfCreditsAvailable(attr.getNodeValue())) {
                        verbose "Nothing wrong with this file in iTXtEntry"
                    } else {
                        throw new MissingCreditsInResourceException("The file '$currentFileName' does not contain the expected credits '$creditsToLookFor'")
                    }

                } else { // TextEntry
                    if (attributeName == "value") {
                        if (checkIfCreditsAvailable(attr.getNodeValue())) {
                            verbose "Nothing wrong with this file in TextEntry"
                        } else {
                            throw new MissingCreditsInResourceException("The file '$currentFileName' does not contain the expected credits '$creditsToLookFor'")
                        }
                    }
                }

            }
        }

    }

    /**
     * Checks in the supplied text content if the credits to look for are missing or not.
     * The comparison is quite dumb, just a search of credit line in the content.
     *
     * @param content The content lo look in
     * @return Boolean True if the credit is available, false otherwise
     */
    private Boolean checkIfCreditsAvailable(String content) {
        return content.contains(creditsToLookFor)
    }

    /**
     * Prints the message according to the internal flag for verbose mode
     *
     * @param message The message to print, if verbose mode enabled
     */
    private def verbose(String message) {
        if (isVerbose) println ">>>>>> $message"
    }

}
