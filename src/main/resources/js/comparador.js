/**
 * HotWheels - Funcionalidades del comparador de vehículos
 */

// Asegurar que el código se ejecute cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    console.log('Comparador de vehículos inicializado');

    // BOTÓN: AGREGAR AL COMPARADOR
    const btnAgregar = document.getElementById('btnAgregarComparador');
    if (btnAgregar) {
        btnAgregar.onclick = function() {
            console.log('Clic en Agregar al Comparador');
            const select = document.getElementById('vehiculoSelect');
            const vehiculoId = select.value;

            if (!vehiculoId) {
                alert('Por favor selecciona un vehículo primero');
                return;
            }

            // Obtener vehículos actuales de la URL
            const urlParams = new URLSearchParams(window.location.search);
            const vehiculosActuales = urlParams.getAll('vehiculos');

            // Verificar límite
            if (vehiculosActuales.length >= 3) {
                alert('Máximo 3 vehículos para comparar');
                return;
            }

            // Verificar si ya está agregado
            if (vehiculosActuales.includes(vehiculoId)) {
                alert('Este vehículo ya está en el comparador');
                return;
            }

            // Agregar a la lista
            vehiculosActuales.push(vehiculoId);

            // Redirigir con nuevos parámetros
            const params = vehiculosActuales.map(id => `vehiculos=${id}`).join('&');
            window.location.href = `/comparar?${params}`;
        };
    }

    // BOTÓN: LIMPIAR COMPARADOR
    const btnLimpiar = document.getElementById('btnLimpiarComparador');
    if (btnLimpiar) {
        btnLimpiar.onclick = function() {
            console.log('Clic en Limpiar Comparador');
            window.location.href = '/comparar';
        };
    }

    // BOTÓN: LIMPIAR TODO (al final de la página)
    const btnLimpiarTodo = document.getElementById('btnLimpiarTodo');
    if (btnLimpiarTodo) {
        btnLimpiarTodo.onclick = function() {
            console.log('Clic en Limpiar Todo');
            window.location.href = '/comparar';
        };
    }

    // BOTÓN: IMPRIMIR
    const btnImprimir = document.getElementById('btnImprimirComparacion');
    if (btnImprimir) {
        btnImprimir.onclick = function() {
            console.log('Clic en Imprimir');
            window.print();
        };
    }

    // BOTONES: REMOVER DEL COMPARADOR
    const botonesRemover = document.querySelectorAll('[data-accion="remover-comparador"]');
    botonesRemover.forEach(function(boton) {
        boton.onclick = function() {
            console.log('Clic en Remover Vehículo');
            const vehiculoId = this.getAttribute('data-vehiculo-id');

            // Obtener vehículos actuales de la URL
            const urlParams = new URLSearchParams(window.location.search);
            const vehiculosActuales = urlParams.getAll('vehiculos');

            // Remover el vehículo
            const nuevosVehiculos = vehiculosActuales.filter(id => id !== vehiculoId.toString());

            // Redirigir
            if (nuevosVehiculos.length > 0) {
                const params = nuevosVehiculos.map(id => `vehiculos=${id}`).join('&');
                window.location.href = `/comparar?${params}`;
            } else {
                window.location.href = '/comparar';
            }
        };
    });
});