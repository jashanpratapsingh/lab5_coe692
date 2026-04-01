<%@ page contentType="text/html;charset=UTF-8" isELIgnored="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>CampusRent</title>
  <link rel="stylesheet" href="assets/styles.css" />
  <script defer src="assets/app.js"></script>
</head>
<body>
  <div class="container">
    <div class="nav">
      <div class="brand">
        <h1>CampusRent Portal</h1>
        <p>University Equipment Rental Platform</p>
      </div>
      <svg class="hero-svg" viewBox="0 0 240 100" aria-hidden="true">
        <defs>
          <linearGradient id="g1" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stop-color="#6ee7ff" />
            <stop offset="100%" stop-color="#8b5cf6" />
          </linearGradient>
        </defs>
        <rect x="5" y="5" width="230" height="90" rx="22" fill="none" stroke="url(#g1)" stroke-width="6" />
        <path d="M30 62 C60 20, 100 88, 135 48 C162 20, 195 76, 212 42" fill="none" stroke="url(#g1)" stroke-width="7" stroke-linecap="round"/>
      </svg>
      <div>
        <span class="badge err" id="tokenBadge">Token not set</span>
      </div>
    </div>
    <div class="menu">
      <a href="dashboard.jsp">Dashboard</a>
      <a href="catalog.jsp">Catalog</a>
      <a href="reservation.jsp" class="active">Reservation</a>
      <a href="checkout.jsp">Checkout/Return</a>
      <a href="history.jsp">History</a>
      <a href="javascript:void(0)" onclick="logout()">Logout</a>
    </div>

    <div class="grid">
      <section class="card">
        <h2>Create Reservation</h2>
        <label>User</label>
        <input id="user" />
        <label>Equipment ID</label>
        <input id="equipmentId" value="1" />
        <label>Start Date</label>
        <input id="startDate" value="2026-03-20" />
        <label>End Date</label>
        <input id="endDate" value="2026-03-22" />
        <button onclick="createReservation()">Create Reservation</button>
      </section>
      <section class="card">
        <h2>Reservation Actions</h2>
        <label>Reservation ID (Cancel)</label>
        <input id="cancelId" value="1" />
        <button class="secondary" onclick="cancelReservation()">Cancel Reservation</button>
        <button onclick="listReservations()">List Reservations</button>
      </section>
      <section class="card full">
        <h3>Output</h3>
        <pre id="reservationOut"></pre>
      </section>
    </div>
  </div>

  <script>
    ensureAuthOrRedirect();
    updateHeaderToken();
    document.getElementById('user').value = getUser() || 'student1';

    async function createReservation() {
      const body = {
        username: document.getElementById('user').value,
        equipmentId: Number(document.getElementById('equipmentId').value),
        startDate: document.getElementById('startDate').value,
        endDate: document.getElementById('endDate').value
      };
      const res = await requestJson(`${API.reservation}/reservations`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
      renderReservationResult('reservationOut', 'Create Reservation', res);
    }

    async function listReservations() {
      const res = await requestJson(`${API.reservation}/reservations`);
      renderReservationResult('reservationOut', 'Reservation List', res);
    }

    async function cancelReservation() {
      const id = document.getElementById('cancelId').value;
      const res = await requestJson(`${API.reservation}/reservations/${encodeURIComponent(id)}/cancel`, { method: 'PUT' });
      renderReservationResult('reservationOut', 'Cancel Reservation', res);
    }
  </script>
</body>
</html>
