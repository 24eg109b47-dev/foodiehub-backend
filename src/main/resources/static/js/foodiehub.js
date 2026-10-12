// FoodieHub Frontend JavaScript Helpers
document.addEventListener('DOMContentLoaded', function () {
    // Auto-dismiss alert banners after 5 seconds
    setTimeout(function () {
        const alerts = document.querySelectorAll('.alert-dismissible');
        alerts.forEach(function (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
});
