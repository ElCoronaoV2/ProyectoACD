import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { ReservationService } from '../../core/services/reservation.service';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user: any = null;
  reservations: any[] = [];
  editMode = false;
  updatedUser: any = {};

  constructor(
    private userService: UserService,
    private reservationService: ReservationService,
    private authService: AuthService,
    private notificationService: NotificationService
  ) { }

  ngOnInit() {
    this.loadProfile();
    this.loadReservations();
  }

  loadProfile() {
    this.userService.getProfile().subscribe({
      next: (res) => {
        this.user = res;
        this.updatedUser = { ...res };
      },
      error: (err) => console.error(err)
    });
  }

  loadReservations() {
    this.reservationService.getMyReservations().subscribe({
      next: (res) => this.reservations = res,
      error: (err) => console.error(err)
    });
  }

  toggleEdit() {
    this.editMode = !this.editMode;
    if (!this.editMode) {
      this.updatedUser = { ...this.user }; // Cancel edits
    }
  }

  saveProfile() {
    // Logic to save profile (call UserService)
    // Since API expects userId in URL based on my previous UserService.ts creation (Step 212: updateProfile(userId, data))
    // But UserController (Step 219) uses /profile (PUT).
    // I should update UserService.ts to use PUT /profile to be consistent with UserController.
    // Or UserController updates /users/{id}.
    // UserController (Step 219) implemented /profile.
    // So I will update UserService.ts to match.

    // Assuming UserService updateProfile calls PUT /profile (I need to fix UserService TS)
    // I'll fix UserService TS in next step or inline if I can.
    // For now, I'll assume I'll call updateProfile(this.user.id, this.updatedUser) and Service decides URL.
    // Or I can update Service now.

    this.userService.updateProfile(this.user.id, this.updatedUser).subscribe({
      next: (res) => {
        this.user = res;
        this.editMode = false;
        this.notificationService.showSuccess('Perfil actualizado');
      },
      error: (err) => this.notificationService.showError('Error al actualizar perfil')
    });
  }

  cancelReservation(id: number) {
    if (confirm('¿Cancelar reserva?')) {
      this.reservationService.updateStatus(id, 'CANCELADA').subscribe({
        next: () => this.loadReservations()
      });
    }
  }
}
