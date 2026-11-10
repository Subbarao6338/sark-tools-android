package omni.toolbox.ui.screens.data

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.viewmodel.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class DocumentItem(
    val fileName: String,
    val fileType: String,
    val bookmarkedPage: Int
)

data class CloudProfile(
    val accountName: String,
    val platform: String,
    val email: String
)

data class ScrapingRule(
    val ruleName: String,
    val targetDomain: String,
    val threadLevels: Int,
    val maxPagesPerThread: Int,
    val extractImages: Boolean,
    val extractVideos: Boolean,
    val extractDocuments: Boolean
)

data class ScrapedThread(
    val threadTitle: String,
    val forumName: String
)

sealed class CrawlerProgress {
    object Idle : CrawlerProgress()
    data class Active(val msg: String, val progress: Float) : CrawlerProgress()
    object Completed : CrawlerProgress()
}

@Composable
fun DocsCrawlerScreen(
    navController: NavHostController,
    @Suppress("UNUSED_PARAMETER") viewModel: OmniViewModel
) {
    var activeTab by remember { mutableIntStateOf(0) }

    val documents = remember {
        mutableStateListOf(
            DocumentItem("research_paper.pdf", "PDF", 12),
            DocumentItem("project_spec.docx", "DOCX", 1),
            DocumentItem("todo_list.txt", "TXT", 1)
        )
    }

    val profiles = remember {
        mutableStateListOf(
            CloudProfile("Subbu Notion", "Notion", "subbu.notion@gmail.com"),
            CloudProfile("My GDrive", "GDrive", "subbu.gdrive@gmail.com")
        )
    }

    val scrapingRules = remember {
        mutableStateListOf(
            ScrapingRule("Wiki Scraper", "wikipedia.org", 2, 50, true, false, true),
            ScrapingRule("Reddit Tech", "old.reddit.com/r/technology", 1, 100, false, false, false)
        )
    }

    val crawledThreads = remember {
        mutableStateListOf(
            ScrapedThread("Jetpack Compose 1.7 Release", "r/androiddev"),
            ScrapedThread("Kotlin Multiplatform in 2026", "r/kotlin")
        )
    }

    var crawlerStatus by remember { mutableStateOf<CrawlerProgress>(CrawlerProgress.Idle) }

    ToolScreen(
        title = "Storage, Docs & Crawlers",
        onBack = { navController.popBackStack() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TabRow(selectedTabIndex = activeTab) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("Documents", modifier = Modifier.padding(12.dp), fontSize = 11.sp)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("Cloud Accounts", modifier = Modifier.padding(12.dp), fontSize = 11.sp)
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Text("Web Scraper", modifier = Modifier.padding(12.dp), fontSize = 11.sp)
                }
                Tab(selected = activeTab == 3, onClick = { activeTab = 3 }) {
                    Text("PDF & Doc Tools", modifier = Modifier.padding(12.dp), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    0 -> DocViewerTab(
                        documents = documents,
                        onAddDocument = { fileName, fileType ->
                            documents.add(DocumentItem(fileName, fileType, 1))
                        }
                    )
                    1 -> CloudAccountsTab(profiles = profiles)
                    2 -> CrawlerScraperTab(
                        crawledThreads = crawledThreads,
                        scrapingRules = scrapingRules,
                        crawlerStatus = crawlerStatus,
                        onClearCrawlerStatus = { crawlerStatus = CrawlerProgress.Idle },
                        onStartCustomWebCrawl = { rule ->
                            crawlerStatus = CrawlerProgress.Active("Starting crawl on ${rule.targetDomain}...", 0.1f)
                        }
                    )
                    3 -> PDFDocToolsTab()
                }
            }
        }
    }
}

@Composable
fun DocViewerTab(documents: List<DocumentItem>, onAddDocument: (String, String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var folderPath by remember { mutableStateOf(android.os.Environment.getExternalStorageDirectory().path + "/Documents") }
    var isIngesting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = folderPath,
                onValueChange = { folderPath = it },
                label = { Text("Local Folder to Ingest") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    isIngesting = true
                    scope.launch {
                        delay(1500)
                        onAddDocument("local_manual.pdf", "PDF")
                        isIngesting = false
                    }
                },
                enabled = !isIngesting
            ) {
                if (isIngesting) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Ingest")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search documents...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(documents.filter { it.fileName.contains(searchQuery, ignoreCase = true) }) { doc ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(doc.fileName, fontWeight = FontWeight.Bold)
                            Text("${doc.fileType} • Page ${doc.bookmarkedPage}", fontSize = 11.sp, color = Color.Gray)
                        }
                        IconButton(onClick = { /* Mock sync */ }) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Sync to Notion")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CloudAccountsTab(profiles: List<CloudProfile>) {
    Column {
        Text("Active Cloud Profiles", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(profiles) { profile ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (profile.platform) {
                                "Notion" -> Icons.Default.Share
                                "GDrive" -> Icons.Default.CloudQueue
                                else -> Icons.Default.FolderZip
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(profile.accountName, fontWeight = FontWeight.Bold)
                            Text("${profile.platform} • ${profile.email}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CrawlerScraperTab(
    crawledThreads: List<ScrapedThread>,
    scrapingRules: List<ScrapingRule>,
    crawlerStatus: CrawlerProgress,
    onClearCrawlerStatus: () -> Unit,
    onStartCustomWebCrawl: (ScrapingRule) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Scraping Rules", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (crawlerStatus != CrawlerProgress.Idle) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Crawler Active", fontWeight = FontWeight.Bold)
                    if (crawlerStatus is CrawlerProgress.Active) {
                        val active = crawlerStatus
                        Text(active.msg, fontSize = 11.sp)
                        LinearProgressIndicator(progress = { active.progress }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                    } else if (crawlerStatus is CrawlerProgress.Completed) {
                        Text("Crawl Completed successfully.", fontSize = 11.sp)
                        Button(onClick = onClearCrawlerStatus) { Text("Dismiss") }
                    }
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(scrapingRules) { rule ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(rule.ruleName, fontWeight = FontWeight.Bold)
                        Text(rule.targetDomain, fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            onStartCustomWebCrawl(rule)
                        }) {
                            Text("Run Crawler")
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)); Text("Scraped History", fontWeight = FontWeight.Bold) }

            items(crawledThreads) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(item.threadTitle, fontWeight = FontWeight.Bold)
                        Text(item.forumName, fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun PDFDocToolsTab() {
    Column {
        Text("PDF & Document Tools", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.AutoMirrored.Filled.CallSplit, contentDescription = null)
                    Text("Split PDF", fontSize = 12.sp)
                }
            }
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.AutoMirrored.Filled.MergeType, contentDescription = null)
                    Text("Merge PDF", fontSize = 12.sp)
                }
            }
        }
    }
}
