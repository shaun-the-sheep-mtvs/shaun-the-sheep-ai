// public/scripts/auth.js
(function() {
  const match = document.cookie.match(/(?:^|;\s*)token=([^;]+)/);
  const token = match ? match[1] : null;
  const path  = window.location.pathname;

  if (!token && path !== '/login') {
    window.location.replace('/login');
    return;
  }
  if (token && path === '/login') {
    window.location.replace('/');
  }
})();
