import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'Restaurant-tec';
  backendStatus = 'Verificando...';
  testResult: any = null;

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.checkBackendStatus();
  }

  checkBackendStatus() {
    this.http.get('/api/locales').subscribe({
      next: (data) => {
        this.backendStatus = '✅ Conectado';
      },
      error: (error) => {
        this.backendStatus = '❌ No conectado';
        console.error('Error conectando con backend:', error);
      }
    });
  }

  testBackend() {
    this.testResult = null;
    this.http.get('/api/locales').subscribe({
      next: (data: any) => {
        this.testResult = {
          success: true,
          message: '✅ Conexión exitosa con el backend!',
          data: data
        };
      },
      error: (error) => {
        this.testResult = {
          success: false,
          message: '❌ Error al conectar con el backend',
          data: error.message
        };
      }
    });
  }
}
