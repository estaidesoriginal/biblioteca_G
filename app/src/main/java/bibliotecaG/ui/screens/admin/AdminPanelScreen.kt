package bibliotecaG.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertChartOutlined
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditAttributes
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InsertChartOutlined
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bibliotecaG.data.model.Order
import bibliotecaG.data.model.User
import bibliotecaG.ui.viewmodel.AdminViewModel
import bibliotecaG.ui.viewmodel.GameViewModel
import bibliotecaG.ui.viewmodel.StoreViewModel
import bibliotecaG.ui.viewmodel.UserRoles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    navController: NavController,
    adminViewModel: AdminViewModel,
    gameViewModel: GameViewModel,   // Nuevo: Para contar juegos
    storeViewModel: StoreViewModel, // Nuevo: Para contar productos
    currentUserRole: String?
) {
    // Recolectamos datos de todos los ViewModels para el resumen
    val orders by adminViewModel.orders.collectAsState()
    val users by adminViewModel.users.collectAsState()
    val games by gameViewModel.games.collectAsState()
    val products by storeViewModel.products.collectAsState()

    val error by adminViewModel.error.collectAsState()

    // Pestañas: 0=Resumen, 1=Detalles, 2=Estados, 3=Roles (Solo Admin)
    var selectedTabIndex by remember { mutableStateOf(0) }

    var orderToEdit by remember { mutableStateOf<Order?>(null) }
    var userToEdit by remember { mutableStateOf<User?>(null) }
    var userToDelete by remember { mutableStateOf<User?>(null) }

    // Solo ADMIN puede ver la pestaña de roles
    val showRolesTab = currentUserRole == UserRoles.ADMIN

    LaunchedEffect(Unit) {
        adminViewModel.loadOrders()
        // Aseguramos que los otros datos estén frescos para el contador
        gameViewModel.refreshGames()
        storeViewModel.loadProducts()

        if (showRolesTab) {
            adminViewModel.loadUsers()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                adminViewModel.loadOrders()
                gameViewModel.refreshGames()
                storeViewModel.loadProducts()
                if (showRolesTab) adminViewModel.loadUsers()
            }) {
                Icon(Icons.Default.Refresh, "Recargar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // --- ACCIONES DE PRODUCTO (Solo Admin) ---
            if (currentUserRole == UserRoles.ADMIN) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("addProduct") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Crear Producto")
                        }
                        OutlinedButton(
                            onClick = { navController.navigate("store") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Ver Tienda")
                        }
                    }
                }
            }

            // --- PESTAÑAS ---
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                // Tab 0: Resumen (Nuevo)
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Resumen") },
                    icon = { Icon(Icons.Default.InsertChartOutlined, null) },
                    selectedContentColor = MaterialTheme.colorScheme.primary
                )
                // Tab 1: Detalles
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Detalles  Compras") },
                    icon = { Icon(Icons.Default.Info, null) }, // Usamos IconList (List)
                    selectedContentColor = MaterialTheme.colorScheme.primary
                )
                // Tab 2: Estados
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Estados  Compras") },
                    icon = { Icon(Icons.Default.EditAttributes, null) },
                    selectedContentColor = MaterialTheme.colorScheme.primary
                )
                // Tab 3: Roles (Solo visible para ADMIN)
                if (showRolesTab) {
                    Tab(
                        selected = selectedTabIndex == 3,
                        onClick = { selectedTabIndex = 3 },
                        text = { Text("Roles  Usuarios") },
                        icon = { Icon(Icons.Default.Group, null) },
                        selectedContentColor = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Indicador visual para Roles
            if (selectedTabIndex == 3) {
                LinearProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.fillMaxWidth().height(2.dp),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            // --- CONTENIDO ---
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                if (error != null) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                } else {
                    when (selectedTabIndex) {
                        0 -> SummaryPanel(
                            gamesCount = games.size,
                            productsCount = products.size,
                            orders = orders
                        )
                        1 -> {
                            if (orders.isEmpty()) EmptyState("No hay órdenes.")
                            else OrdersDetailPanel(orders)
                        }
                        2 -> {
                            if (orders.isEmpty()) EmptyState("No hay órdenes.")
                            else OrdersManagementPanel(orders, onEditClick = { orderToEdit = it })
                        }
                        3 -> {
                            if (showRolesTab) {
                                if (users.isEmpty()) EmptyState("No hay usuarios.")
                                else UsersManagementPanel(
                                    users = users,
                                    onEditRole = { userToEdit = it },
                                    onDelete = { userToDelete = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIÁLOGOS ---

    if (orderToEdit != null) {
        StatusChangeDialog(
            order = orderToEdit!!,
            onDismiss = { orderToEdit = null },
            onStatusSelected = { newStatus ->
                adminViewModel.updateStatus(orderToEdit!!.id, newStatus)
                orderToEdit = null
            }
        )
    }

    if (userToEdit != null) {
        RoleChangeDialog(
            user = userToEdit!!,
            onDismiss = { userToEdit = null },
            onRoleSelected = { newRole ->
                adminViewModel.changeUserRole(userToEdit!!.id, newRole)
                userToEdit = null
            }
        )
    }

    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Eliminar Usuario") },
            text = { Text("¿Eliminar a ${userToDelete?.name} permanentemente?") },
            confirmButton = {
                Button(
                    onClick = {
                        adminViewModel.deleteUser(userToDelete!!.id)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { userToDelete = null }) { Text("Cancelar") } }
        )
    }
}

// --- PANEL #0: RESUMEN (NUEVO) ---
@Composable
fun SummaryPanel(
    gamesCount: Int,
    productsCount: Int,
    orders: List<Order>
) {
    val totalSales = orders.size
    val paidSales = orders.count { it.status.equals("pagado", ignoreCase = true) }
    val pendingSales = orders.count { it.status.equals("pendiente", ignoreCase = true) }
    val canceledSales = orders.count { it.status.equals("cancelado", ignoreCase = true) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Estadísticas Generales", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(title = "Juegos", value = gamesCount.toString(), modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primaryContainer)
            StatCard(title = "Productos", value = productsCount.toString(), modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.secondaryContainer)
        }

        Divider()

        Text("Resumen de Ventas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(title = "Total Ventas", value = totalSales.toString(), modifier = Modifier.weight(1f))
            StatCard(title = "Pagadas", value = paidSales.toString(), modifier = Modifier.weight(1f), color = Color(0xFFE8F5E9)) // Fondo verde claro
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(title = "Pendientes", value = pendingSales.toString(), modifier = Modifier.weight(1f), color = Color(0xFFFFF3E0)) // Fondo naranja claro
            StatCard(title = "Canceladas", value = canceledSales.toString(), modifier = Modifier.weight(1f), color = Color(0xFFFFEBEE)) // Fondo rojo claro
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.surfaceVariant) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Composable
fun EmptyState(msg: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(msg, color = Color.Gray)
    }
}

// --- PANELES DE PEDIDOS ---

@Composable
fun OrdersDetailPanel(orders: List<Order>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(orders) { order ->
            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Orden #${order.id.takeLast(8)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Usuario ID: ${order.userId}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        StatusChip(order.status)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    if (order.items.isEmpty()) {
                        Text("Sin detalles.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        order.items.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("• ...${item.productId.takeLast(6)} (x${item.quantity})", style = MaterialTheme.typography.bodyMedium)
                                Text("$${item.priceAtPurchase * item.quantity}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text("Total: $${order.total}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun OrdersManagementPanel(orders: List<Order>, onEditClick: (Order) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(orders) { order ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onEditClick(order) },
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Orden #${order.id.takeLast(8)}", fontWeight = FontWeight.Bold)
                        Text("User: ${order.userId}", style = MaterialTheme.typography.bodySmall)
                        Text("Total: $${order.total}", style = MaterialTheme.typography.bodyMedium)
                    }
                    StatusChip(order.status)
                    IconButton(onClick = { onEditClick(order) }) {
                        Icon(Icons.Default.Edit, "Editar")
                    }
                }
            }
        }
    }
}

// --- PANEL DE USUARIOS (Corregido) ---

@Composable
fun UsersManagementPanel(users: List<User>, onEditRole: (User) -> Unit, onDelete: (User) -> Unit) {
    // FILTRADO: Excluimos a los administradores de la lista para protegerlos
    val filteredUsers = users.filter { it.role != UserRoles.ADMIN }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filteredUsers) { user ->
            // Corrección: Usamos llaves para pasar el usuario a las funciones callback
            UserRow(
                user = user,
                onEditRole = { onEditRole(user) },
                onDelete = { onDelete(user) }
            )
        }
    }
}

@Composable
fun UserRow(user: User, onEditRole: () -> Unit, onDelete: () -> Unit) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold)
                Text(user.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                RoleChip(user.role)
            }
            Row {
                IconButton(onClick = onEditRole) {
                    Icon(Icons.Default.Edit, "Rol", tint = MaterialTheme.colorScheme.tertiary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Borrar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// --- UTILS & HELPERS ---

@Composable
fun StatusChip(status: String) {
    val (color, text) = when (status.lowercase()) {
        "pagado" -> Color(0xFF4CAF50) to "PAGADO"
        "cancelado" -> Color(0xFFF44336) to "CANCELADO"
        else -> Color(0xFFFF9800) to "PENDIENTE"
    }
    Surface(color = color.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small) {
        Text(text, color = color, modifier = Modifier.padding(6.dp, 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RoleChip(role: String) {
    val color = getRoleColor(role) // Usamos la función auxiliar
    Surface(color = color.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small) {
        Text(role, color = color, modifier = Modifier.padding(6.dp, 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

fun getRoleColor(role: String): Color {
    return when (role) {
        UserRoles.ADMIN -> Color(0xFFD32F2F)
        UserRoles.MANAGER -> Color(0xFF1976D2)
        UserRoles.SELLER -> Color(0xFF388E3C)
        else -> Color.Gray
    }
}

@Composable
fun StatusChangeDialog(order: Order, onDismiss: () -> Unit, onStatusSelected: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Estado Orden #${order.id.takeLast(8)}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onStatusSelected("pagado") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { Text("PAGADO") }
                Button(onClick = { onStatusSelected("pendiente") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) { Text("PENDIENTE") }
                Button(onClick = { onStatusSelected("cancelado") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))) { Text("CANCELADO") }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

@Composable
fun RoleChangeDialog(user: User, onDismiss: () -> Unit, onRoleSelected: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rol para ${user.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // EXCLUSIÓN: No permitimos asignar el rol ADMIN
                listOf(UserRoles.USER, UserRoles.SELLER, UserRoles.MANAGER).forEach { role ->
                    Button(
                        onClick = { onRoleSelected(role) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (role == user.role) Color.Gray else MaterialTheme.colorScheme.tertiary
                        )
                    ) { Text(role) }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}