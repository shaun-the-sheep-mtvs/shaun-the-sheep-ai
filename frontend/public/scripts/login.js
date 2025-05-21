document.addEventListener('DOMContentLoaded', () => {
  const form = document.querySelector('.loginForm');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email    = form.querySelector('input[name="email"]').value;
    const password = form.querySelector('input[name="password"]').value;

    try {
      const res = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        credentials: 'include',          // ← 쿠키를 주고받으려면 꼭 필요
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      const body = await res.json();

      if (!res.ok) {
        alert(body.error || '로그인에 실패했습니다.');
        return;
      }

      // 로그인이 성공하면, 백엔드가 Set-Cookie 로 토큰을 내려주기 때문에
      // 브라우저가 알아서 쿠키를 저장해 줍니다 (HttpOnly).

      // 원하는 성공 페이지로 이동
      window.location.href = '/dashboard';
    } catch (err) {
      console.error(err);
      alert('네트워크 오류가 발생했습니다.');
    }
  });
});
