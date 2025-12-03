package bibliotecaG.ui.screens.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import bibliotecaG.data.model.CartItem
import bibliotecaG.ui.viewmodel.AuthViewModel
import bibliotecaG.ui.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    storeViewModel: StoreViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val cartItems by storeViewModel.cart.collectAsState()
    val purchaseSuccessId by storeViewModel.purchaseSuccess.collectAsState()
    val lastTotal by storeViewModel.lastPurchaseTotal.collectAsState()

    var showPaymentDialog by remember { mutableStateOf(false) }

    // --- USUARIO NO AUTENTICADO ---
    if (currentUser == null) {
        GuestCartView(onLoginClick = { navController.navigate("login") })
        return
    }

    // --- CARRITO VACÍO ---
    if (cartItems.isEmpty() && purchaseSuccessId == null) {
        EmptyCartView(onGoToStore = { navController.navigate("store") })
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Carrito") })
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                CartSummaryBar(
                    total = storeViewModel.getTotal(),
                    onCheckoutClick = { showPaymentDialog = true }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems) { item ->
                CartItemCard(
                    item = item,
                    onIncrease = { storeViewModel.increaseQty(item.product) },
                    onDecrease = { storeViewModel.decreaseQty(item.product) },
                    onRemove = { storeViewModel.removeFromCart(item.product) }
                )
            }
        }
    }

    // --- DIALOGO DE PAGO ---
    if (showPaymentDialog) {
        PaymentDialog(
            total = storeViewModel.getTotal(),
            onDismiss = { showPaymentDialog = false },
            onConfirm = {
                showPaymentDialog = false

                // 🔥 INTEGRACIÓN DEL CÓDIGO PEQUEÑO (funciona la compra)
                currentUser?.id?.let { uid ->
                    storeViewModel.confirmPurchase(uid)
                }
            }
        )
    }

    // --- DIALOGO DE COMPRA EXITOSA ---
    purchaseSuccessId?.let { orderId ->
        SuccessDialog(
            orderId = orderId,
            totalPaid = lastTotal,
            onDismiss = { storeViewModel.dismissPurchasePopup() }
        )
    }
}

// =========================================================
//   COMPONENTES VISUALES
// =========================================================

@Composable
fun GuestCartView(onLoginClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
            Spacer(Modifier.height(16.dp))
            Text("Debes iniciar sesión para ver tu carrito", textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onLoginClick) { Text("Iniciar Sesión") }
        }
    }
}

@Composable
fun EmptyCartView(onGoToStore: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Tu carrito está vacío", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onGoToStore) { Text("Ir a la Tienda") }
        }
    }
}

@Composable
fun CartItemCard(item: CartItem, onIncrease: () -> Unit, onDecrease: () -> Unit, onRemove: () -> Unit) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text("Stock: ${item.product.stock}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (item.quantity >= item.product.stock) MaterialTheme.colorScheme.error else Color.Gray
                )
                Text("Unitario: $${item.product.price}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("Subtotal: $${item.total}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Remove, "Disminuir")
                }

                Text("${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)

                IconButton(
                    onClick = onIncrease,
                    modifier = Modifier.size(32.dp),
                    enabled = item.quantity < item.product.stock
                ) {
                    Icon(Icons.Default.Add, "Aumentar")
                }
            }

            Spacer(Modifier.width(8.dp))

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun CartSummaryBar(total: Double, onCheckoutClick: () -> Unit) {
    Surface(shadowElevation = 8.dp, tonalElevation = 2.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total a Pagar", style = MaterialTheme.typography.labelMedium)
                Text("$${total}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onCheckoutClick) { Text("Comprar") }
        }
    }
}

// =========================================================
//   DIALOGOS Y PAGO
// =========================================================

@Composable
fun PaymentDialog(total: Double, onDismiss: () -> Unit, onConfirm: () -> Unit) {

    var selectedOption by remember { mutableStateOf(1) }  // 1=tarjeta, 2=transferencia

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.CreditCard, null) },
        title = { Text("Método de Pago") },
        text = {
            Column {
                Text("Seleccione su método de pago:")
                Spacer(Modifier.height(8.dp))

                // TARJETA
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable { selectedOption = 1 }
                ) {
                    RadioButton(selected = selectedOption == 1, onClick = { selectedOption = 1 })
                    Text("Tarjeta de Crédito/Débito")
                }

                // TRANSFERENCIA
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable { selectedOption = 2 }
                ) {
                    RadioButton(selected = selectedOption == 2, onClick = { selectedOption = 2 })
                    Text("Transferencia")
                }

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(8.dp))
                Text("Monto Total: $$total", fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Confirmar Compra") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun SuccessDialog(orderId: String, totalPaid: Double, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("¡Compra Exitosa!", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text("Tu pedido ha sido procesado correctamente.")
                Spacer(Modifier.height(8.dp))
                Text("Total Pagado: $$totalPaid", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                Text("ID de Compra:", style = MaterialTheme.typography.labelMedium)
                Text(orderId, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(24.dp))

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Continuar")
                }
            }
        }
    }
}
