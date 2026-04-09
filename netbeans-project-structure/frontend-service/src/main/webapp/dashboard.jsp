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
      <a href="reservation.jsp">Reservation</a>
      <a href="checkout.jsp">Checkout/Return</a>
      <a href="history.jsp">History</a>
      <a href="javascript:void(0)" onclick="logout()">Logout</a> 
    </div>

    <div class="grid">
      <section class="card full">
        <h2>Welcome</h2>
        <div id="userLabel" class="notice">User: guest</div>
        <p class="notice">Use the navigation to search equipment, create reservations, process checkout/return, and view rental history.</p>
      </section>
      <section class="card">
        <h3>Service Health Check</h3>
        <button color="red" onclick="checkHealth()">Run Health Checks</button>
        <pre id="healthOut"></pre>
      </section>
      <section class="card">
        <h3>Quick Links</h3>
        <div class="notice">Suggested flow:</div>
        <ol>
          <li>Catalog Search</li>
          <li>Create Reservation</li>
          <li>Checkout / Return</li>
          <li>View History</li>
        </ol>
      </section>
    </div>
  </div>

  <script>
    ensureAuthOrRedirect();
    updateHeaderToken();
    async function checkHealth() {
      const checks = await Promise.all([
        requestJson(`${API.auth}/health`),
        requestJson(`${API.catalog}/health`),
        requestJson(`${API.inventory}/health`),
        requestJson(`${API.reservation}/health`),
        requestJson(`${API.checkout}/health`)
      ]);
      renderHealthResult('healthOut', checks);
    }
  </script>
</body>
</html>
