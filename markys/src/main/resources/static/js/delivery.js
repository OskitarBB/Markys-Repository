// Variable que mantiene el estado visible del carrito
var carritoVisible = false;

// Esperamos que todos los elementos de la página carguen para ejecutar el script
if(document.readyState == 'loading'){
    document.addEventListener('DOMContentLoaded', ready)
}else{
    ready();
}

function ready(){
    // Agregamos funcionalidad a los botones eliminar del carrito
    var botonesEliminarItem = document.getElementsByClassName('btn-eliminar');
    for(var i=0;i<botonesEliminarItem.length; i++){
        var button = botonesEliminarItem[i];
        button.addEventListener('click',eliminarItemCarrito);
    }

    // Agrego funcionalidad al boton sumar cantidad
    var botonesSumarCantidad = document.getElementsByClassName('sumar-cantidad');
    for(var i=0;i<botonesSumarCantidad.length; i++){
        var button = botonesSumarCantidad[i];
        button.addEventListener('click',sumarCantidad);
    }

    // Agrego funcionalidad al boton restar cantidad
    var botonesRestarCantidad = document.getElementsByClassName('restar-cantidad');
    for(var i=0;i<botonesRestarCantidad.length; i++){
        var button = botonesRestarCantidad[i];
        button.addEventListener('click',restarCantidad);
    }

    // Agregamos funcionalidad al boton Agregar al carrito
    var botonesAgregarAlCarrito = document.getElementsByClassName('boton-item');
    for(var i=0; i<botonesAgregarAlCarrito.length;i++){
        var button = botonesAgregarAlCarrito[i];
        button.addEventListener('click', agregarAlCarritoClicked);
    }

    // Agregamos funcionalidad al botón comprar
    document.getElementsByClassName('btn-pagar')[0].addEventListener('click',pagarClicked);
}

// Eliminamos todos los elementos del carrito y lo ocultamos
function vaciarCarrito() {
    var carritoItems = document.getElementsByClassName('carrito-items')[0];
    while (carritoItems.hasChildNodes()) {
        carritoItems.removeChild(carritoItems.firstChild)
    }
    actualizarTotalCarrito();
    ocultarCarrito();
    localStorage.removeItem('carrito');
}


// Función que controla el boton clickeado de agregar al carrito
function agregarAlCarritoClicked(event){
    var button = event.target;
    var item = button.parentElement;
    var nombre = item.getElementsByClassName('nombre-item')[0].innerText;
    var precio = item.getElementsByClassName('precio-item')[0].innerText;
    var imagenSrc = item.getElementsByClassName('img-item')[0].src;

    agregarItemAlCarrito(nombre, precio, imagenSrc);
    hacerVisibleCarrito();
}

// Función que hace visible el carrito
function hacerVisibleCarrito(){
    carritoVisible = true;
    var carrito = document.getElementsByClassName('carrito')[0];
    carrito.style.marginRight = '0';
    carrito.style.opacity = '1';

    var items = document.getElementsByClassName('contenedor-items')[0];
    items.style.width = '60%';
}
//Función para filtrar en Delivery
function filtrarDelivery() {
    var input = document.getElementById("filtroDelivery");
    var filtro = input.value.toLowerCase();
    var productos = document.getElementsByClassName("producto-delivery");
    var mensaje = document.getElementById("mensaje-vacio");

    var hayCoincidencias = false;

    for (var i = 0; i < productos.length; i++) {
        var nombre = productos[i].querySelector(".nombre-producto").textContent.toLowerCase();
        if (nombre.includes(filtro)) {
            productos[i].style.display = "";
            hayCoincidencias = true;
        } else {
            productos[i].style.display = "none";
        }
    }

    mensaje.style.display = hayCoincidencias ? "none" : "block";
}

// Función que agrega un item al carrito
function agregarItemAlCarrito(nombre, precio, imagenSrc){
    var item = document.createElement('div');
    item.classList.add('item');
    var itemsCarrito = document.getElementsByClassName('carrito-items')[0];

    // Controlamos que el item no se encuentre ya en el carrito
    var nombresItemsCarrito = itemsCarrito.getElementsByClassName('carrito-item-nombre');
    for(var i=0;i < nombresItemsCarrito.length;i++){
        if(nombresItemsCarrito[i].innerText==nombre){
            alert("El item ya se encuentra en el carrito");
            return;
        }
    }

    var itemCarritoContenido = `
        <div class="carrito-item">
            <img src="${imagenSrc}" width="80px" alt="">
            <div class="carrito-item-detalles">
                <span class="carrito-item-nombre">${nombre}</span>
                <div class="selector-cantidad">
                    <i class="fa-solid fa-minus restar-cantidad"></i>
                    <input type="text" value="1" class="carrito-item-cantidad" disabled>
                    <i class="fa-solid fa-plus sumar-cantidad"></i>
                </div>
                <span class="carrito-item-precio">${precio}</span>
            </div>
            <button class="btn-eliminar">
                <i class="fa-solid fa-trash"></i>
            </button>
        </div>
    `;
    item.innerHTML = itemCarritoContenido;
    itemsCarrito.append(item);

    // Agregamos funcionalidades al nuevo item
    item.getElementsByClassName('btn-eliminar')[0].addEventListener('click', function(event) {
            eliminarItemCarrito(event);
            guardarCarritoEnLocalStorage();
        });
        item.getElementsByClassName('restar-cantidad')[0].addEventListener('click', function(event) {
            restarCantidad(event);
            guardarCarritoEnLocalStorage();
        });
        item.getElementsByClassName('sumar-cantidad')[0].addEventListener('click', function(event) {
            sumarCantidad(event);
            guardarCarritoEnLocalStorage();
        });

        actualizarTotalCarrito();
        guardarCarritoEnLocalStorage();

}

// Aumento en uno la cantidad del elemento seleccionado
function sumarCantidad(event) {
    var buttonClicked = event.target;
    var selector = buttonClicked.parentElement;
    let cantidadActual = parseInt(selector.getElementsByClassName('carrito-item-cantidad')[0].value);
    cantidadActual++;
    selector.getElementsByClassName('carrito-item-cantidad')[0].value = cantidadActual;
    actualizarTotalCarrito();
    guardarCarritoEnLocalStorage();
}

// Resto en uno la cantidad del elemento seleccionado
function restarCantidad(event) {
    var buttonClicked = event.target;
    var selector = buttonClicked.parentElement;
    var cantidadActual = parseInt(selector.getElementsByClassName('carrito-item-cantidad')[0].value);
    cantidadActual--;
    if (cantidadActual >= 1) {
        selector.getElementsByClassName('carrito-item-cantidad')[0].value = cantidadActual;
        actualizarTotalCarrito();
        guardarCarritoEnLocalStorage();
    }
}

// Elimino el item seleccionado del carrito
function eliminarItemCarrito(event) {
    var buttonClicked = event.target;
    buttonClicked.parentElement.parentElement.remove();
    actualizarTotalCarrito();
    ocultarCarrito();
    guardarCarritoEnLocalStorage();
}

// Función que controla si hay elementos en el carrito
function ocultarCarrito(){
    var carritoItems = document.getElementsByClassName('carrito-items')[0];
    if(carritoItems.childElementCount==0){
        var carrito = document.getElementsByClassName('carrito')[0];
        carrito.style.marginRight = '-100%';
        carrito.style.opacity = '0';
        carritoVisible = false;

        var items = document.getElementsByClassName('contenedor-items')[0];
        items.style.width = '100%';
    }
}

// Función para parsear precios correctamente
function parsePrecio(precioStr) {
    // Eliminar todos los símbolos no numéricos excepto puntos y comas
    precioStr = precioStr.replace(/[^\d,.]/g, '');

    // Reemplazar comas por puntos si se usan como decimales
    precioStr = precioStr.replace(',', '.');

    // Si hay más de un punto, eliminar todos excepto el último (que será el decimal)
    const puntos = precioStr.split('.').length - 1;
    if (puntos > 1) {
        precioStr = precioStr.replace(/\./g, '');
        precioStr = precioStr.replace(/(\d+)(\d{2})$/, '$1.$2');
    }

    return parseFloat(precioStr);
}

// Actualizamos el total de Carrito
function actualizarTotalCarrito(){
    var carritoContenedor = document.getElementsByClassName('carrito')[0];
    var carritoItems = carritoContenedor.getElementsByClassName('carrito-item');
    var total = 0;

    for(var i=0; i< carritoItems.length;i++){
        var item = carritoItems[i];
        var precioElemento = item.getElementsByClassName('carrito-item-precio')[0];
        var precio = parsePrecio(precioElemento.innerText);
        var cantidadItem = item.getElementsByClassName('carrito-item-cantidad')[0];
        var cantidad = parseInt(cantidadItem.value);

        console.log(`Procesando: ${precioElemento.innerText} -> ${precio} x ${cantidad}`);
        total = total + (precio * cantidad);
    }

    // Formatear el total siempre con 2 decimales
    document.getElementsByClassName('carrito-precio-total')[0].innerText =
        'S/.' + total.toLocaleString('es-PE', {minimumFractionDigits: 2, maximumFractionDigits: 2});
}
//Opciones en los modales
function pagarClicked() {
    document.getElementById("modalOpciones").style.display = "flex";
}

function cerrarModal() {
    document.getElementById("modalOpciones").style.display = "none";
}

function mostrarModalDireccion() {
    cerrarModal();
    document.getElementById("modalDireccion").style.display = "flex";
}

function cerrarModalDireccion() {
    document.getElementById("modalDireccion").style.display = "none";
}

//Después de continuar con el pago en Recojo
function continuarConPago() {
    const carritoGuardado = JSON.parse(localStorage.getItem('carrito')) || [];

    // Preparamos el carrito sin información innecesaria
    const carritoSinImagen = carritoGuardado.map(({ nombre, precio, cantidad }) => ({
        nombre, precio, cantidad
    }));

    console.log("[DEBUG] JSON enviado:", JSON.stringify({ productos: carritoSinImagen }));

    fetch('/api/pago/preferencia', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ productos: carritoSinImagen })
    })
    // Convertimos la respuesta a JSON
    .then(response => response.json())
    .then(data => {
        // Obtenemos el ID de la preferencia
        const preferenceId = data.preferenceId;


        // Inicializamos MercadoPago con tu public key
        const mp = new MercadoPago('APP_USR-37e01aa5-6862-429d-b13c-b456f59f7cff', { //Acá irá la Public Key del vendedor
            locale: 'es-PE'
        });

        // Limpiamos el contenedor para evitar acumulación de botones
                document.getElementById("mercadopago-button").innerHTML = "";

        // Renderizamos el Checkout Pro embebido en el contenedor "mercadopago-button"
        mp.checkout({
            preference: {
                id: preferenceId
            },
            render: {
                container: '#mercadopago-button', // ID del contenedor donde se mostrará el botón
                label: 'Pagar con Mercado Pago',   // Texto customizable para el botón
            }
        });
    })
    .catch(error => {
        console.error("Error al iniciar el pago:", error);
        alert("Ocurrió un error al redirigir al pago.");
    });
}

function delivery() {
    cerrarModal();
    alert("Lo sentimos, aún no contamos con servicio de delivery.");
}
//CAMBIOS A PARTIR DE ACÁ
//Guardar carrito
function guardarCarritoEnLocalStorage() {
    const carritoItems = document.getElementsByClassName('carrito-item');
    const carrito = [];

    for (let item of carritoItems) {
        const nombre = item.getElementsByClassName('carrito-item-nombre')[0].innerText;
        let precioTexto = item.getElementsByClassName('carrito-item-precio')[0].innerText;
        let precio = parsePrecio(precioTexto);
        const imagen = item.getElementsByTagName('img')[0].src;
        const cantidad = parseInt(item.getElementsByClassName('carrito-item-cantidad')[0].value);

        carrito.push({ nombre: nombre, precio: precio, imagen: imagen, cantidad: cantidad });
    }

    localStorage.setItem('carrito', JSON.stringify(carrito));
}

//Cargar Carrito
function cargarCarritoDesdeLocalStorage() {
    const carrito = JSON.parse(localStorage.getItem('carrito')) || [];
    for (const item of carrito) {
        agregarItemAlCarrito(item.nombre, item.precio, item.imagen);
        // Luego cambia la cantidad a la guardada
        const carritoItems = document.getElementsByClassName('carrito-item');
        const ultimo = carritoItems[carritoItems.length - 1];
        ultimo.getElementsByClassName('carrito-item-cantidad')[0].value = item.cantidad;
    }
    actualizarTotalCarrito();
}

