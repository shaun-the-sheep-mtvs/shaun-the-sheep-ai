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
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            });
            const body = await res.json();

            if (!res.ok) {
                alert(body.error || '로그인에 실패했습니다.');
                return;
            }

            // JWT 토큰을 로컬 스토리지에 저장
            localStorage.setItem('token', body.token);

            // 로그인 성공 후 이동
            window.location.href = '/dashboard';
        } catch (err) {
            alert('네트워크 오류가 발생했습니다.');
        }
    });
});
