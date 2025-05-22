// HotWheels - JavaScript principal

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar componentes
    inicializarCarrito();
    inicializarAlertas();
    inicializarAnimaciones();

    console.log('🚗 HotWheels - Sistema iniciado correctamente');
});

// ============================================
// GESTIÓN DEL CARRITO
// ============================================

function inicializarCarrito() {
    actualizarContadorCarrito();

    // Agregar event listeners a botones de carrito
    const botonesAgregar = document.querySelectorAll('[data-accion="agregar-carrito"]');
    botonesAgregar.forEach(boton => {
        boton.addEventListener('click', function(e) {
            e.preventDefault();
            const vehiculoId = this.dataset.vehiculoId;
            agregarAlCarrito(vehiculoId);
        });
    });
}

function actualizarContadorCarrito() {
    fetch('/carrito/api/contador')
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al obtener contador');
            }
            return response.json();
        })
        .then(data => {
            const contador = document.getElementById('carrito-contador');
            if (contador) {
                contador.textContent = data;
                contador.style.display = data > 0 ? 'inline' : 'none';
            }
        })
        .catch(error => {
            console.log('Info: Usuario no logueado o error en contador de carrito');
        });
}

function agregarAlCarrito(vehiculoId) {
    const boton = document.querySelector(`[data-vehiculo-id="${vehiculoId}"]`);
    if (!boton) {
        console.error('Botón no encontrado para vehículo ID:', vehiculoId);
        return;
    }

    const textoOriginal = boton.innerHTML;

    // Mostrar estado de carga
    boton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Agregando...';
    boton.disabled = true;

    fetch(`/carrito/api/agregar/${vehiculoId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text || 'Error al agregar al carrito. Verifica si estás autenticado.');
                });
            }
            return response.text();
        })
        .then(data => {
            console.log('Éxito:', data);
            actualizarContadorCarrito();
            mostrarMensaje('success', data);

            // Cambiar estado del botón
            boton.innerHTML = '<i class="fas fa-check"></i> Agregado';
            boton.classList.remove('btn-primary');
            boton.classList.add('btn-success');

            // Restaurar botón después de 2 segundos
            setTimeout(() => {
                boton.innerHTML = textoOriginal;
                boton.classList.remove('btn-success');
                boton.classList.add('btn-primary');
                boton.disabled = false;
            }, 2000);
        })
        .catch(error => {
            console.error('Error al agregar al carrito:', error);
            mostrarMensaje('error', error.message || 'Error al agregar al carrito. ¿Has iniciado sesión?');

            // Restaurar botón
            boton.innerHTML = textoOriginal;
            boton.disabled = false;
        });
}

// ============================================
// SISTEMA DE MENSAJES
// ============================================

function inicializarAlertas() {
    // Auto-ocultar alertas después de 5 segundos
    const alertas = document.querySelectorAll('.alert');
    alertas.forEach(alerta => {
        setTimeout(() => {
            if (alerta && alerta.parentNode) {
                alerta.style.opacity = '0';
                setTimeout(() => {
                    if (alerta.parentNode) {
                        alerta.remove();
                    }
                }, 300);
            }
        }, 5000);
    });
}

function mostrarMensaje(tipo, mensaje) {
    // Remover alertas existentes del mismo tipo
    const alertasExistentes = document.querySelectorAll(`.alert-${tipo === 'success' ? 'success' : 'danger'}`);
    alertasExistentes.forEach(alerta => alerta.remove());

    // Crear nueva alerta
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${tipo === 'success' ? 'success' : 'danger'} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        <i class="fas fa-${tipo === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    // Buscar un contenedor válido para insertar la alerta
    const container = document.querySelector('.container');
    if (container) {
        // Insertar al inicio del contenedor
        if (container.firstChild) {
            container.insertBefore(alertDiv, container.firstChild);
        } else {
            container.appendChild(alertDiv);
        }

        // Auto-remover después de 5 segundos
        setTimeout(() => {
            if (alertDiv && alertDiv.parentNode) {
                alertDiv.style.opacity = '0';
                setTimeout(() => {
                    if (alertDiv.parentNode) {
                        alertDiv.remove();
                    }
                }, 300);
            }
        }, 5000);
    } else {
        // Si no hay contenedor, mostrar en consola
        console.error('No se pudo mostrar mensaje:', tipo, mensaje);
    }
}

// ============================================
// ANIMACIONES Y UX
// ============================================

function inicializarAnimaciones() {
    // Agregar animación fade-in a cards cuando aparecen en viewport
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('fade-in');
                observer.unobserve(entry.target);
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });

    const cards = document.querySelectorAll('.card');
    cards.forEach(card => {
        observer.observe(card);
    });
}

// ============================================
// UTILIDADES
// ============================================

function formatearPrecio(precio) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    }).format(precio);
}

function formatearNumero(numero) {
    return new Intl.NumberFormat('es-CO').format(numero);
}

function confirmarAccion(mensaje, callback) {
    if (confirm(mensaje)) {
        callback();
    }
}

// ============================================
// BÚSQUEDA Y FILTROS
// ============================================

function actualizarModelos(marcaId) {
    const selectModelo = document.getElementById('modeloId');
    if (!selectModelo) return;

    // Limpiar opciones existentes
    selectModelo.innerHTML = '<option value="">Todos los modelos</option>';

    if (!marcaId) return;

    // Mostrar loading
    selectModelo.innerHTML = '<option value="">Cargando...</option>';
    selectModelo.disabled = true;

    fetch(`/vehiculos/api/modelos/${marcaId}`)
        .then(response => response.json())
        .then(modelos => {
            selectModelo.innerHTML = '<option value="">Todos los modelos</option>';
            modelos.forEach(modelo => {
                const option = document.createElement('option');
                option.value = modelo.id;
                option.textContent = modelo.nombre;
                selectModelo.appendChild(option);
            });
            selectModelo.disabled = false;
        })
        .catch(error => {
            selectModelo.innerHTML = '<option value="">Error al cargar modelos</option>';
            selectModelo.disabled = false;
        });
}

// ============================================
// COMPARADOR DE VEHÍCULOS
// ============================================

class Comparador {
    constructor() {
        this.vehiculosSeleccionados = new Set();
        this.maxVehiculos = 3;
    }

    agregar(vehiculoId) {
        if (this.vehiculosSeleccionados.has(vehiculoId)) {
            mostrarMensaje('error', 'Este vehículo ya está en el comparador');
            return false;
        }

        if (this.vehiculosSeleccionados.size >= this.maxVehiculos) {
            mostrarMensaje('error', `Máximo ${this.maxVehiculos} vehículos para comparar`);
            return false;
        }

        this.vehiculosSeleccionados.add(vehiculoId);
        this.actualizarUI();
        mostrarMensaje('success', 'Vehículo agregado al comparador');
        return true;
    }

    remover(vehiculoId) {
        this.vehiculosSeleccionados.delete(vehiculoId);
        this.actualizarUI();
        mostrarMensaje('success', 'Vehículo removido del comparador');
    }

    actualizarUI() {
        const contador = document.getElementById('comparador-contador');
        const boton = document.getElementById('btn-comparar');

        if (contador) {
            contador.textContent = this.vehiculosSeleccionados.size;
            contador.style.display = this.vehiculosSeleccionados.size > 0 ? 'inline' : 'none';
        }

        if (boton) {
            boton.disabled = this.vehiculosSeleccionados.size === 0;
        }

        // Actualizar botones de vehículos
        document.querySelectorAll('[data-accion="agregar-comparador"]').forEach(btn => {
            const vehiculoId = parseInt(btn.dataset.vehiculoId);
            if (this.vehiculosSeleccionados.has(vehiculoId)) {
                btn.innerHTML = '<i class="fas fa-check"></i> En comparador';
                btn.classList.remove('btn-outline-warning');
                btn.classList.add('btn-warning');
            } else {
                btn.innerHTML = '<i class="fas fa-balance-scale"></i> Comparar';
                btn.classList.remove('btn-warning');
                btn.classList.add('btn-outline-warning');
            }
        });
    }

    irAComparador() {
        if (this.vehiculosSeleccionados.size === 0) {
            mostrarMensaje('error', 'Selecciona al menos un vehículo para comparar');
            return;
        }

        const params = Array.from(this.vehiculosSeleccionados)
            .map(id => `vehiculos=${id}`)
            .join('&');

        window.location.href = `/comparar?${params}`;
    }
}

// Instancia global del comparador
window.comparador = new Comparador();

// ============================================
// EVENTOS GLOBALES
// ============================================

// Manejar clicks en botones con data-accion
document.addEventListener('click', function(e) {
    const accion = e.target.closest('[data-accion]');
    if (!accion) return;

    const tipoAccion = accion.dataset.accion;
    const vehiculoId = parseInt(accion.dataset.vehiculoId);

    switch (tipoAccion) {
        case 'agregar-carrito':
            e.preventDefault();
            agregarAlCarrito(vehiculoId);
            break;

        case 'agregar-comparador':
            e.preventDefault();
            if (window.comparador.vehiculosSeleccionados.has(vehiculoId)) {
                window.comparador.remover(vehiculoId);
            } else {
                window.comparador.agregar(vehiculoId);
            }
            break;
    }
});

// Manejar cambios en select de marcas
document.addEventListener('change', function(e) {
    if (e.target.id === 'marcaId') {
        actualizarModelos(e.target.value);
    }
});