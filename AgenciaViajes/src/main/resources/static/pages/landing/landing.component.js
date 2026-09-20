import { NavbarComponent } from '../../js/components/navbar/navbar.component.js';
import { HeroVideoComponent } from '../../js/components/hero-video/hero-video.component.js';
import { ExploreColombiaComponent } from '../../js/components/explore-colombia/explore-colombia.component.js';
import { WhyChooseUsComponent } from '../../js/components/why-choose-us/why-choose-us.component.js';
import { TestimonialsComponent } from '../../js/components/testimonials/testimonials.component.js';
import { FooterComponent } from '../../js/components/footer/footer.component.js';
import { AnimationsManager } from '../../js/animations/gsap-animations.js';

/**
 * Landing Page Master Component
 * Orchestrates all modular components and lifecycle.
 */
export class LandingComponent {
    constructor() {
        this.navbar = null;
        this.hero = null;
        this.explore = null;
        this.whyChooseUs = null;
        this.testimonials = null;
        this.footer = null;
        this.animations = null;
    }

    init() {
        // Instantiate and initialize child components
        this.navbar = new NavbarComponent();
        this.hero = new HeroVideoComponent();
        this.explore = new ExploreColombiaComponent();
        this.whyChooseUs = new WhyChooseUsComponent();
        this.testimonials = new TestimonialsComponent();
        this.footer = new FooterComponent();

        // Initialize GSAP scroll and entrance animations
        this.animations = new AnimationsManager();
        this.animations.init();

        console.info('Traveling Colombia Landing Page initialized successfully.');
    }
}
