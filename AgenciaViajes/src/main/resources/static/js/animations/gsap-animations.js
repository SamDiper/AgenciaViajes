/**
 * GSAP Animations Manager
 * Traveling Colombia
 */
export class AnimationsManager {
    constructor() {
        this.prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        this.gsap = window.gsap || null;
        this.ScrollTrigger = window.ScrollTrigger || null;
    }

    init() {
        if (!this.gsap) {
            console.info('GSAP not loaded. Skipping animations.');
            return;
        }

        if (this.ScrollTrigger) {
            this.gsap.registerPlugin(this.ScrollTrigger);
        }

        if (this.prefersReducedMotion) {
            this.initNavbarScroll();
            return;
        }

        this.initHeroLogo();
        this.initNavbarScroll();
        this.initDestinationsScroll();
        this.initWhyChooseUsScroll();
        this.initTestimonialsScroll();
    }

    initHeroLogo() {
        const logo = document.querySelector('.hero-brand-logo');
        if (logo) {
            this.gsap.from(logo, {
                y: 30,
                opacity: 0,
                scale: 0.95,
                duration: 1.2,
                ease: 'power3.out'
            });
        }
    }

    initNavbarScroll() {
        const navbar = document.getElementById('navbar');
        if (!navbar) return;

        const updateNavbar = () => {
            if (window.scrollY > 50) {
                navbar.classList.add('navbar-scrolled');
                navbar.classList.remove('navbar-transparent');
            } else {
                navbar.classList.remove('navbar-scrolled');
                navbar.classList.add('navbar-transparent');
            }
        };

        window.addEventListener('scroll', updateNavbar, { passive: true });
        updateNavbar();
    }

    initDestinationsScroll() {
        const section = document.querySelector('#destinos');
        if (!section || !this.ScrollTrigger) return;

        const header = section.querySelector('.section-header');
        if (header) {
            this.gsap.from(header, {
                scrollTrigger: {
                    trigger: section,
                    start: 'top 80%'
                },
                y: 35,
                opacity: 0,
                duration: 0.8,
                ease: 'power2.out'
            });
        }

        const cards = section.querySelectorAll('.destination-card');
        if (cards.length > 0) {
            this.gsap.from(cards, {
                scrollTrigger: {
                    trigger: section.querySelector('.destinations-grid') || section,
                    start: 'top 75%'
                },
                y: 40,
                opacity: 0,
                stagger: 0.18,
                duration: 0.8,
                ease: 'power3.out'
            });
        }
    }

    initWhyChooseUsScroll() {
        const section = document.querySelector('#why-choose-us');
        if (!section || !this.ScrollTrigger) return;

        const items = section.querySelectorAll('.benefit-item');
        if (items.length > 0) {
            this.gsap.from(items, {
                scrollTrigger: {
                    trigger: section,
                    start: 'top 75%'
                },
                y: 35,
                opacity: 0,
                stagger: 0.15,
                duration: 0.8,
                ease: 'power3.out'
            });
        }
    }

    initTestimonialsScroll() {
        const section = document.querySelector('#testimonios');
        if (!section || !this.ScrollTrigger) return;

        const cards = section.querySelectorAll('.testimonial-card');
        if (cards.length > 0) {
            this.gsap.from(cards, {
                scrollTrigger: {
                    trigger: section,
                    start: 'top 75%'
                },
                y: 40,
                opacity: 0,
                stagger: 0.15,
                duration: 0.85,
                ease: 'power3.out'
            });
        }
    }
}
