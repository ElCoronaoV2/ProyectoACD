import { Component, OnInit, OnDestroy, ElementRef, ViewChild, AfterViewInit, HostListener, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.css'
})
export class LandingPageComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('landingContainer') landingContainer!: ElementRef;

  scrollProgress = 0;
  private isBrowser: boolean;
  private observer?: IntersectionObserver;

  // Lightbox
  showLightbox = false;
  selectedImage = '';
  selectedImageTitle = '';

  constructor(
    public auth: AuthService,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  get isLoggedIn(): boolean {
    return this.auth.isLoggedIn();
  }

  get dashboardRoute(): string {
    if (this.auth.isCEO()) return '/dashboard/ceo';
    if (this.auth.isDirector()) return '/dashboard/director';
    if (this.auth.isEmployee()) return '/dashboard/employee';
    return '/profile';
  }

  ngOnInit(): void {
    if (this.isBrowser) {
      // Initialize scroll animations
      this.initScrollAnimations();
    }
  }

  ngAfterViewInit(): void {
    if (this.isBrowser) {
      // Setup Intersection Observer for reveal animations
      this.setupIntersectionObserver();
    }
  }

  ngOnDestroy(): void {
    if (this.observer) {
      this.observer.disconnect();
    }
  }

  @HostListener('window:scroll', ['$event'])
  onScroll(): void {
    if (!this.isBrowser) return;

    // Update scroll progress bar
    const scrollTop = window.scrollY;
    const docHeight = document.documentElement.scrollHeight - window.innerHeight;
    this.scrollProgress = scrollTop / docHeight;
  }

  private initScrollAnimations(): void {
    // Smooth scroll for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
      anchor.addEventListener('click', (e) => {
        e.preventDefault();
        const target = document.querySelector(anchor.getAttribute('href') || '');
        if (target) {
          target.scrollIntoView({ behavior: 'smooth' });
        }
      });
    });
  }

  private setupIntersectionObserver(): void {
    const options = {
      root: null,
      rootMargin: '0px',
      threshold: 0.1
    };

    this.observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('revealed');
        }
      });
    }, options);

    // Observe all reveal elements
    setTimeout(() => {
      const revealElements = document.querySelectorAll('.reveal-up, .reveal-left, .reveal-right, .reveal-scale');
      revealElements.forEach(el => this.observer?.observe(el));
    }, 100);
  }

  openLightbox(imageSrc: string, title: string): void {
    this.selectedImage = imageSrc;
    this.selectedImageTitle = title;
    this.showLightbox = true;
    document.body.style.overflow = 'hidden';
  }

  closeLightbox(): void {
    this.showLightbox = false;
    this.selectedImage = '';
    this.selectedImageTitle = '';
    document.body.style.overflow = '';
  }

  onLightboxBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('lightbox-backdrop')) {
      this.closeLightbox();
    }
  }
}
