package com.suzushinlab.imagecompressorfx

import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.geometry.Insets
import javafx.scene.control.Alert.AlertType
import javafx.stage.DirectoryChooser
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageCompressorFX : Application() {
    private var stage: Stage? = null
    private val inputPathField = TextField().apply { promptText = "Input Directory" }
    private val outputPathField = TextField().apply { promptText = "Output Directory" }
    private val compressionRateComboBox = ComboBox<String>().apply {
        items.addAll("0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9")
        value = "0.5"
    }
    private val logArea = TextArea().apply {
        isEditable = false
    }

    override fun start(primaryStage: Stage) {
        stage = primaryStage
        val root = VBox().apply {
            padding = Insets(10.0)
            spacing = 10.0
            children.addAll(
                createDirectorySelector(inputPathField, "Select Input Directory"),
                createDirectorySelector(outputPathField, "Select Output Directory"),
                Label("Compression Rate: smaller numbers correspond to higher compression rates."),
                compressionRateComboBox,
                createCompressButton(),
                Label("Log:"),
                logArea
            )
        }

        primaryStage.scene = Scene(root, 540.0, 480.0)
        primaryStage.title = "ImageCompressorFX"
        primaryStage.show()
    }

    private fun createDirectorySelector(pathField: TextField, buttonText: String): VBox {
        val directoryChooser = DirectoryChooser()
        val selectButton = Button(buttonText).apply {
            setOnAction {
                val file = directoryChooser.showDialog(stage)
                if(file != null) {
                    pathField.text = file.absolutePath
                }
            }
        }
        return VBox(pathField, selectButton)
    }

    private fun createCompressButton(): Button {
        return Button("Start Compression").apply {
            setOnAction {
                val inputDir = File(inputPathField.text)
                val outputDir = File(outputPathField.text)
                val compressionRate = compressionRateComboBox.value.toDouble()

                if (!inputDir.exists() || !inputDir.isDirectory) {
                    showAlert("Input Error", "Invalid input directory")
                    return@setOnAction
                }
                if (!outputDir.exists() || !outputDir.isDirectory) {
                    showAlert("Output Error", "Invalid output directory")
                    return@setOnAction
                }

                inputDir.listFiles { _, name -> name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif") }
                    ?.forEach { file ->
                        try {
                            val originalSize = file.length()
                            val outputFile = File(outputDir, "${file.nameWithoutExtension}-min.${file.extension}")
                            compressImage(file, compressionRate, outputFile)
                            val compressedSize = outputFile.length()
                            val compressionRatio = if (originalSize > 0) {
                                (1 - (compressedSize.toDouble() / originalSize.toDouble())) * 100
                            } else {
                                0.0
                            }
                            logArea.appendText("Before: ${file.name} - $originalSize bytes\n")
                            logArea.appendText("After: ${outputFile.name} - $compressedSize bytes\n")
                            logArea.appendText("Compression Ratio: ${"%.2f".format(compressionRatio)}%\n\n")
                        } catch (e: IOException) {
                            logArea.appendText("Error processing ${file.name}: ${e.message}\n")
                        }
                    }
                logArea.appendText("Compression finished.\n")
            }
        }
    }

    private fun compressImage(inputFile: File, compressionRate: Double, outputFile: File) {
        val originalImage = ImageIO.read(inputFile)
        when (val formatName = inputFile.extension.lowercase()) {
            "jpg", "jpeg" -> compressJpg(originalImage, compressionRate, outputFile)
            "png" -> compressPng(originalImage, outputFile)
            "gif" -> compressGif(originalImage, outputFile)
            else -> throw IllegalArgumentException("Unsupported file format: $formatName")
        }
    }

    private fun compressJpg(image: BufferedImage, compressionRate: Double, outputFile: File) {
        val imageWriter = ImageIO.getImageWritersByFormatName("jpg").next()
        val imageOutputStream = FileOutputStream(outputFile)

        val imageWriteParam = imageWriter.defaultWriteParam.apply {
            compressionMode = ImageWriteParam.MODE_EXPLICIT
            compressionQuality = compressionRate.toFloat() // Compression rate between 0.0 and 1.0
        }

        imageWriter.output = ImageIO.createImageOutputStream(imageOutputStream)
        imageWriter.write(null, javax.imageio.IIOImage(image, null, null), imageWriteParam)

        imageOutputStream.close()
        imageWriter.dispose()
    }

    private fun compressPng(image: BufferedImage, outputFile: File) {
        // PNG format does not use compressionQuality, just saves the image with default compression
        ImageIO.write(image, "png", outputFile)
    }

    private fun compressGif(image: BufferedImage, outputFile: File) {
        // GIF format does not use compressionQuality, just saves the image with default compression
        ImageIO.write(image, "gif", outputFile)
    }

    private fun showAlert(title: String, message: String) {
        Alert(AlertType.ERROR).apply {
            this.title = title
            this.headerText = null
            this.contentText = message
            showAndWait()
        }
    }
}

fun main() {
    Application.launch(ImageCompressorFX::class.java)
}
