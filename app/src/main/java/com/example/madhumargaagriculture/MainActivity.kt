package com.example.madhumargaagriculture

import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.*
import androidx.room.Room
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.madhumargaagriculture.data.database.AppDatabase
import com.example.madhumargaagriculture.data.repository.LogRepository
import com.example.madhumargaagriculture.viewmodel.LogViewModel
import com.example.madhumargaagriculture.data.entity.InspectionLog
import com.example.madhumargaagriculture.viewmodel.InspectionViewModel
import com.example.madhumargaagriculture.viewmodel.HarvestViewModel
import com.example.madhumargaagriculture.viewmodel.HiveViewModel
import com.example.madhumargaagriculture.data.entity.HiveEntity
import com.example.madhumargaagriculture.data.entity.HarvestEntity
import com.example.madhumargaagriculture.utils.NotificationHelper
import com.example.madhumargaagriculture.model.AlertItem
import com.example.madhumargaagriculture.auth.FirebaseAuthManager
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.io.File
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.core.content.FileProvider
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid

import androidx.compose.foundation.lazy.itemsIndexed

import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.os.Bundle
import android.os.Build
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Context
import android.app.Activity
import com.example.madhumargaagriculture.ui.theme.MadhuMargaAgricultureTheme

class MainActivity : ComponentActivity() {
    lateinit var db: AppDatabase
    lateinit var repository: LogRepository
    lateinit var viewModel: LogViewModel
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        db = AppDatabase.getDatabase(applicationContext)

        repository = LogRepository(db.logDao())
        viewModel = LogViewModel(repository)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

        enableEdgeToEdge()
        setContent {
            MadhuMargaAgricultureTheme(darkTheme = themeViewModel.isDark.value) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (currentRoute != "splash" && currentRoute != "login") {
                            BottomNav(navController)
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        AppNavigation(
                            context = this@MainActivity,
                            viewModel = viewModel,
                            themeViewModel = themeViewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    context: Context,
    viewModel: LogViewModel,
    themeViewModel: ThemeViewModel,
    navController: NavHostController,
    hiveViewModel: HiveViewModel = viewModel(),
    inspectionViewModel: InspectionViewModel = viewModel(),
    harvestViewModel: HarvestViewModel = viewModel()
) {
    val authManager = remember { FirebaseAuthManager() }
    val startDestination = if (authManager.getCurrentUser() != null) "home" else "splash"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally { it } + fadeIn() },
        exitTransition = { slideOutHorizontally { -it } + fadeOut() },
        popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
        popExitTransition = { slideOutHorizontally { it } + fadeOut() }
    ) {
        composable("splash") {
            SplashScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("hive") {
            HiveRegisterScreen(
                viewModel = viewModel,
                hiveViewModel = hiveViewModel,
                navController = navController,
                context = context
            )
        }

        composable("inspection") {
            InspectionScreen(
                navController = navController,
                viewModel = viewModel,
                inspectionViewModel = inspectionViewModel,
                context = context
            )
        }

        composable("inspection_detail") {
            InspectionDetailScreen(
                navController = navController,
                inspectionViewModel = inspectionViewModel,
                context = context
            )
        }

        composable("harvest") {
            HarvestScreen(harvestViewModel = harvestViewModel)
        }

        composable("flora") {
            FloraScreen(navController = navController)
        }

        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                hiveViewModel = hiveViewModel,
                harvestViewModel = harvestViewModel,
                navController = navController
            )
        }
    }
}

@Composable
fun AnimatedTitle() {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            1f,
            animationSpec = tween(800, easing = EaseOutBack)
        )
        alpha.animateTo(
            1f,
            animationSpec = tween(800)
        )
    }

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF2E7D32), // green
            Color(0xFFFFC107)  // yellow
        )
    )

    Text(
        text = "Madhu Marga 🌱",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .graphicsLayer(
                scaleX = scale.value,
                scaleY = scale.value,
                alpha = alpha.value
            ),
        style = androidx.compose.ui.text.TextStyle(
            brush = gradient
        )
    )
}

@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale),
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(8.dp),
        content = content
    )
}

@Composable
fun TopHeader(
    title: String = "Madhu-Marga",
    showBack: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF2E7D32),
                shape = RoundedCornerShape(
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: LogViewModel
) {
    val logs by viewModel.logs.collectAsState(initial = emptyList())
    val recentLogs = logs.take(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8E9)) // Soft green background
    ) {
        TopHeader(title = "Madhu-Marga", showBack = false)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // Greeting
            Text(
                text = "Hello, Beekeeper! 👋",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1B1B)
            )
            Text(
                text = "Here's your hive overview.",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Overview Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Total Hives", "12", modifier = Modifier.weight(1f))
                StatCard("Active Hives", "9", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Honey (This Year)", "26.7 kg", modifier = Modifier.weight(1f))
                StatCard("Alerts", "2", isAlert = true, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Activity
            Text(
                "Recent Activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (recentLogs.isEmpty()) {
                ActivityItem("No recent activity", "Logs will appear here")
            } else {
                recentLogs.forEach { log ->
                    ActivityItem(log.logText, formatDate(log.timestamp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI Suggestion Box
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Suggestion", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Colony is strong and healthy. Good time for honey harvest.", fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { navController.navigate("dashboard") },
                        modifier = Modifier.height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("View Details", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, isAlert: Boolean = false, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAlert) Color(0xFFFFEBEE) else Color.White
        ),
        modifier = modifier.height(90.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAlert) Color.Red else Color.Black
            )
        }
    }
}

@Composable
fun ActivityItem(title: String, time: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(time, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun BottomNav(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") }
        )
        NavigationBarItem(
            selected = currentRoute == "hive",
            onClick = { navController.navigate("hive") },
            icon = { Icon(Icons.Default.HomeWork, contentDescription = "Hives") },
            label = { Text("Hives") }
        )
        NavigationBarItem(
            selected = currentRoute == "inspection",
            onClick = { navController.navigate("inspection") },
            icon = { Icon(Icons.Default.List, contentDescription = "Logs") },
            label = { Text("Logs") }
        )
        NavigationBarItem(
            selected = currentRoute == "flora",
            onClick = { navController.navigate("flora") },
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
            label = { Text("Calendar") }
        )
        NavigationBarItem(
            selected = currentRoute == "harvest",
            onClick = { navController.navigate("harvest") },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Harvest") },
            label = { Text("Harvest") }
        )
    }
}

// -------- LOCALE --------

fun getLogStatus(text: String): String {
    val lower = text.lowercase()

    return when {
        "healthy" in lower || "good" in lower -> "good"
        "low" in lower || "warning" in lower -> "warning"
        "disease" in lower || "dead" in lower || "problem" in lower -> "critical"
        else -> "normal"
    }
}

fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "good", "yes", "high" -> Color(0xFF2E7D32)     // Green
        "moderate", "warning" -> Color(0xFFF9A825)     // Yellow
        "low", "no", "bad" -> Color(0xFFC62828)        // Red
        else -> Color.Gray
    }
}

fun setLocale(context: Context, langCode: String) {
    val locale = Locale.Builder().setLanguage(langCode).build()
    Locale.setDefault(locale)
    val config = context.resources.configuration
    config.setLocale(locale)
    @Suppress("DEPRECATION")
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

fun formatDate(time: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(time))
}

fun openFile(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "text/csv")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    context.startActivity(intent)
}

fun exportCsv(context: Context, logs: List<InspectionLog>) {
    val fileName = "inspection_logs.csv"
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

    uri?.let {
        resolver.openOutputStream(it)?.bufferedWriter().use { writer ->
            writer?.write("Date,Log\n")
            logs.forEach { log ->
                val date = formatDate(log.timestamp)
                writer?.write("$date,${log.logText}\n")
            }
        }
        Toast.makeText(context, "Opening CSV...", Toast.LENGTH_SHORT).show()
        openFile(context, it)
    } ?: run {
        Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
    }
}

fun exportPdf(context: Context, logs: List<InspectionLog>) {
    val pdfDocument = PdfDocument()
    val paint = Paint()

    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
    val page = pdfDocument.startPage(pageInfo)

    var y = 40

    // Title
    paint.textSize = 18f
    page.canvas.drawText("Madhu Marga - Inspection Report", 40f, y.toFloat(), paint)

    y += 30
    paint.textSize = 12f

    logs.forEach {
        val text = "${formatDate(it.timestamp)} - ${it.logText}"
        page.canvas.drawText(text, 40f, y.toFloat(), paint)
        y += 20
    }

    pdfDocument.finishPage(page)

    val file = File(context.getExternalFilesDir(null), "inspection_report.pdf")
    pdfDocument.writeTo(file.outputStream())

    pdfDocument.close()

    Toast.makeText(context, "PDF saved", Toast.LENGTH_LONG).show()
}

fun shareCsv(context: Context) {
    val file = File(context.getExternalFilesDir(null), "inspection_logs.csv")
    if (!file.exists()) {
        Toast.makeText(context, "CSV file not found. Please export it first.", Toast.LENGTH_SHORT).show()
        return
    }

    val uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share CSV"))
}

@Composable
fun LanguageSelector(context: Context) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = {
                setLocale(context, "en")
                if (context is Activity) context.recreate()
            },
            modifier = Modifier.animateContentSize()
        ) {
            Text(text = "English")
        }

        Button(
            onClick = {
                setLocale(context, "hi")
                if (context is Activity) context.recreate()
            },
            modifier = Modifier.animateContentSize()
        ) {
            Text(text = "हिंदी")
        }

        Button(
            onClick = {
                setLocale(context, "kn")
                if (context is Activity) context.recreate()
            },
            modifier = Modifier.animateContentSize()
        ) {
            Text(text = "ಕನ್ನಡ")
        }
    }
}



@Composable
fun HiveItem(text: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text, fontWeight = FontWeight.Medium)
            }

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.Gray)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            expanded = false
                            onEdit()
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            onDelete()
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiveRegisterScreen(
    viewModel: LogViewModel,
    hiveViewModel: HiveViewModel,
    navController: NavHostController,
    context: Context
) {
    var hiveId by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val hiveList by hiveViewModel.allHives.collectAsState(initial = emptyList())
    var editHive by remember { mutableStateOf<HiveEntity?>(null) }

    Scaffold(
        topBar = {
            TopHeader(
                title = "Hive Register",
                showBack = true,
                onBackClick = { navController.popBackStack() }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF1F8E9))
                .padding(16.dp)
        ) {
            // INPUT SECTION
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        if (editHive == null) "Register New Hive" else "Edit Hive Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = hiveId,
                        onValueChange = { hiveId = it },
                        label = { Text("Hive ID (e.g. Hive #1)") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (hiveId.isNotBlank() && location.isNotBlank()) {
                                if (editHive == null) {
                                    val newHive = HiveEntity(
                                        hiveName = hiveId,
                                        location = location,
                                        queenStatus = "Active",
                                        hiveStrength = "Strong",
                                        notes = ""
                                    )
                                    hiveViewModel.insertHive(newHive)
                                    viewModel.insertLog(InspectionLog(logText = "Registered: $hiveId - $location", timestamp = System.currentTimeMillis()))
                                } else {
                                    hiveViewModel.updateHive(editHive!!.copy(hiveName = hiveId, location = location))
                                }
                                hiveId = ""
                                location = ""
                                editHive = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(if (editHive == null) Icons.Default.Add else Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (editHive == null) "Add Hive" else "Update Hive", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // LIST SECTION
            Text("Registered Hives", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(hiveList) { hive ->
                    HiveItem(
                        text = "${hive.hiveName} - ${hive.location}",
                        onEdit = {
                            hiveId = hive.hiveName
                            location = hive.location
                            editHive = hive
                        },
                        onDelete = {
                            hiveViewModel.deleteHive(hive)
                        }
                    )
                }
            }
        }
    }
}

// -------- INSPECTION --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionDetailScreen(
    navController: NavHostController,
    inspectionViewModel: InspectionViewModel,
    context: Context
) {
    val inspections by inspectionViewModel.allInspections.collectAsState(initial = emptyList())
    // For now showing dummy but with AI analyzer logic
    val dummyQueenSeen = true
    val dummyPestsSeen = false // Changed to false to match "No" in user design
    val dummyHoneyFlow = "Good"
    val dummyColonyLevel = "High" // Changed to match "High" in user design
    val dummyTemperature = 32
    
    val aiSuggestion = HiveHealthAnalyzer.analyzeHive(
        dummyQueenSeen, dummyPestsSeen, dummyHoneyFlow, dummyColonyLevel
    )

    val alerts = HiveHealthAnalyzer.generateAlerts(
        dummyQueenSeen, dummyPestsSeen, dummyTemperature, dummyColonyLevel
    )

    Scaffold(
        topBar = {
            TopHeader(
                title = "Inspection Log",
                showBack = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomNav(navController)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF1F8E9))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // TITLE
            Text("Hive #3 Inspection", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            Text(
                "May 12, 2024 • 09:30 AM",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // QUESTIONS SECTION
            InspectionItem("Queen Seen?", "Yes", Color(0xFF4CAF50))
            InspectionItem("Pests Seen?", "No", Color(0xFFF44336))
            InspectionItem("Honey Flow", "Good", Color(0xFF8BC34A))
            InspectionItem("Activity Level", "High", Color(0xFF4CAF50))

            Spacer(modifier = Modifier.height(20.dp))

            // ALERTS SECTION
            if (alerts.isNotEmpty()) {
                Text("Intervention Alerts", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
                alerts.forEach { alert ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (alert.type == "CRITICAL") Color(0xFFFFEBEE) else Color(0xFFFFF8E1)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (alert.type == "CRITICAL") Color.Red else Color(0xFFF9A825))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(if (alert.type == "CRITICAL") "🚨" else "⚠️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(alert.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                Text(alert.message, fontSize = 12.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // NOTES SECTION
            Text("Notes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Colony is strong and healthy.\nPlenty of brood.",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // AI SUGGESTION CARD
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "AI Suggestion",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = aiSuggestion,
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // SAVE BUTTON
            Button(
                onClick = { 
                    val notificationHelper = NotificationHelper(context)
                    alerts.forEach { alert ->
                        notificationHelper.showNotification(alert.title, alert.message)
                    }
                    navController.popBackStack() 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B5E20)
                )
            ) {
                Text("Save Inspection", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InspectionItem(
    title: String,
    status: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp
        )
        Box(
            modifier = Modifier
                .background(
                    color.copy(alpha = 0.15f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = status,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionScreen(
    navController: NavHostController,
    viewModel: LogViewModel,
    inspectionViewModel: InspectionViewModel,
    context: Context
) {
    var noteText by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }

    val logs by viewModel.logs.collectAsState(initial = emptyList())
    
    val filteredLogs = logs.filter {
        it.logText.contains(searchText, ignoreCase = true)
    }.filter {
        !it.logText.startsWith("Hive Register:") && !it.logText.startsWith("Harvest:")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F7EE))
    ) {
        TopHeader(
            title = "Inspection Logs",
            showBack = true,
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // SEARCH BAR
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search logs...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ADD NOTE CARD
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(5.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "New Inspection Note",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text("e.g. Hive #3 looks healthy...") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (noteText.isNotBlank()) {
                                viewModel.insertLog(
                                    InspectionLog(
                                        logText = noteText,
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                                noteText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Logs", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BUTTONS
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ActionButton("CSV", modifier = Modifier.weight(1f)) { exportCsv(context, logs) }
                ActionButton("PDF", modifier = Modifier.weight(1f)) { exportPdf(context, logs) }
                ActionButton("Share", modifier = Modifier.weight(1f)) { shareCsv(context) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Recent Logs",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // LOGS LIST
            filteredLogs.forEach { log ->
                LogCard(log, viewModel)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ActionButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
        modifier = modifier.height(52.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(text)
    }
}

fun getLogType(log: String): String {
    val text = log.lowercase()
    return when {
        text.contains("good") ||
        text.contains("healthy") ||
        text.contains("strong") -> "Positive"

        text.contains("warning") ||
        text.contains("queen") ||
        text.contains("check") -> "Warning"

        text.contains("bad") ||
        text.contains("low") ||
        text.contains("dead") ||
        text.contains("disease") -> "Negative"

        else -> "Normal"
    }
}

@Composable
fun LogCard(
    log: InspectionLog,
    viewModel: LogViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    val status = getLogType(log.logText)

    val cardColor = when (status) {
        "Positive" -> Color(0xFFE8F5E9)
        "Warning" -> Color(0xFFFFF8E1)
        "Negative" -> Color(0xFFFFEBEE)
        else -> Color(0xFFF5F5F5)
    }

    val borderColor = when (status) {
        "Positive" -> Color(0xFF2E7D32)
        "Warning" -> Color(0xFFF9A825)
        "Negative" -> Color(0xFFC62828)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.5.dp, borderColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(borderColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = log.logText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = status,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier
                        .background(
                            borderColor.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatDate(log.timestamp),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = {
                            expanded = false
                            showDialog.value = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                        onClick = {
                            expanded = false
                            viewModel.deleteLog(log)
                        }
                    )
                }
            }
        }
    }

    if (showDialog.value) {
        EditLogDialog(
            log = log,
            onDismiss = { showDialog.value = false },
            onSave = { newText ->
                viewModel.updateLog(log.copy(logText = newText))
                showDialog.value = false
            }
        )
    }
}

@Composable
fun EditLogDialog(
    log: InspectionLog,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(log.logText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onSave(text)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Edit Log") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

// -------- HARVEST TRACKER --------



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HarvestScreen(harvestViewModel: HarvestViewModel) {

    var hive by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    val harvestList by harvestViewModel.allHarvests.collectAsState(initial = emptyList())

    val totalHoney = harvestList.sumOf { it.quantity }

    Scaffold(
        containerColor = Color(0xFFF6FAF2),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Harvest Tracker 🍯",
                        fontSize = 20.sp,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // ADD RECORD CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        "Add Harvest Record",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = hive,
                        onValueChange = { hive = it },
                        label = { Text("Hive Name") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Honey Quantity (kg)") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {

                            if (hive.isNotEmpty() && quantity.isNotEmpty()) {

                                harvestViewModel.insertHarvest(
                                    HarvestEntity(
                                        hiveId = 0, // In a real app, you'd select a hive ID
                                        date = formatDate(System.currentTimeMillis()),
                                        quantity = quantity.toDoubleOrNull() ?: 0.0,
                                        honeyType = "Multi-floral"
                                    )
                                )

                                hive = ""
                                quantity = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {

                        Icon(
                            Icons.Default.Add,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Add Record", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // HARVEST LIST
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(harvestList) { record ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF1F8E9)
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column {

                                Text(
                                    text = record.date,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Hive ID: ${record.hiveId}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "${record.quantity} kg",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // TOTAL CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFDCEFD8)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        "Total Honey Collected",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "${String.format("%.1f", totalHoney)} kg",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------- LOGIN --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val authManager = remember { FirebaseAuthManager() }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopHeader(title = "Login")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.bee_logo),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            
            Spacer(Modifier.height(20.dp))
            
            Text(
                "Welcome Back",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(Modifier.height(30.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(20.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF2E7D32))
            } else {
                AnimatedButton(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            authManager.loginUser(
                                email, password,
                                onSuccess = {
                                    isLoading = false
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onFailure = {
                                    isLoading = false
                                    error = it
                                }
                            )
                        } else {
                            error = "Please fill all fields"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Login", fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(10.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text("Don't have an account? Register")
            }
            
            TextButton(onClick = { 
                if (email.isNotBlank()) {
                    authManager.resetPassword(email, 
                        onSuccess = { error = "Reset email sent!" },
                        onFailure = { error = it }
                    )
                } else {
                    error = "Enter email for reset"
                }
            }) {
                Text("Forgot Password?")
            }

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val authManager = remember { FirebaseAuthManager() }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopHeader(title = "Register", showBack = true, onBackClick = { navController.popBackStack() })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Create Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(Modifier.height(30.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(20.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF2E7D32))
            } else {
                AnimatedButton(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            authManager.registerUser(
                                email, password,
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onFailure = {
                                    isLoading = false
                                    error = it
                                }
                            )
                        } else {
                            error = "Please fill all fields"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Register", fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(10.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("Already have an account? Login")
            }

            if (error.isNotEmpty()) {
                Text(error, color = Color.Red)
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    val authManager = remember { FirebaseAuthManager() }

    LaunchedEffect(true) {
        delay(2500)
        val startDestination = if (authManager.getCurrentUser() != null) "home" else "login"
        navController.navigate(startDestination) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF6E3)) // cream background
    ) {

        // 🌼 Bottom Image (flowers)
        Image(
            painter = painterResource(id = R.drawable.splash_bottom),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )

        // 🌟 Center Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🐝 Bee Image
            Image(
                painter = painterResource(id = R.drawable.bee_logo),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🟩 Title
            Text(
                text = "Madhu-Marga",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // 🟩 Subtitle
            Text(
                text = "Digital Beekeeper's Diary",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 🟩 Progress bar
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.6f),
                color = Color(0xFF4CAF50)
            )
        }
    }
}

// -------- FLORA --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloraScreen(navController: NavHostController) {
    val calendar = remember { Calendar.getInstance() }
    val monthName = remember(calendar) { 
        SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time) 
    }
    val year = remember(calendar) { calendar.get(Calendar.YEAR) }
    
    var selectedDay by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }

    Scaffold(
        topBar = {
            TopHeader(
                title = stringResource(id = R.string.flora_calendar),
                showBack = true,
                onBackClick = { navController.popBackStack() }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF1F8E9)) // Soft green background
        ) {
            // MONTH HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Previous month logic */ }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
                }

                Text(
                    text = "$monthName $year",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                IconButton(onClick = { /* Next month logic */ }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
                }
            }

            // CALENDAR GRID
            CalendarView(selectedDay) {
                selectedDay = it
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BLOOMING SECTION
            Text(
                "Blooming Nearby",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF2E7D32)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { FloraItem("Sunflower", "Helianthus annuus", R.drawable.ic_flower) }
                item { FloraItem("Mustard", "Brassica juncea", R.drawable.ic_flower) }
                item { FloraItem("Neem", "Azadirachta indica", R.drawable.ic_flower) }
                item { FloraItem("Coriander", "Coriandrum sativum", R.drawable.ic_flower) }
            }
        }
    }
}

@Composable
fun CalendarView(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    val days = (1..31).toList()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .height(260.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        items(days.size) { index ->
            val day = days[index]
            val isSelected = day == selectedDay

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFF2E7D32) else Color.Transparent)
                    .clickable {
                        onDaySelected(day)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    color = if (isSelected) Color.White else Color.Black,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun FloraItem(name: String, scientific: String, icon: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(scientific, fontSize = 12.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        }
    }
}
