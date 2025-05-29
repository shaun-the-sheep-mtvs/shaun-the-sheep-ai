// src/utils/jwt.ts
export function parseJwt<T>(token: string): T | null {
  try {
    // 1) Base64URL → Base64 표준 형식으로
    const base64Url = token.split('.')[1];
    const base64 = base64Url
      .replace(/-/g, '+')
      .replace(/_/g, '/')
      // 패딩 문자(=)가 없으면 추가
      + '=='.slice(0, (4 - (base64Url.length % 4)) % 4);

    // 2) atob → Latin1 문자열
    const binary = atob(base64);

    // 3) Latin1 → Percent-encoding → UTF-8 디코딩
    const utf8 = decodeURIComponent(
      binary
        .split('')
        .map(c => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
        .join('')
    );

    return JSON.parse(utf8) as T;
  } catch {
    return null;
  }
}
