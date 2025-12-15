* App: Biblioteca_G
* Integrantes: Ariel Ben Lulu - Marcelo Novoa
* Funcionalidades: Biblioteca de Juegos con E-commerce con interfaz personalizable y adaptable segun el rol del usuario

-User: posee acceso al catalogo y biblioteca, puede crear,editar y borrar juegos (menos lo que esten protegidos por administrador)

-Seller: posee acceso a la creacion de productos para la tienda

-Manager: posee acceso al panel "Gestion" donde este puede ver la cantidad de productos y juegos que hay,resumenes de ventas, detalles de compras y actualizar el estado de una compra

-Admin: posee acceso a todas las funcionalidades de la app, ademas de un nuevo panel de gestion de usuarios,donde este puede modificar el rol o borrar de un usuario

* Instrucciones para ejecucion:
ingresar al link https://api-biblioteca-apgn.onrender.com para 'prender' la api, luego 

* Ubicacion de la APK:
https://drive.google.com/drive/folders/1Wr7eM-dzILV9UkXuC7qN5djVmycGKx57?usp=sharing

* Ubicacion de la API:
https://github.com/estaidesoriginal/api_biblioteca

* Endpoints:
- 👤 Usuarios: /usuarios -
POST /usuarios/login: Inicia sesión y devuelve token
POST /usuarios/registro: Registra un nuevo usuario (con Rol USER)

GET /usuarios: Lista todos los usuarios registrados 
DELETE /usuarios/{id}: Elimina un usuario permanentemente 
PUT /usuarios/{id}/rol: Cambia el rol de un usuario 

- 🎮 Juegos: /juegos -
GET /juegos: Obtiene el catálogo completo
GET /juegos/{id}: Obtiene detalle de un juego

POST /juegos: Crea un nuevo juego
PUT /juegos/{id}: Actualiza datos de un juego
DELETE /juegos/{id}: Elimina un juego

- 🛍️ Productos:/productos -
GET /productos: Lista productos disponibles
POST /productos: Crea producto con categorías y stock
PUT /productos/{id}: Actualiza producto e inventario
DELETE /productos/{id}: Elimina un producto (validado)

- 🧾 Compras: /compras -
POST /compras: Genera una orden de compra (y calcula total)
GET /compras: Historial completo de las ventas 
PUT /compras/{id}/estado: ,"Actualiza el estado de la compra estado 





