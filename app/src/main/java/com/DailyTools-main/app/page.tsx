import Link from 'next/link';

const tools = [
  {
    id: 'merge-pdf',
    title: 'Merge PDF',
    subtitle: 'Combine multiple PDFs into one',
    description: 'Combine multiple PDFs into one document',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
      </svg>
    ),
  },
  {
    id: 'split-pdf',
    title: 'Split PDF',
    subtitle: 'Extract pages into separate files',
    description: 'Extract pages or split PDF into multiple files',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14.828 14.828a4 4 0 01-5.656 0M9 10h1.586a1 1 0 01.707.293l.707.707A1 1 0 0012.414 11H13m-3-3h1.586a1 1 0 01.707.293l.707.707A1 1 0 0012.414 11H13m-3-3V9a2 2 0 114 0v2a2 2 0 01-4 0V9z" />
      </svg>
    ),
  },
  {
    id: 'compress-pdf',
    title: 'Compress PDF',
    subtitle: 'Reduce file size without losing quality',
    description: 'Reduce PDF file size while maintaining quality',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
      </svg>
    ),
  },
  {
    id: 'images-to-pdf',
    title: 'Images to PDF',
    subtitle: 'Convert images into a PDF document',
    description: 'Convert images to PDF format',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
      </svg>
    ),
  },
  {
    id: 'pdf-to-images',
    title: 'PDF to Images',
    subtitle: 'Export each page as an image',
    description: 'Extract images from PDF documents',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
      </svg>
    ),
  },
  {
    id: 'protect-pdf',
    title: 'Protect PDF',
    subtitle: 'Lock your PDF with a password',
    description: 'Add password protection to your PDF',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
      </svg>
    ),
  },
];

export default function Home() {
  return (
    <div className="min-h-screen flex flex-col">
      {/* Hero Section */}
      <div className="text-center py-16 px-4">
        <h1 className="text-6xl font-bold text-foreground mb-6">
          DailyTools
        </h1>
        <p className="text-xl text-muted-foreground mb-4 max-w-3xl mx-auto">
          Fast, private, offline PDF tools in your browser.
        </p>
        <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
          No uploads. No tracking. Everything runs on your device.
        </p>
      </div>

      {/* Tools Grid */}
      <div className="flex-1 px-4 pb-16">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 max-w-6xl mx-auto">
          {tools.map((tool) => (
            <Link key={tool.id} href={`/tools/${tool.id}`}>
              <div className="group p-8 rounded-xl border border-gray-200 bg-card text-card-foreground shadow-sm hover:shadow-xl hover:border-accent transition-all duration-300 cursor-pointer dark:border-gray-800 h-full">
                <div className="flex items-start space-x-4 mb-4">
                  <div className="p-3 rounded-lg bg-accent/10 text-accent group-hover:bg-accent group-hover:text-accent-foreground transition-colors flex-shrink-0">
                    {tool.icon}
                  </div>
                  <div className="flex-1 min-w-0">
                    <h3 className="text-xl font-semibold text-foreground mb-1">{tool.title}</h3>
                    <p className="text-sm font-medium text-accent mb-2">{tool.subtitle}</p>
                    <p className="text-sm text-muted-foreground leading-relaxed">{tool.description}</p>
                  </div>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>

      {/* Footer */}
      <footer className="border-t border-gray-200 dark:border-gray-800 py-8 px-4">
        <div className="text-center">
          <p className="text-sm text-muted-foreground max-w-2xl mx-auto">
            All processing happens locally in your browser. Your files never leave your device.
          </p>
        </div>
      </footer>
    </div>
  );
}
