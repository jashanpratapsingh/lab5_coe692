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
      <a href="catalog.jsp" class="active">Catalog</a>
      <a href="reservation.jsp">Reservation</a>
      <a href="checkout.jsp">Checkout/Return</a>
      <a href="history.jsp">History</a>
      <a href="javascript:void(0)" onclick="logout()">Logout</a>
    </div>

    <div class="grid">
      <section class="card full">
        <h2>Equipment Catalog</h2>
        <label>Search Query</label>
        <input id="query" value="camera" />
        <button onclick="searchCatalog()">Search</button>
        <pre id="catalogOut"></pre>
      </section>
    </div>
  </div>

  <script>
    ensureAuthOrRedirect();
    updateHeaderToken();
    async function searchCatalog() {
      const q = document.getElementById('query').value;
      const res = await requestJson(`${API.catalog}/catalog/equipment?query=${encodeURIComponent(q)}`);
      renderCatalogResult('catalogOut', res);
    }
  </script>
</body>
</html>
