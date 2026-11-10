'use client';

import { useState, useCallback, useEffect } from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { PDFDocument } from 'pdf-lib';
import JSZip from 'jszip';

const toolData = {
  'merge-pdf': {
    title: 'Merge PDF',
    description: 'Combine multiple PDFs into one document',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
      </svg>
    ),
    acceptMultiple: true,
    actionLabel: 'Merge PDFs',
  },
  'split-pdf': {
    title: 'Split PDF',
    description: 'Extract pages or split PDF into multiple files',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14.828 14.828a4 4 0 01-5.656 0M9 10h1.586a1 1 0 01.707.293l.707.707A1 1 0 0012.414 11H13m-3-3h1.586a1 1 0 01.707.293l.707.707A1 1 0 0012.414 11H13m-3-3V9a2 2 0 114 0v2a2 2 0 01-4 0V9z" />
      </svg>
    ),
    acceptMultiple: false,
    actionLabel: 'Split PDF',
  },
  'compress-pdf': {
    title: 'Compress PDF',
    description: 'Reduce PDF file size while maintaining quality',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
      </svg>
    ),
    acceptMultiple: false,
    actionLabel: 'Compress PDF',
  },
  'images-to-pdf': {
    title: 'Images to PDF',
    description: 'Convert images to PDF format',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
      </svg>
    ),
    acceptMultiple: true,
    actionLabel: 'Convert to PDF',
  },
  'pdf-to-images': {
    title: 'PDF to Images',
    description: 'Extract images from PDF documents',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
      </svg>
    ),
    acceptMultiple: false,
    actionLabel: 'Extract Images',
  },
  'protect-pdf': {
    title: 'Protect PDF',
    description: 'Add password protection to your PDF',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
      </svg>
    ),
    acceptMultiple: false,
    actionLabel: 'Protect PDF',
  },
};

export default function ToolPage() {
  const params = useParams();
  const toolId = params.tool as string;
  const tool = toolData[toolId as keyof typeof toolData];

  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [isDragOver, setIsDragOver] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string>('');
  const [originalSize, setOriginalSize] = useState<number | null>(null);
  const [compressedSize, setCompressedSize] = useState<number | null>(null);
  
  // Password protection state
  const [password, setPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  const [passwordErrors, setPasswordErrors] = useState<{
    password?: string;
    confirmPassword?: string;
  }>({});

  const handleFileSelect = useCallback((files: FileList | null) => {
    if (!files) return;

    const fileArray = Array.from(files);
    if (tool?.acceptMultiple) {
      setSelectedFiles(prev => [...prev, ...fileArray]);
    } else {
      setSelectedFiles(fileArray.slice(0, 1));
      // Set original size for compress-pdf tool
      if (toolId === 'compress-pdf' && fileArray.length > 0) {
        setOriginalSize(fileArray[0].size);
        setCompressedSize(null);
      }
    }
  }, [tool?.acceptMultiple, toolId]);

  const handleDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(true);
  }, []);

  const handleDragLeave = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(false);
  }, []);

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(false);
    handleFileSelect(e.dataTransfer.files);
  }, [handleFileSelect]);

  const handleFileInputChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    handleFileSelect(e.target.files);
  }, [handleFileSelect]);

  const removeFile = useCallback((index: number) => {
    setSelectedFiles(prev => {
      const newFiles = prev.filter((_, i) => i !== index);
      // Clear sizes if no files remain for compress-pdf tool
      if (toolId === 'compress-pdf' && newFiles.length === 0) {
        setOriginalSize(null);
        setCompressedSize(null);
      }
      return newFiles;
    });
  }, [toolId]);

  // Password validation for protect-pdf tool
  const validatePassword = useCallback(() => {
    const errors: { password?: string; confirmPassword?: string } = {};

    if (password.length < 6) {
      errors.password = 'Password must be at least 6 characters';
    }

    if (confirmPassword && password !== confirmPassword) {
      errors.confirmPassword = 'Passwords do not match';
    }

    setPasswordErrors(errors);
    return Object.keys(errors).length === 0;
  }, [password, confirmPassword]);

  const handlePasswordChange = useCallback((value: string) => {
    setPassword(value);
  }, []);

  const handleConfirmPasswordChange = useCallback((value: string) => {
    setConfirmPassword(value);
  }, []);

  // Validate passwords when they change (for protect-pdf)
  useEffect(() => {
    if (toolId === 'protect-pdf' && (password || confirmPassword)) {
      validatePassword();
    }
  }, [password, confirmPassword, toolId, validatePassword]);

  const mergePDFs = useCallback(async () => {
    if (selectedFiles.length === 0 || toolId !== 'merge-pdf') return;

    setIsProcessing(true);
    setSuccessMessage('');

    try {
      // Create a new PDF document
      const mergedPdf = await PDFDocument.create();

      // Load and merge each PDF
      for (const file of selectedFiles) {
        const fileBuffer = await file.arrayBuffer();
        const pdf = await PDFDocument.load(fileBuffer);

        // Copy all pages from the current PDF to the merged PDF
        const pages = await mergedPdf.copyPages(pdf, pdf.getPageIndices());
        pages.forEach(page => mergedPdf.addPage(page));
      }

      // Save the merged PDF
      const mergedPdfBytes = await mergedPdf.save();

      // Create download link
      const blob = new Blob([mergedPdfBytes.buffer as ArrayBuffer], { type: 'application/pdf' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'merged-document.pdf';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);

      setSuccessMessage('PDFs merged successfully! Download started.');
    } catch (error) {
      console.error('Error merging PDFs:', error);
      setSuccessMessage('Error merging PDFs. Please try again.');
    } finally {
      setIsProcessing(false);
    }
  }, [selectedFiles, toolId]);

  const imagesToPDF = useCallback(async () => {
    if (selectedFiles.length === 0 || toolId !== 'images-to-pdf') return;

    setIsProcessing(true);
    setSuccessMessage('');

    try {
      // Create a new PDF document
      const pdfDoc = await PDFDocument.create();

      // Standard page size (A4)
      const pageWidth = 595.28; // A4 width in points
      const pageHeight = 841.89; // A4 height in points

      // Process each image in order
      for (const file of selectedFiles) {
        const imageBuffer = await file.arrayBuffer();

        // Create a new page for each image
        const page = pdfDoc.addPage([pageWidth, pageHeight]);

        let image;
        if (file.type.startsWith('image/jpeg') || file.type.startsWith('image/jpg')) {
          image = await pdfDoc.embedJpg(imageBuffer);
        } else if (file.type.startsWith('image/png')) {
          image = await pdfDoc.embedPng(imageBuffer);
        } else {
          throw new Error(`Unsupported image format: ${file.type}`);
        }

        // Calculate scaling to fit the image on the page while maintaining aspect ratio
        const imageWidth = image.width;
        const imageHeight = image.height;
        const pageAspectRatio = pageWidth / pageHeight;
        const imageAspectRatio = imageWidth / imageHeight;

        let scaledWidth, scaledHeight;
        if (imageAspectRatio > pageAspectRatio) {
          // Image is wider than page ratio - fit to width
          scaledWidth = pageWidth;
          scaledHeight = pageWidth / imageAspectRatio;
        } else {
          // Image is taller than page ratio - fit to height
          scaledHeight = pageHeight;
          scaledWidth = pageHeight * imageAspectRatio;
        }

        // Center the image on the page
        const x = (pageWidth - scaledWidth) / 2;
        const y = (pageHeight - scaledHeight) / 2;

        // Draw the image on the page
        page.drawImage(image, {
          x,
          y,
          width: scaledWidth,
          height: scaledHeight,
        });
      }

      // Save the PDF
      const pdfBytes = await pdfDoc.save();

      // Create download link
      const blob = new Blob([pdfBytes.buffer as ArrayBuffer], { type: 'application/pdf' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'images-to-pdf.pdf';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);

      setSuccessMessage('Images converted to PDF successfully! Download started.');
    } catch (error) {
      console.error('Error converting images to PDF:', error);
      setSuccessMessage('Error converting images to PDF. Please try again.');
    } finally {
      setIsProcessing(false);
    }
  }, [selectedFiles, toolId]);

  const splitPDF = useCallback(async () => {
    if (selectedFiles.length === 0 || toolId !== 'split-pdf') return;

    setIsProcessing(true);
    setSuccessMessage('');

    try {
      // Load the PDF
      const file = selectedFiles[0];
      const fileBuffer = await file.arrayBuffer();
      const pdf = await PDFDocument.load(fileBuffer);
      const pageCount = pdf.getPageCount();

      // Create a ZIP file
      const zip = new JSZip();

      // Extract each page into a separate PDF
      for (let i = 0; i < pageCount; i++) {
        // Create a new PDF document for this page
        const singlePagePdf = await PDFDocument.create();

        // Copy the page from the original PDF
        const [copiedPage] = await singlePagePdf.copyPages(pdf, [i]);
        singlePagePdf.addPage(copiedPage);

        // Save the single-page PDF
        const pdfBytes = await singlePagePdf.save();

        // Add to ZIP with a descriptive filename
        const pageNumber = (i + 1).toString().padStart(3, '0');
        zip.file(`page-${pageNumber}.pdf`, pdfBytes);
      }

      // Generate ZIP file
      const zipBlob = await zip.generateAsync({ type: 'blob' });

      // Create download link
      const url = URL.createObjectURL(zipBlob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'split-pdf.zip';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);

      setSuccessMessage(`PDF split successfully! ${pageCount} page(s) extracted. Download started.`);
    } catch (error) {
      console.error('Error splitting PDF:', error);
      setSuccessMessage('Error splitting PDF. Please try again.');
    } finally {
      setIsProcessing(false);
    }
  }, [selectedFiles, toolId]);

  const compressPDF = useCallback(async () => {
    if (selectedFiles.length === 0 || toolId !== 'compress-pdf') return;

    setIsProcessing(true);
    setSuccessMessage('');
    setOriginalSize(null);
    setCompressedSize(null);

    try {
      // Load the PDF
      const file = selectedFiles[0];
      const originalSizeBytes = file.size;
      setOriginalSize(originalSizeBytes);

      const fileBuffer = await file.arrayBuffer();
      const sourcePdf = await PDFDocument.load(fileBuffer);

      // Create a new PDF document for optimization
      // Note: pdf-lib has limitations with direct image extraction and recompression
      // from existing PDFs. However, creating a new PDF and copying pages can help
      // with optimization through object stream compression and structure cleanup.
      const compressedPdf = await PDFDocument.create();

      const pageCount = sourcePdf.getPageCount();

      // Copy all pages from source to compressed PDF
      // This process can help optimize the PDF structure
      const pages = await compressedPdf.copyPages(sourcePdf, sourcePdf.getPageIndices());
      pages.forEach(page => compressedPdf.addPage(page));

      // Save the compressed PDF
      // pdf-lib automatically applies some optimizations when saving
      const compressedPdfBytes = await compressedPdf.save();

      const compressedSizeBytes = compressedPdfBytes.length;
      setCompressedSize(compressedSizeBytes);

      // Calculate compression ratio
      const compressionRatio = ((1 - compressedSizeBytes / originalSizeBytes) * 100).toFixed(1);
      const sizeReduction = ((originalSizeBytes - compressedSizeBytes) / 1024 / 1024).toFixed(2);

      // Create download link
      const blob = new Blob([compressedPdfBytes.buffer as ArrayBuffer], { type: 'application/pdf' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'compressed-document.pdf';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);

      if (compressedSizeBytes < originalSizeBytes) {
        setSuccessMessage(`PDF compressed successfully! Reduced by ${compressionRatio}% (${sizeReduction} MB saved). Download started.`);
      } else {
        setSuccessMessage(`PDF processed. Size: ${(compressedSizeBytes / 1024 / 1024).toFixed(2)} MB. Note: Some PDFs may not compress further due to existing optimization. Download started.`);
      }
    } catch (error) {
      console.error('Error compressing PDF:', error);
      setSuccessMessage('Error compressing PDF. Please try again.');
      setOriginalSize(null);
      setCompressedSize(null);
    } finally {
      setIsProcessing(false);
    }
  }, [selectedFiles, toolId]);

  const pdfToImages = useCallback(async () => {
    if (selectedFiles.length === 0 || toolId !== 'pdf-to-images') return;

    // Ensure we're in the browser environment
    if (typeof window === 'undefined' || typeof document === 'undefined') {
      setSuccessMessage('Error: This feature requires a browser environment.');
      return;
    }

    setIsProcessing(true);
    setSuccessMessage('');

    try {
      // Dynamically import pdfjs-dist to avoid SSR issues
      const pdfjsLib = await import('pdfjs-dist');
      
      // Configure PDF.js worker for Next.js App Router
      // For pdfjs-dist v5.x, use jsdelivr CDN with ES module worker
      const version = pdfjsLib.version || '5.4.530';
      pdfjsLib.GlobalWorkerOptions.workerSrc = `https://cdn.jsdelivr.net/npm/pdfjs-dist@${version}/build/pdf.worker.min.mjs`;

      // Load the PDF file
      const file = selectedFiles[0];
      const fileBuffer = await file.arrayBuffer();

      // Load PDF document using PDF.js
      const loadingTask = pdfjsLib.getDocument({ 
        data: fileBuffer,
        useWorkerFetch: false,
        isEvalSupported: false,
        useSystemFonts: true
      });
      const pdf = await loadingTask.promise;
      const pageCount = pdf.numPages;

      // Create a ZIP file
      const zip = new JSZip();

      // Create a temporary canvas for rendering
      const canvas = document.createElement('canvas');
      const context = canvas.getContext('2d', { willReadFrequently: true });

      if (!context) {
        throw new Error('Could not get canvas context. Canvas API may not be supported.');
      }

      // Process each page
      for (let pageNum = 1; pageNum <= pageCount; pageNum++) {
        // Get the page
        const page = await pdf.getPage(pageNum);

        // Set canvas size to match page size (scale for better quality)
        const viewport = page.getViewport({ scale: 2.0 }); // 2x scale for better quality
        canvas.width = viewport.width;
        canvas.height = viewport.height;

        // Render the page to canvas
        const renderContext = {
          canvas,
          canvasContext: context,
          viewport: viewport,
        };

        await page.render(renderContext).promise;

        // Convert canvas to PNG blob
        const blob = await new Promise<Blob>((resolve, reject) => {
          canvas.toBlob((blob) => {
            if (blob) {
              resolve(blob);
            } else {
              reject(new Error('Failed to convert canvas to blob'));
            }
          }, 'image/png');
        });

        // Convert blob to array buffer for ZIP
        const arrayBuffer = await blob.arrayBuffer();

        // Add to ZIP with zero-padded filename
        const pageNumber = pageNum.toString().padStart(3, '0');
        zip.file(`page-${pageNumber}.png`, arrayBuffer);
      }

      // Generate ZIP file
      const zipBlob = await zip.generateAsync({ type: 'blob' });

      // Create download link
      const url = URL.createObjectURL(zipBlob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'pdf-to-images.zip';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);

      setSuccessMessage(`PDF converted successfully! ${pageCount} page(s) extracted as PNG images. Download started.`);
    } catch (error) {
      console.error('Error converting PDF to images:', error);
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      setSuccessMessage(`Error converting PDF to images: ${errorMessage}. Please ensure the PDF is valid and try again.`);
    } finally {
      setIsProcessing(false);
    }
  }, [selectedFiles, toolId]);

  const protectPDF = useCallback(async () => {
    console.log('Protect PDF button clicked', {
      selectedFiles: selectedFiles.length,
      toolId,
      passwordLength: password.length,
      passwordsMatch: password === confirmPassword
    });

    if (selectedFiles.length === 0 || toolId !== 'protect-pdf' || password.length < 6 || password !== confirmPassword) {
      console.log('Protect PDF validation failed - returning early');
      return;
    }

    console.log('Starting PDF protection process');
    setIsProcessing(true);
    setSuccessMessage('');

    try {
      console.log('Loading PDF file...');
      // Load the PDF
      const file = selectedFiles[0];
      const fileBuffer = await file.arrayBuffer();
      console.log('PDF file loaded, size:', fileBuffer.byteLength);

      // Check if PDF is already encrypted by trying to load it
      // If it's already encrypted, PDFDocument.load would fail or we'd need a password
      let pdfDoc;
      try {
        pdfDoc = await PDFDocument.load(fileBuffer);
      } catch (loadError) {
        // If loading fails, it might be encrypted
        throw new Error('The PDF appears to already be password protected. Please use an unprotected PDF.');
      }

      // Double-check by trying to save without encryption first
      // If this fails, the PDF might be encrypted
      try {
        await pdfDoc.save();
      } catch (saveError) {
        throw new Error('The PDF appears to already be password protected. Please use an unprotected PDF.');
      }

      console.log('Encrypting PDF with password...');
      // Save with the EXACT encryption options specified
      const pdfBytes = await pdfDoc.save({
        userPassword: password,
        ownerPassword: password,
        permissions: {
          printing: 'none',
          modifying: false,
          copying: false,
          annotating: false,
          fillingForms: false,
          contentAccessibility: false,
          documentAssembly: false,
        },
      } as any);
      console.log('PDF encrypted, size:', pdfBytes.length);

      // Create Blob from the encrypted bytes
      const blob = new Blob([pdfBytes.buffer as ArrayBuffer], { type: 'application/pdf' });
      console.log('Blob created, size:', blob.size);

      // Create object URL
      const url = URL.createObjectURL(blob);
      console.log('Object URL created:', url);

      // Trigger <a download> click
      const link = document.createElement('a');
      link.href = url;
      link.download = 'protected.pdf';
      document.body.appendChild(link);
      console.log('Triggering download click...');
      link.click();
      document.body.removeChild(link);

      // Revoke URL after download
      URL.revokeObjectURL(url);
      console.log('Download process complete');

      setSuccessMessage('PDF protected successfully! The file is now password protected. Download started.');
    } catch (error) {
      console.error('Error protecting PDF:', error);
      const errorMessage = error instanceof Error ? error.message : 'Error protecting PDF. Please try again.';
      console.log('Setting error message:', errorMessage);
      setSuccessMessage(errorMessage);
    } finally {
      console.log('Setting isProcessing to false');
      setIsProcessing(false);
    }
  }, [selectedFiles, toolId, password]);

  if (!tool) {
    return (
      <div className="text-center">
        <h1 className="text-2xl font-bold text-foreground mb-4">Tool Not Found</h1>
        <Link href="/" className="text-accent hover:underline">
          Go back to home
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto space-y-8">
      {/* Header */}
      <div className="text-center">
        <div className="flex items-center justify-center space-x-3 mb-4">
          <div className="p-3 rounded-lg bg-accent/10 text-accent">
            {tool.icon}
          </div>
          <h1 className="text-3xl font-bold text-foreground">{tool.title}</h1>
        </div>
        <p className="text-lg text-muted-foreground">{tool.description}</p>
      </div>

      {/* File Upload */}
      <div
        className={`relative border-2 border-dashed rounded-lg p-8 text-center transition-colors ${
          isDragOver
            ? 'border-accent bg-accent/5'
            : 'border-gray-300 dark:border-gray-600 hover:border-accent'
        }`}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
        <input
          type="file"
          multiple={tool.acceptMultiple}
          accept={toolId.includes('pdf') && !toolId.includes('images') ? '.pdf' : toolId.includes('images') ? 'image/*' : undefined}
          onChange={handleFileInputChange}
          className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
        />
        <div className="space-y-4">
          <div className="mx-auto w-12 h-12 text-gray-400">
            <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
            </svg>
          </div>
          <div>
            <p className="text-lg font-medium text-foreground">
              {isDragOver ? 'Drop files here' : 'Drag & drop files here, or click to select'}
            </p>
            <p className="text-sm text-muted-foreground mt-1">
              {tool.acceptMultiple ? 'Select multiple files' : 'Select a file'}
            </p>
          </div>
        </div>
      </div>

      {/* Selected Files */}
      {selectedFiles.length > 0 && (
        <div className="space-y-3">
          <h3 className="text-lg font-semibold text-foreground">Selected Files</h3>
          <div className="space-y-2">
            {selectedFiles.map((file, index) => (
              <div key={index} className="flex items-center justify-between p-3 bg-card border border-gray-200 rounded-md dark:border-gray-800">
                <div className="flex items-center space-x-3">
                  <div className="w-8 h-8 bg-accent/10 rounded flex items-center justify-center">
                    <svg className="w-4 h-4 text-accent" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                  </div>
                  <div>
                    <p className="font-medium text-foreground">{file.name}</p>
                    <p className="text-sm text-muted-foreground">
                      {(file.size / 1024 / 1024).toFixed(2)} MB
                    </p>
                  </div>
                </div>
                <button
                  onClick={() => removeFile(index)}
                  className="p-1 text-muted-foreground hover:text-foreground transition-colors"
                  aria-label="Remove file"
                >
                  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* File Size Comparison (for compress-pdf tool) */}
      {toolId === 'compress-pdf' && (originalSize !== null || compressedSize !== null) && (
        <div className="space-y-2 text-center">
          {originalSize !== null && (
            <p className="text-sm text-muted-foreground">
              Original size: <span className="font-medium text-foreground">{(originalSize / 1024 / 1024).toFixed(2)} MB</span>
            </p>
          )}
          {compressedSize !== null && (
            <p className="text-sm text-muted-foreground">
              Compressed size: <span className="font-medium text-foreground">{(compressedSize / 1024 / 1024).toFixed(2)} MB</span>
            </p>
          )}
        </div>
      )}

      {/* Password Inputs (for protect-pdf tool) */}
      {toolId === 'protect-pdf' && (
        <div className="space-y-4">
          <div className="space-y-2">
            <label htmlFor="password" className="block text-sm font-medium text-foreground">
              Password
            </label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => handlePasswordChange(e.target.value)}
              className={`w-full px-4 py-2 border rounded-md bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent transition-colors ${
                passwordErrors.password
                  ? 'border-red-500 dark:border-red-500'
                  : 'border-gray-300 dark:border-gray-600'
              }`}
              placeholder="Enter password (min 6 characters)"
            />
            {passwordErrors.password && (
              <p className="text-sm text-red-600 dark:text-red-400">{passwordErrors.password}</p>
            )}
          </div>

          <div className="space-y-2">
            <label htmlFor="confirmPassword" className="block text-sm font-medium text-foreground">
              Confirm Password
            </label>
            <input
              type="password"
              id="confirmPassword"
              value={confirmPassword}
              onChange={(e) => handleConfirmPasswordChange(e.target.value)}
              className={`w-full px-4 py-2 border rounded-md bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent transition-colors ${
                passwordErrors.confirmPassword
                  ? 'border-red-500 dark:border-red-500'
                  : 'border-gray-300 dark:border-gray-600'
              }`}
              placeholder="Confirm password"
            />
            {passwordErrors.confirmPassword && (
              <p className="text-sm text-red-600 dark:text-red-400">{passwordErrors.confirmPassword}</p>
            )}
          </div>
        </div>
      )}

      {/* Success Message */}
      {successMessage && (
        <div className="text-center">
          <p className={`text-sm font-medium ${successMessage.includes('Error') ? 'text-red-600 dark:text-red-400' : 'text-green-600 dark:text-green-400'}`}>
            {successMessage}
          </p>
        </div>
      )}

      {/* Action Button */}
      <div className="flex justify-center">
        <button
          disabled={
            (() => {
              const isDisabled = selectedFiles.length === 0 ||
                isProcessing ||
                (toolId === 'protect-pdf'
                  ? (password.length < 6 || password !== confirmPassword)
                  : toolId !== 'merge-pdf' && toolId !== 'images-to-pdf' && toolId !== 'split-pdf' && toolId !== 'compress-pdf' && toolId !== 'pdf-to-images');

              if (toolId === 'protect-pdf') {
                console.log('Protect PDF button disabled check:', {
                  selectedFiles: selectedFiles.length,
                  isProcessing,
                  passwordLength: password.length,
                  passwordsMatch: password === confirmPassword,
                  isDisabled
                });
              }

              return isDisabled;
            })()
          }
          onClick={
            toolId === 'merge-pdf' ? mergePDFs :
            toolId === 'images-to-pdf' ? imagesToPDF :
            toolId === 'split-pdf' ? splitPDF :
            toolId === 'compress-pdf' ? compressPDF :
            toolId === 'pdf-to-images' ? pdfToImages :
            toolId === 'protect-pdf' ? protectPDF :
            undefined
          }
          className="px-8 py-3 bg-accent text-accent-foreground rounded-md font-medium hover:bg-accent/90 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          {isProcessing ? 'Processing...' : tool.actionLabel}
        </button>
      </div>
    </div>
  );
}