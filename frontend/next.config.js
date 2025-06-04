/** @type {import('next').NextConfig} */
const nextConfig = {async headers() {
  return [
    {
      // 모든 요청에 대해 캐시 무효화 헤더 설정
      source: '/:path',
      headers: [
        { key: 'Cache-Control', value: 'no-store, no-cache, must-revalidate, proxy-revalidate' },
        { key: 'Pragma',       value: 'no-cache' },
        { key: 'Expires',      value: '0' },
      ],
    },
  ];
},
};

module.exports = nextConfig;
