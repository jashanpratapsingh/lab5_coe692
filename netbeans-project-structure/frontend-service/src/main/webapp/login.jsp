<%@ page contentType="text/html;charset=UTF-8" isELIgnored="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>CampusRent Login</title>
  <link rel="stylesheet" href="assets/styles.css" />
  <script defer src="assets/app.js"></script>
</head>
<body>
  <div class="container">
    <div class="nav">
      <div class="brand">
        <h1>CampusRent Login</h1>
        <p>Sign in to access rental operations</p>
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
    </div>

    <div class="grid">
      <section class="card full">
        <h2>Authentication</h2>
        <label>Username</label>
        <input id="username" value="student1" />
        <label>Password</label>
        <input id="password" type="password" value="password123" />
        <button onclick="doLogin()">Login</button>
        <pre id="loginOut"></pre>
        <div class="notice">Default test accounts: student1/password123 and staff1/password123</div>
      </section>
    </div>
  </div>

  <script>
    updateHeaderToken();

    async function doLogin() {
      if (isPrivateBlocked()) {
        renderText('loginOut', 'Private browsing not allowed', ['Please open this site in a normal (non-private) window and try again.']);
        clearAuthState();
        updateHeaderToken();
        return;
      }
      const body = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
      };
      const result = await requestJson(`${API.auth}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
      renderAuthResult('loginOut', result);
      if (result.ok && result.data && result.data.success && result.data.token) {
        setPrivateBlocked(false);
        setToken(result.data.token);
        setSessionMarker(result.data.sessionMarker || '');
        setUser(body.username);
        updateHeaderToken();
        setTimeout(() => { window.location.href = '/dashboard.jsp'; }, 400);
      } else {
        clearAuthState();
        updateHeaderToken();
      }
    }
  </script>
</body>
</html>
