/* ==========================================================================
   BarberManager — JS común del frontend
   - Validación visual de formularios (la validación real la hace el backend).
   - Mostrar/ocultar contraseña.
   - Evitar doble envío de formularios.
   Autor: Gerald (frontend)
   ========================================================================== */
(function () {
  'use strict';

  var REGLAS = {
    // Reglas extra que se activan con data-regla="..."
    telefono: {
      prueba: function (v) { return v === '' || /^9\d{8}$/.test(v); },
      mensaje: 'Ingresa un celular de 9 dígitos que empiece con 9.'
    },
    username: {
      prueba: function (v) { return /^[A-Za-z0-9._]{4,50}$/.test(v); },
      mensaje: 'Usa de 4 a 50 caracteres: letras, números, punto o guion bajo.'
    },
    nombre: {
      prueba: function (v) { return /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' -]{2,100}$/.test(v); },
      mensaje: 'Solo letras y espacios (mínimo 2 caracteres).'
    },

    password: {
      prueba: function (v) {
        return v.length >= 12 &&
            v.length <= 72 &&
            /[A-Za-z]/.test(v) &&
            /\d/.test(v);
      },
      mensaje: 'Usa entre 12 y 72 caracteres, con al menos una letra y un número.'
    }
  };

  function mensajePorDefecto(campo) {
    var v = campo.validity;
    if (v.valueMissing) return 'Este campo es obligatorio.';
    if (v.typeMismatch && campo.type === 'email') return 'Ingresa un correo válido (ej.: nombre@correo.com).';
    if (v.tooShort) return 'Debe tener al menos ' + campo.minLength + ' caracteres.';
    if (v.tooLong) return 'Debe tener como máximo ' + campo.maxLength + ' caracteres.';
    if (v.patternMismatch) return campo.getAttribute('data-mensaje') || 'El formato no es válido.';
    if (v.rangeUnderflow) return 'El valor mínimo es ' + campo.min + '.';
    if (v.rangeOverflow) return 'El valor máximo es ' + campo.max + '.';
    return '';
  }

  function feedbackDe(campo) {
    var id = campo.id + '-error';
    var fb = document.getElementById(id);
    if (!fb) {
      fb = document.createElement('div');
      fb.className = 'invalid-feedback';
      fb.id = id;
      var destino = campo.closest('.bm-input-pass') || campo;
      destino.insertAdjacentElement('afterend', fb);
    }
    // Asociar el mensaje al campo para lectores de pantalla
    var desc = (campo.getAttribute('aria-describedby') || '').split(' ').filter(Boolean);
    if (desc.indexOf(id) === -1) { desc.push(id); campo.setAttribute('aria-describedby', desc.join(' ')); }
    return fb;
  }

  function validarCampo(campo) {
    if (!campo.willValidate || campo.type === 'hidden') return true;
    var valor = (campo.value || '').trim();
    campo.setCustomValidity('');
    var mensaje = mensajePorDefecto(campo);

    var regla = REGLAS[campo.getAttribute('data-regla')];
    if (!mensaje && regla && valor !== '' && !regla.prueba(valor)) mensaje = regla.mensaje;

    var igualA = campo.getAttribute('data-igual-a');
    if (!mensaje && igualA) {
      var otro = document.getElementById(igualA);
      if (otro && otro.value !== campo.value) mensaje = 'Las contraseñas no coinciden.';
    }

    campo.setCustomValidity(mensaje);
    var fb = feedbackDe(campo);
    if (mensaje) {
      campo.classList.add('is-invalid');
      campo.setAttribute('aria-invalid', 'true');
      fb.textContent = mensaje;
      if (campo.closest('.bm-input-pass')) fb.style.display = 'block';
      return false;
    }
    campo.classList.remove('is-invalid');
    campo.removeAttribute('aria-invalid');
    fb.textContent = '';
    fb.style.display = '';
    return true;
  }

  function prepararFormulario(form) {
    form.setAttribute('novalidate', 'novalidate');
    var campos = form.querySelectorAll('input, select, textarea');

    campos.forEach(function (campo) {
      // Valida al salir del campo y, si ya tenía error, mientras escribe
      campo.addEventListener('blur', function () { if (campo.value !== '' || campo.classList.contains('is-invalid')) validarCampo(campo); });
      campo.addEventListener('input', function () {
        if (campo.classList.contains('is-invalid')) validarCampo(campo);
        // Si cambia la contraseña, revalida la confirmación
        form.querySelectorAll('[data-igual-a="' + campo.id + '"]').forEach(function (c) { if (c.value) validarCampo(c); });
      });
    });

    form.addEventListener('submit', function (e) {
      var primero = null;
      campos.forEach(function (campo) {
        if (!validarCampo(campo) && !primero) primero = campo;
      });
      if (primero) {
        e.preventDefault();
        primero.focus();
        return;
      }
      // Evita doble clic / doble envío
      var boton = form.querySelector('[type="submit"]');
      if (boton) {
        boton.disabled = true;
        boton.setAttribute('data-texto', boton.innerHTML);
        boton.innerHTML = '<span class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>' + (boton.getAttribute('data-enviando') || 'Enviando…');
      }
    });
  }

  function prepararVerPassword() {
    document.querySelectorAll('[data-ver-pass]').forEach(function (btn) {
      btn.addEventListener('click', function () {
        var campo = document.getElementById(btn.getAttribute('data-ver-pass'));
        if (!campo) return;
        var oculto = campo.type === 'password';
        campo.type = oculto ? 'text' : 'password';
        btn.textContent = oculto ? 'Ocultar' : 'Ver';
        btn.setAttribute('aria-pressed', oculto ? 'true' : 'false');
      });
    });
  }

  // Marca como inválidos los campos que el servidor devolvió con error
  function marcarErroresServidor() {
    document.querySelectorAll('[data-error-servidor]').forEach(function (campo) {
      var msg = campo.getAttribute('data-error-servidor');
      if (!msg) return;
      campo.classList.add('is-invalid');
      campo.setAttribute('aria-invalid', 'true');
      var fb = feedbackDe(campo);
      fb.textContent = msg;
      if (campo.closest('.bm-input-pass')) fb.style.display = 'block';
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('form[data-validar]').forEach(prepararFormulario);
    prepararVerPassword();
    marcarErroresServidor();
  });

  // Si el navegador restaura la página desde la caché (botón atrás), reactiva botones
  window.addEventListener('pageshow', function () {
    document.querySelectorAll('[type="submit"][data-texto]').forEach(function (b) {
      b.disabled = false;
      b.innerHTML = b.getAttribute('data-texto');
    });
  });
})();
