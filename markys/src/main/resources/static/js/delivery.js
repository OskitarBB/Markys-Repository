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
function pagarClicked(){
    document.getElementById("modalOpciones").style.display = "block";
}

function cerrarModal(){
    document.getElementById("modalOpciones").style.display = "none";
}

function recogerEnTienda(){
    cerrarModal();
    alert("¡Gracias por tu compra! Puedes recoger tu pedido en tienda.");
    vaciarCarrito();
}

function delivery(){
    cerrarModal();
    alert("Lo sentimos, aún no contamos con servicio de delivery.");
    // Si luego decides implementarlo, aquí podrías abrir otro modal/formulario.
}

function vaciarCarrito() {
    var carritoItems = document.getElementsByClassName('carrito-items')[0];
    while (carritoItems.hasChildNodes()) {
        carritoItems.removeChild(carritoItems.firstChild)
    }
    actualizarTotalCarrito();
    ocultarCarrito();
}


// Función que controla el boton clickeado de agregar al carrito
function agregarAlCarritoClicked(event){
    var button = event.target;
    var item = button.parentElement;
    var titulo = item.getElementsByClassName('titulo-item')[0].innerText;
    var precio = item.getElementsByClassName('precio-item')[0].innerText;
    var imagenSrc = item.getElementsByClassName('img-item')[0].src;

    agregarItemAlCarrito(titulo, precio, imagenSrc);
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
function agregarItemAlCarrito(titulo, precio, imagenSrc){
    var item = document.createElement('div');
    item.classList.add('item');
    var itemsCarrito = document.getElementsByClassName('carrito-items')[0];

    // Controlamos que el item no se encuentre ya en el carrito
    var nombresItemsCarrito = itemsCarrito.getElementsByClassName('carrito-item-titulo');
    for(var i=0;i < nombresItemsCarrito.length;i++){
        if(nombresItemsCarrito[i].innerText==titulo){
            alert("El item ya se encuentra en el carrito");
            return;
        }
    }

    var itemCarritoContenido = `
        <div class="carrito-item">
            <img src="${imagenSrc}" width="80px" alt="">
            <div class="carrito-item-detalles">
                <span class="carrito-item-titulo">${titulo}</span>
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
    item.getElementsByClassName('btn-eliminar')[0].addEventListener('click', eliminarItemCarrito);
    item.getElementsByClassName('restar-cantidad')[0].addEventListener('click',restarCantidad);
    item.getElementsByClassName('sumar-cantidad')[0].addEventListener('click',sumarCantidad);

    actualizarTotalCarrito();
}

// Aumento en uno la cantidad del elemento seleccionado
function sumarCantidad(event){
    var buttonClicked = event.target;
    var selector = buttonClicked.parentElement;
    var cantidadActual = selector.getElementsByClassName('carrito-item-cantidad')[0].value;
    cantidadActual++;
    selector.getElementsByClassName('carrito-item-cantidad')[0].value = cantidadActual;
    actualizarTotalCarrito();
}

// Resto en uno la cantidad del elemento seleccionado
function restarCantidad(event){
    var buttonClicked = event.target;
    var selector = buttonClicked.parentElement;
    var cantidadActual = selector.getElementsByClassName('carrito-item-cantidad')[0].value;
    cantidadActual--;
    if(cantidadActual>=1){
        selector.getElementsByClassName('carrito-item-cantidad')[0].value = cantidadActual;
        actualizarTotalCarrito();
    }
}

// Elimino el item seleccionado del carrito
function eliminarItemCarrito(event){
    var buttonClicked = event.target;
    buttonClicked.parentElement.parentElement.remove();
    actualizarTotalCarrito();
    ocultarCarrito();
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