package bibliotecaG.ui.screens.store

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bibliotecaG.data.model.Product
import bibliotecaG.ui.viewmodel.AuthViewModel
import bibliotecaG.ui.viewmodel.StoreViewModel
import bibliotecaG.ui.viewmodel.UserRoles
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    navController: NavController,
    storeViewModel: StoreViewModel,
    authViewModel: AuthViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }
    val products by storeViewModel.products.collectAsState()
    val currentUserRole by authViewModel.currentUserRole.collectAsState()
    val availableCategories = remember(products) {
        products.flatMap { it.categories }.distinct().sorted()
    }

    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { product ->
            val matchesSearch = searchQuery.isBlank() ||
                    product.name.contains(searchQuery, true) ||
                    product.categories.any { it.contains(searchQuery, true) }

            val matchesCategory = selectedCategory == null ||
                    product.categories.contains(selectedCategory)

            matchesSearch && matchesCategory
        }
    }

    val canManageProducts = currentUserRole == UserRoles.ADMIN || currentUserRole == UserRoles.SELLER

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda Oficial") },
                actions = {
                    if (canManageProducts) {
                        IconButton(onClick = { navController.navigate("addProduct") }) {
                            Icon(Icons.Default.Add, contentDescription = "Nuevo Producto")
                        }
                    }
                }
            )
        },
        floatingActionButton = { }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar productos...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Filtrar por:", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(8.dp))

                Box {
                    OutlinedButton(
                        onClick = { isCategoryMenuExpanded = true }
                    ) {
                        Text(selectedCategory ?: "Todas las categorías")
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowDropDown, null)
                    }

                    DropdownMenu(
                        expanded = isCategoryMenuExpanded,
                        onDismissRequest = { isCategoryMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas") },
                            onClick = {
                                selectedCategory = null
                                isCategoryMenuExpanded = false
                            }
                        )
                        availableCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    isCategoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No se encontraron productos.", style = MaterialTheme.typography.bodyLarge)
                        if (selectedCategory != null) {
                            TextButton(onClick = { selectedCategory = null }) {
                                Text("Limpiar filtros")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredProducts) { product ->
                        ProductCard(
                            product = product,
                            userRole = currentUserRole,
                            canManage = canManageProducts,
                            onAddToCart = { storeViewModel.addToCart(product) },
                            onEdit = { navController.navigate("editProduct/${product.id}") },
                            onDelete = { storeViewModel.deleteProduct(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    userRole: String?,
    canManage: Boolean,
    onAddToCart: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            AsyncImage(
                model = product.imageUrl ?: "https://via.placeholder.com/150",
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(bottom = 12.dp),
                contentScale = ContentScale.Crop
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("$${product.price}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }

            val categoriesText = product.categories?.joinToString(", ") ?: "Sin categoría"
            Text(categoriesText, style = MaterialTheme.typography.labelSmall, color = Color.Gray)

            Spacer(Modifier.height(8.dp))

            Text(product.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (canManage) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                } else if (userRole != UserRoles.MANAGER) {
                    Button(onClick = onAddToCart) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Agregar")
                    }
                } else {
                    Text(
                        "Solo Lectura (Manager)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }

}
