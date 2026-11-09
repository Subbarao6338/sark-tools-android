package com.toolbox.doctools

import androidx.compose.runtime.Composable
import com.toolbox.everyday.ocr.OcrScreen
import com.toolbox.everyday.notepad.NotepadScreen
import com.toolbox.everyday.pdftoolkit.PdfToolkitScreen
import com.toolbox.everyday.docscanner.DocumentScannerScreen
import com.toolbox.documents.pdf.PdfEditorScreen
import com.toolbox.documents.word.WordEditorScreen
import com.toolbox.documents.creator.DocumentCreationScreen

object DocToolsRegistry {
    @Composable fun Ocr() = OcrScreen()
    @Composable fun Notepad() = NotepadScreen()
    @Composable fun PdfToolkit() = PdfToolkitScreen()
    @Composable fun DocumentScanner() = DocumentScannerScreen()
    @Composable fun PdfEditor() = PdfEditorScreen()
    @Composable fun WordEditor() = WordEditorScreen()
    @Composable fun DocumentCreation() = DocumentCreationScreen()
    @Composable fun PdfToImages() = PdfToImagesScreen()
    @Composable fun CompressPdf() = CompressPdfScreen()
}
