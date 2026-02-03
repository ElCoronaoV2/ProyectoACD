import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  selector: 'app-director',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './director.component.html',
  styleUrls: ['./director.component.css']
})
export class DirectorComponent implements OnInit {
  ceos: any[] = [];
  newCeo = {
    nombre: '',
    email: '',
    password: ''
  };
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadCeos();
  }

  loadCeos() {
    this.adminService.getCeos().subscribe({
      next: (data: any) => this.ceos = data,
      error: (err: any) => console.error('Error loading CEOs', err)
    });
  }

  createCeo(event: Event) {
    event.preventDefault();
    this.errorMessage = '';
    this.successMessage = '';

    this.adminService.createCeo(this.newCeo).subscribe({
      next: (response: any) => {
        this.successMessage = 'CEO creado exitosamente';
        this.newCeo = { nombre: '', email: '', password: '' };
        this.loadCeos();
      },
      error: (err: any) => {
        this.errorMessage = 'Error al crear CEO. Verifique si el email ya existe.';
        console.error(err);
      }
    });
  }
}
