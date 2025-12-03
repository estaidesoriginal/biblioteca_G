package bibliotecaG.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
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
import bibliotecaG.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    navController: NavController,
    adminViewModel: AdminViewModel
) {
    val orders by adminViewModel.orders.collectAsState()
    val error by adminViewModel.error.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    var orderToEdit by remember { mutableStateOf<Order?>(null) }

    LaunchedEffect(Unit) {
        adminViewModel.loadOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { adminViewModel.loadOrders() }) {
                Icon(Icons.Default.Refresh, "Recargar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Detalles") },
                    icon = { Icon(Icons.Default.List, null) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Gestión") },
                    icon = { Icon(Icons.Default.Settings, null) }
                )
            }

            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                if (error != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.width(8.dp))
                            Text("Error: $error", color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                } else if (orders.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No hay órdenes registradas.", color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { adminViewModel.loadOrders() }) {
                            Text("Intentar recargar")
                        }
                    }
                } else {

                    when (selectedTabIndex) {
                        0 -> OrdersDetailPanel(orders)
                        1 -> OrdersManagementPanel(orders, onEditClick = { orderToEdit = it })
                    }
                }
            }
        }
    }

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
}

@Composable
fun OrdersDetailPanel(orders: List<Order>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(orders) { order ->
            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Orden #${order.id.takeLast(8)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Usuario ID: ${order.userId}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        StatusChip(order.status)
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("Productos:", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(4.dp))

                    if (order.items.isEmpty()) {
                        Text("Sin detalles de items.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    } else {
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• Prod ID: ...${item.productId.takeLast(6)} (x${item.quantity})", style = MaterialTheme.typography.bodyMedium)
                                Text("$${item.priceAtPurchase * item.quantity}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text("Total: $${order.total}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
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
                        Text("User: ${order.userId}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("Total: $${order.total}", style = MaterialTheme.typography.bodyMedium)
                    }

                    StatusChip(order.status)

                    IconButton(onClick = { onEditClick(order) }) {
                        Icon(Icons.Default.Edit, "Cambiar")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (color, text) = when (status.lowercase()) {
        "pagado" -> Color(0xFF4CAF50) to "PAGADO"
        "cancelado" -> Color(0xFFF44336) to "CANCELADO"
        else -> Color(0xFFFF9800) to "PENDIENTE"
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusChangeDialog(order: Order, onDismiss: () -> Unit, onStatusSelected: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Estado") },
        text = {
            Column {
                Text("Orden: ...${order.id.takeLast(8)}")
                Text("Estado actual: ${order.status}")
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { onStatusSelected("pagado") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) { Text("Marcar como PAGADO") }

                Button(
                    onClick = { onStatusSelected("pendiente") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) { Text("Marcar como PENDIENTE") }

                Button(
                    onClick = { onStatusSelected("cancelado") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) { Text("Marcar como CANCELADO") }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}