import { gsap } from "https://cdn.jsdelivr.net/npm/gsap@3.12.7/+esm";

gsap.from(".titulo", {
    duration: 1,
    scale: 0.5,
    opacity: 0,
    ease: "back.out(1.7)"
});


    document.addEventListener('DOMContentLoaded', () => {
    const btnEditar = document.getElementById('btnHabilitarEditar');
    const btnCancelar = document.getElementById('btnCancelar');
    const contenedorBotones = document.getElementById('contenedorBotones');

    if (btnEditar) {
    btnEditar.addEventListener('click', () => {
    const inputs = document.querySelectorAll('.input-perfil');
    inputs.forEach(input => {
    input.removeAttribute('readonly');
    input.classList.remove('bg-slate-100', 'text-slate-500', 'cursor-not-allowed');
    input.classList.add('bg-white', 'text-slate-800', 'ring-2', 'ring-blue-500', 'border-transparent');
});

    contenedorBotones.classList.remove('hidden');
    btnEditar.classList.add('hidden');
});
}

    if (btnCancelar) {
    btnCancelar.addEventListener('click', () => {
    const inputs = document.querySelectorAll('.input-perfil');
    inputs.forEach(input => {
    input.setAttribute('readonly', 'readonly');
    input.classList.add('bg-slate-100', 'text-slate-500', 'cursor-not-allowed');
    input.classList.remove('bg-white', 'text-slate-800', 'ring-2', 'ring-blue-500', 'border-transparent');
});

    contenedorBotones.classList.add('hidden');
    btnEditar.classList.remove('hidden');
});
}
});


