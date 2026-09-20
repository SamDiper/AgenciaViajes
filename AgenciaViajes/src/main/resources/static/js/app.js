import { LandingComponent } from '../pages/landing/landing.component.js';

// Document ready bootstrap
document.addEventListener('DOMContentLoaded', () => {
    const app = new LandingComponent();
    app.init();
});