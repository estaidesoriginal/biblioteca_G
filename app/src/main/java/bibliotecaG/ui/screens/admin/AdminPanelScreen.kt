package bibliotecaG.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
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
    adminViewModel: AdminViewModel // Necesitamos pasar este nuevo ViewModel
) {
    val orders by adminViewModel.orders.collectAsState()
    var showStatusDialog by remember { mutableStateOf<Order?>(null) }

    // Cargar órdenes al entrar
    LaunchedEffect(Unit) {
        adminViewModel.loadOrders()
    }

    if (showStatusDialog != null) {
        StatusChangeDialog(
            order = showStatusDialog!!,
            onDismiss = { showStatusDialog = null },
            onStatusSelected = { newStatus ->
                adminViewModel.updateStatus(showStatusDialog!!.id, newStatus)
                showStatusDialog = null
            }
        )
    }

    Scaffold(
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
                .padding(16.dp)
        ) {
            Text("Gestión de Órdenes", style = MaterialTheme.typography.headlineMedium)
            Text("Total órdenes: ${orders.size}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(Modifier.height(16.dp))

            // Botones de gestión rápida de tienda
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { navController.navigate("addProduct") }, modifier = Modifier.weight(1f)) {
                    Text("Nuevo Producto")
                }
                OutlinedButton(onClick = { navController.navigate("store") }, modifier = Modifier.weight(1f)) {
                    Text("Ir a Tienda")
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(orders) { order ->
                    OrderCard(order = order, onClick = { showStatusDialog = order })
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, onClick: () -> Unit) {
    // Color según estado
    val statusColor = when (order.status.lowercase()) {
        "pagado" -> Color(0xFF4CAF50) // Verde
        "cancelado" -> Color(0xFFF44336) // Rojo
        else -> Color(0xFFFF9800) // Naranja (Pendiente)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("ID: ...${order.id.takeLast(8)}", fontWeight = FontWeight.Bold)
                Text("Usuario: ${order.userId}", style = MaterialTheme.typography.bodySmall)
                Text("Total: $${order.total}", style = MaterialTheme.typography.bodyMedium)
            }

            Surface(
                color = statusColor.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = order.status.uppercase(),
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onClick) {
                Icon(Icons.Default.Edit, "Editar Estado")
            }
        }
    }
}

@Composable
fun StatusChangeDialog(order: Order, onDismiss: () -> Unit, onStatusSelected: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Estado") },
        text = {
            Column {
                Text("Orden: ...${order.id.takeLast(8)}")
                Spacer(Modifier.height(16.dp))
                Button(onClick = { onStatusSelected("pagado") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                    Text("Marcar como PAGADO")
                }
                Button(onClick = { onStatusSelected("pendiente") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) {
                    Text("Marcar como PENDIENTE")
                }
                Button(onClick = { onStatusSelected("cancelado") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))) {
                    Text("Marcar como CANCELADO")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}