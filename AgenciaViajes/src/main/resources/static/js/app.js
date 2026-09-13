import { gsap } from "https://cdn.jsdelivr.net/npm/gsap@3.12.7/+esm";

gsap.from(".titulo", {
    duration: 1,
    scale: 0.5,
    opacity: 0,
    ease: "back.out(1.7)"
});