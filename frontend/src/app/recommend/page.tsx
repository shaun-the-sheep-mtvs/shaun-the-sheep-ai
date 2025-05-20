import Image from "next/image"
import { Search, Menu, MessageSquare, Bell, User } from "lucide-react"

export default function Home() {
  return (
    <main className="max-w-screen-md mx-auto bg-white min-h-screen">
      {/* Header with menu and search */}
      <header className="p-4 border-b">
        <div className="flex items-center justify-between">
          <Menu className="h-6 w-6 text-gray-700" />
          <h1 className="text-xl font-bold text-center">스킨케어</h1>
          <Bell className="h-6 w-6 text-gray-700" />
        </div>
      </header>

      {/* Greeting Section */}
      <section className="p-4 bg-pink-50 mb-6">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-pink-200 flex items-center justify-center">
            <User className="h-5 w-5 text-pink-500" />
          </div>
          <p className="text-lg">
            안녕하세요, <span className="text-pink-500 font-medium">마라님</span>
          </p>
        </div>
      </section>

      {/* Recommendation Section - Moved to top */}
      <section className="mb-8 px-4">
        <h2 className="text-lg font-medium mb-4">회원님에게 어떤 제품이 좋을까요?</h2>

        <div className="bg-emerald-400 rounded-xl p-4 text-white mb-4">
          <h3 className="font-medium mb-2">민감형 ⭐</h3>
          <p className="text-sm mb-3">
            당신의 피부는 외부 자극에 민감하게 반응하는 편입니다. 진정 효과가 있는 제품을 사용하는 것이 좋습니다.
          </p>
          <div className="bg-white bg-opacity-20 rounded-lg p-3 text-sm">자극이 적은 제품을 사용해보세요!</div>
        </div>

        <button className="w-full bg-pink-400 text-white rounded-full py-3 font-medium mb-4">
          체크리스트 작성하러 가기
        </button>
      </section>

      {/* Search Bar */}
      <div className="px-4 mb-6">
        <div className="flex items-center gap-2 p-3 bg-gray-100 rounded-full">
          <Search className="h-5 w-5 text-gray-500" />
          <input
            type="text"
            placeholder="검색"
            className="bg-transparent border-none outline-none flex-1 text-gray-700"
          />
        </div>
      </div>

      {/* Toner Section */}
      <section className="mb-8 px-4">
        <div className="flex items-center gap-2 mb-4">
          <div className="relative w-8 h-8 flex items-center justify-center bg-green-100 rounded-md">
            <MessageSquare className="h-4 w-4 text-green-500" />
            <span className="absolute top-1 left-1 text-[6px] text-green-500">...</span>
          </div>
          <h2 className="text-lg font-medium">토너</h2>
        </div>

        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
          {[1, 2, 3].map((item) => (
            <div
              key={`toner-${item}`}
              className="bg-white rounded-2xl shadow-md overflow-hidden border border-gray-100"
            >
              <div className="flex flex-col items-center">
                <div className="relative w-full h-40 mb-2 bg-gray-50 p-2" style={{ position: 'relative' }}>
                  <Image
                    src="/placeholder.svg"
                    alt="다이브인 자몽차 히알루론산 세럼"
                    fill
                    sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                    className="object-contain"
                  />
                </div>
                <div className="p-3 w-full">
                  <p className="text-xs font-medium mb-2 line-clamp-2">
                    다이브인 자몽차 히알루론산 세럼 100ml 기획 (+수딩크림 50ml)
                  </p>
                  <p className="text-xs text-gray-500 mb-2">수분</p>
                  <button className="w-full bg-green-400 text-white rounded-full py-1.5 text-sm">구매</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Serum Section */}
      <section className="mb-8 px-4">
        <div className="flex items-center gap-2 mb-4">
          <div className="relative w-8 h-8 flex items-center justify-center bg-blue-100 rounded-md">
            <MessageSquare className="h-4 w-4 text-blue-500" />
            <span className="absolute top-1 left-1 text-[6px] text-blue-500">...</span>
          </div>
          <h2 className="text-lg font-medium">세럼</h2>
        </div>

        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
          {[1, 2, 3].map((item) => (
            <div
              key={`serum-${item}`}
              className="bg-white rounded-2xl shadow-md overflow-hidden border border-gray-100"
            >
              <div className="flex flex-col items-center">
                <div className="relative w-full h-40 mb-2 bg-gray-50 p-2" style={{ position: 'relative' }}>
                  <Image
                    src="/placeholder.svg"
                    alt="다이브인 자몽차 히알루론산 세럼"
                    fill
                    sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                    className="object-contain"
                  />
                </div>
                <div className="p-3 w-full">
                  <p className="text-xs font-medium mb-2 line-clamp-2">
                    다이브인 자몽차 히알루론산 세럼 100ml 기획 (+수딩크림 50ml)
                  </p>
                  <p className="text-xs text-gray-500 mb-2">유분</p>
                  <button className="w-full bg-blue-400 text-white rounded-full py-1.5 text-sm">구매</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Lotion Section */}
      <section className="mb-8 px-4">
        <div className="flex items-center gap-2 mb-4">
          <div className="relative w-8 h-8 flex items-center justify-center bg-red-100 rounded-md">
            <MessageSquare className="h-4 w-4 text-red-500" />
            <span className="absolute top-1 left-1 text-[6px] text-red-500">...</span>
          </div>
          <h2 className="text-lg font-medium">로션</h2>
        </div>

        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
          {[1, 2, 3].map((item) => (
            <div
              key={`lotion-${item}`}
              className="bg-white rounded-2xl shadow-md overflow-hidden border border-gray-100"
            >
              <div className="flex flex-col items-center">
                <div className="relative w-full h-40 mb-2 bg-gray-50 p-2" style={{ position: 'relative' }}>
                  <Image
                    src="/placeholder.svg"
                    alt="다이브인 자몽차 히알루론산 로션"
                    fill
                    sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                    className="object-contain"
                  />
                </div>
                <div className="p-3 w-full">
                  <p className="text-xs font-medium mb-2 line-clamp-2">
                    다이브인 자몽차 히알루론산 로션 100ml 기획 (+수딩크림 50ml)
                  </p>
                  <p className="text-xs text-gray-500 mb-2">민감도</p>
                  <button className="w-full bg-red-400 text-white rounded-full py-1.5 text-sm">구매</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Cream Section */}
      <section className="mb-8 px-4">
        <div className="flex items-center gap-2 mb-4">
          <div className="relative w-8 h-8 flex items-center justify-center bg-orange-100 rounded-md">
            <MessageSquare className="h-4 w-4 text-orange-500" />
            <span className="absolute top-1 left-1 text-[6px] text-orange-500">...</span>
          </div>
          <h2 className="text-lg font-medium">크림</h2>
        </div>

        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
          {[1, 2, 3].map((item) => (
            <div
              key={`cream-${item}`}
              className="bg-white rounded-2xl shadow-md overflow-hidden border border-gray-100"
            >
              <div className="flex flex-col items-center">
                <div className="relative w-full h-40 mb-2 bg-gray-50 p-2" style={{ position: 'relative' }}>
                  <Image
                    src="/placeholder.svg"
                    alt="다이브인 자몽차 히알루론산 크림"
                    fill
                    sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                    className="object-contain"
                  />
                </div>
                <div className="p-3 w-full">
                  <p className="text-xs font-medium mb-2 line-clamp-2">
                    다이브인 자몽차 히알루론산 크림 100ml 기획 (+수딩크림 50ml)
                  </p>
                  <p className="text-xs text-gray-500 mb-2">탄력</p>
                  <button className="w-full bg-orange-400 text-white rounded-full py-1.5 text-sm">구매</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Essence Section */}
      <section className="mb-8 px-4">
        <div className="flex items-center gap-2 mb-4">
          <div className="relative w-8 h-8 flex items-center justify-center bg-emerald-100 rounded-md">
            <MessageSquare className="h-4 w-4 text-emerald-500" />
            <span className="absolute top-1 left-1 text-[6px] text-emerald-500">...</span>
          </div>
          <h2 className="text-lg font-medium">에센스</h2>
        </div>

        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
          {[1, 2, 3].map((item) => (
            <div
              key={`essence-${item}`}
              className="bg-white rounded-2xl shadow-md overflow-hidden border border-gray-100"
            >
              <div className="flex flex-col items-center">
                <div className="relative w-full h-40 mb-2 bg-gray-50 p-2" style={{ position: 'relative' }}>
                  <Image
                    src="/placeholder.svg"
                    alt="다이브인 자몽차 히알루론산 에센스"
                    fill
                    sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                    className="object-contain"
                  />
                </div>
                <div className="p-3 w-full">
                  <p className="text-xs font-medium mb-2 line-clamp-2">
                    다이브인 자몽차 히알루론산 에센스 100ml 기획 (+수딩크림 50ml)
                  </p>
                  <p className="text-xs text-gray-500 mb-2">수분</p>
                  <button className="w-full bg-emerald-400 text-white rounded-full py-1.5 text-sm">구매</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>
    </main>
  )
}
