package bibliotecaG.ui.screens.store

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bibliotecaG.data.model.Product
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    productToEdit: Product? = null,
    onSave: (Product) -> Unit,
    onBack: () -> Unit
) {
    // Estados del formulario
    var name by remember { mutableStateOf(productToEdit?.name ?: "") }
    var description by remember { mutableStateOf(productToEdit?.description ?: "") }
    var priceStr by remember { mutableStateOf(productToEdit?.price?.toString() ?: "") }
    var category by remember { mutableStateOf(productToEdit?.category ?: "") }
    var stockStr by remember { mutableStateOf(productToEdit?.stock?.toString() ?: "") }
    var imageUrl by remember { mutableStateOf(productToEdit?.imageUrl ?: "") }

    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productToEdit == null) "Nuevo Producto" else "Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Validación básica
                if (name.isBlank() || priceStr.isBlank()) {
                    showError = true
                } else {
                    try {
                        val newProduct = Product(
                            id = productToEdit?.id ?: UUID.randomUUID().toString(),
                            name = name,
                            description = description,
                            price = priceStr.toDouble(),
                            category = category,
                            stock = stockStr.toIntOrNull() ?: 0,
                            imageUrl = imageUrl.ifBlank { null }
                        )
                        onSave(newProduct)
                    } catch (e: NumberFormatException) {
                        showError = true
                    }
                }
            }) {
                Icon(Icons.Default.Check, "Guardar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showError) {
                Text("Error: Verifique nombre y precio", color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nombre del Producto") }, modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = priceStr, onValueChange = { priceStr = it },
                    label = { Text("Precio ($)") }, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = stockStr, onValueChange = { stockStr = it },
                    label = { Text("Stock") }, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            OutlinedTextField(
                value = category, onValueChange = { category = it },
                label = { Text("Categoría (ej. Consolas, Juegos)") }, modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = imageUrl, onValueChange = { imageUrl = it },
                label = { Text("URL de Imagen") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
        }
    }
}