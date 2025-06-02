"use client"

import { useState, useEffect, useRef } from "react"
import { motion } from "framer-motion"
import {
  ChevronRight,
  CheckCircle,
  Star,
  Clock,
  Target,
  Zap,
  Brain,
  Scan,
  Droplet,
  Smile,
  Users,
  CalendarDays,
  Headset,
  ThumbsUp
} from "lucide-react"
import Link from "next/link"
import Image from "next/image"
import styles from "./page.module.css"

interface GraphItem {
  name: string;
  value: number;
  color: string;
  dotColor: string;
}

export default function Home() {
  const [skinType, setSkinType] = useState("복합성")
  const [scanLineTop, setScanLineTop] = useState(0)
  const heroTextContainerRef = useRef<HTMLDivElement>(null)

  const initialGraphItems: GraphItem[] = [
    { name: "수분 지수", value: 67, color: "#2ECC71", dotColor: "#2ECC71" },
    { name: "유분 지수", value: 50, color: "#3498DB", dotColor: "#3498DB" },
    { name: "민감도 지수", value: 33, color: "#E74C3C", dotColor: "#E74C3C" },
    { name: "탄력 지수", value: 75, color: "#F39C12", dotColor: "#F39C12" },
  ];

  const [animatedGraphItems, setAnimatedGraphItems] = useState<GraphItem[]>(initialGraphItems);

  useEffect(() => {
    const types = ["건성", "지성", "복합성", "민감성", "중성"]
    const interval = setInterval(() => {
      setSkinType(types[Math.floor(Math.random() * types.length)])
    }, 2000)
    return () => clearInterval(interval)
  }, [])

  useEffect(() => {
    let animationFrameId: number;
    let startTime: number | null = null;
    const duration = 2000;
    let direction = 1;

    const animateScanLine = (timestamp: number) => {
      if (!heroTextContainerRef.current) {
        animationFrameId = requestAnimationFrame(animateScanLine);
        return;
      }
      if (!startTime) startTime = timestamp;
      const progress = timestamp - startTime;
      let newTop = (progress / duration) * 100;

      if (direction === -1) {
        newTop = 100 - newTop;
      }

      if (newTop > 100) {
        newTop = 100;
        direction = -1;
        startTime = timestamp;
      } else if (newTop < 0) {
        newTop = 0;
        direction = 1;
        startTime = timestamp;
      }
      
      setScanLineTop(newTop);
      animationFrameId = requestAnimationFrame(animateScanLine);
    }

    animationFrameId = requestAnimationFrame(animateScanLine);

    return () => {
      cancelAnimationFrame(animationFrameId);
    }
  }, []);

  useEffect(() => {
    const intervalId = setInterval(() => {
      setAnimatedGraphItems(prevItems => 
        prevItems.map(item => ({
          ...item,
          value: Math.floor(Math.random() * 81) + 20 // 20 ~ 100 사이의 랜덤 값
        }))
      );
    }, 2500); // 2.5초마다 값 변경

    return () => clearInterval(intervalId);
  }, []);

  const skinConcerns = [
    {
      icon: <Droplet className={styles.concernIcon} />,
      title: "거친 피부",
      description: "각질, 건조, 당김이 고민이에요",
      cardClass: styles.concernCard,
      iconBgClass: styles.concernIconContainerBlue,
    },
    {
      icon: <Target className={styles.concernIcon} />,
      title: "잘 몰라요",
      description: "내 피부 상태가 어떤지 모르겠어요",
      cardClass: `${styles.concernCard} ${styles.concernCardPurple}`,
      iconBgClass: styles.concernIconContainerPurple,
    },
    {
      icon: <Zap className={styles.concernIcon} />,
      title: "여드름",
      description: "트러블, 붉은기, 흉터가 고민이에요",
      cardClass: `${styles.concernCard} ${styles.concernCardRose}`,
      iconBgClass: styles.concernIconContainerRose,
    },
  ]
  

  const recommendations = [
    {
      icon: <Target className={styles.recommendationIcon} style={{ color: 'var(--blue-accent)' }} />,
      title: "뭐부터 시작할지 몰라요",
      description: "피부는 좋아지고 싶어요",
    },
    {
      icon: <Clock className={styles.recommendationIcon} style={{ color: 'var(--primary-color)' }} />,
      title: "피부 상태가 궁금해요",
      description: "누가 좀 말해줬으면...",
    },
    {
      icon: <Smile className={styles.recommendationIcon} style={{ color: 'var(--purple-accent)' }} />,
      title: "돈 아끼고 싶어요",
      description: "헛돈 쓰기 싫어요",
    },
  ]
  
  
  const choices = [
    {
      title: "예약",
      icon: <CalendarDays className={styles.choiceIcon} />,
    },
    {
      title: "지원 관련",
      icon: <Headset className={styles.choiceIcon} />,
    },
    {
      title: "난 좋아",
      icon: <ThumbsUp className={styles.choiceIcon} />,
    },
  ]

  const checkpoints = [
    {
      id: "01",
      badge: "체크 포인트",
      title: "떠먹여 주는 나의 피부 상태 확인",
      description: "클릭 한번으로 내 피부 알아보기",
      imageUrl: "/checkpoint1.png",
      reverse: false,
    },
    {
      id: "02",
      badge: "체크 포인트",
      title: "좋다는 제품들이 너무 많아",
      description: "그럴 땐 추천 받아서 필요한거만 알면 되지 !!",
      imageUrl: "/checkpoint2.png",
      reverse: true,
    },
    {
      id: "03",
      badge: "체크 포인트",
      title: "내가 사용하고 있는 제품을 검사 받아보자 !",
      description: null,
      imageUrl: "/checkpoint3.png",
      reverse: false,
    },
  ]

  return (
    <div className={styles.landingPageWrapper}>
      {/* Header */}
      <header className={styles.header}>
        <div className={`${styles.container} ${styles.headerContainer}`}>
          <motion.div className={styles.logo} whileHover={{ scale: 1.05 }}>
            <Droplet className={styles.logoIcon} />
            <span className={styles.logoText}>스킨AI</span>
          </motion.div>
          <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
            <button className={styles.headerButton}>피부 진단하기</button>
          </motion.div>
        </div>
      </header>

      {/* Hero Section */}
      <section className={`${styles.section} ${styles.heroSection}`}>
        <div className={styles.container}>
          <div className={styles.heroContent}>
            <div ref={heroTextContainerRef} className={styles.heroTextContainer}>
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5 }}
              >
                <div className={styles.aiBadge}>
                  <Scan className={styles.aiBadgeIcon} />
                  <span className={styles.aiBadgeText}>AI 피부 분석 기술</span>
                </div>
                <div 
                  className={styles.newScanLine} 
                  style={{ top: `${scanLineTop}%` }}
                />
                <h1 className={styles.heroTitle}>
                  <span className={styles.pinkGradientText}>AI 피부 진단</span>으로
                  <br />
                  맞춤 케어 시작하기
                </h1>

                <div className={styles.heroSkinTypeInfo}>
                  <p className={styles.heroSkinTypeQuestion}>
                    아직도 아무거나 사용하시나요?
                  </p>
                  <p className={styles.heroSkinTypeDynamic}>
                    당신의 피부 타입은 <span style={{ fontWeight: 'bold'}}>{skinType}</span>일 수도 있어요!
                  </p>
                </div>

                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                  <button className={styles.heroCtaButton}>체험해보기</button>
                </motion.div>
                <p className={styles.heroBenefitText}>
                  <CheckCircle className={styles.heroBenefitIcon} />
                  <span>3분만에 완성되는 맞춤형 스킨케어 솔루션</span>
                </p>
              </motion.div>
            </div>
            <div className={`${styles.heroImageContainer} ${styles.heroGraphContainer}`}> 
              {animatedGraphItems.map((item, index) => (
                <motion.div 
                  key={item.name} 
                  className={styles.graphItem}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ duration: 0.5, delay: index * 0.1 }}
                >
                  <div className={styles.graphItemHeader}>
                    <div style={{display: 'flex', alignItems: 'center'}}>
                      <span 
                        className={styles.graphItemDot} 
                        style={{ backgroundColor: item.dotColor }}
                      ></span>
                      <span className={styles.graphItemName}>{item.name}</span>
                    </div>
                    <span 
                      className={styles.graphItemValue}
                      style={{ backgroundColor: item.color, color: 'white', padding: '4px 10px', borderRadius: '12px', fontSize: '13px', fontWeight: 'bold' }}
                    >
                      {item.value}%
                    </span>
                  </div>
                  <div className={styles.graphItemBarBackground}>
                    <motion.div 
                      className={styles.graphItemBarForeground}
                      style={{ backgroundColor: item.color }}
                      animate={{ width: `${item.value}%` }}
                      transition={{ duration: 0.8, ease: "easeInOut" }}
                    />
                  </div>
                </motion.div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Skin Concerns Section */}
      <section className={`${styles.section} ${styles.skinConcernsSection}`}>
        <div className={styles.container}>
          <h2 className={styles.sectionTitle}>
            <span className={styles.pinkGradientText}>어떤 피부에</span>해당 되시나요?
          </h2>
          <p className={styles.sectionSubtitle}>
            당신의 피부 타입과 고민에 맞는 솔루션을 제공해 드립니다
          </p>
          <div className={styles.concernsGrid}>
            {skinConcerns.map((item, index) => (
              <motion.div
                key={index}
                className={item.cardClass}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: index * 0.1 }}
                viewport={{ once: true }}
                whileHover={{ scale: 1.05, y: -5 }}
              >
                <div className={`${styles.concernIconContainer} ${item.iconBgClass}`}>
                  {item.icon}
                </div>
                <h3 className={styles.concernTitle}>{item.title}</h3>
                <p className={styles.concernDescription}>{item.description}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Recommendations Section */}
      <section className={`${styles.section} ${styles.recommendationsSection}`}>
        <div className={styles.container}>
          <h2 className={styles.sectionTitle}>이런 사람에게 추천해요</h2>
          <p className={styles.sectionSubtitle}>당신도 해당되나요?</p>
          <div className={styles.recommendationsGrid}>
            {recommendations.map((item, index) => (
              <motion.div
                key={index}
                className={styles.recommendationCard}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: index * 0.1 }}
                viewport={{ once: true }}
                whileHover={{ scale: 1.05 }}
              >
                <div className={styles.recommendationIconContainer}>{item.icon}</div>
                <h3 className={styles.recommendationTitle}>{item.title}</h3>
                <p className={styles.recommendationDescription}>{item.description}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Choice Section */}
      {/* 
      <section className={`${styles.section} ${styles.choiceSection}`}>
        <div className={styles.container}>
          <h2 className={styles.servicesTitle} style={{ marginBottom: '2rem' }}>어디에 해당되시나요?</h2>
          <div className={styles.choicesGrid}>
            {choices.map((option, index) => (
              <motion.div
                key={index}
                className={styles.choiceCard}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: index * 0.1 }}
                viewport={{ once: true }}
                whileHover={{ scale: 1.02 }}
              >
                <div className={styles.choiceIconContainer}>{option.icon}</div>
                <h3 className={styles.choiceTitle}>{option.title}</h3>
              </motion.div>
            ))}
          </div>
        </div>
      </section>
      */}

      {/* Services Section */}
      <section className={`${styles.section} ${styles.servicesSection}`}>
        <div className={styles.container}>
          <h2 className={styles.servicesTitle}>이런 서비스들을 받을 수 있으세요!</h2>
        </div>
      </section>

      {/* Checkpoints Section */}
      <section className={`${styles.section} ${styles.checkpointsSection}`}>
        <div className={`${styles.container} ${styles.checkpointsContainer}`}>
          {checkpoints.map((cp) => (
            <motion.div
              key={cp.id}
              className={`${styles.checkpoint} ${cp.reverse ? styles.checkpointReverse : ""}`}
              initial={{ opacity: 0, y: 40 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.7 }}
              viewport={{ once: true }}
            >
              <div className={styles.checkpointTextContainer}>
                <div className={styles.checkpointBadgeContainer}>
                  <div className={styles.checkpointBadge}>{cp.badge}</div>
                  <div className={styles.checkpointNumber}>{cp.id}</div>
                </div>
                <h3 className={styles.checkpointTitle} dangerouslySetInnerHTML={{ __html: cp.title.replace(/\n/g, "<br />") }}></h3>
                {cp.description && <p className={styles.checkpointDescription} dangerouslySetInnerHTML={{ __html: cp.description.replace(/\n/g, "<br />") }}></p>}
              </div>
              <div className={styles.checkpointCardAndImageContainer}>
                <div className={styles.checkpointImageWrapper}>
                  <Image 
                    src={cp.imageUrl}
                    alt={`${cp.title} 이미지`}
                    width={400}
                    height={300}
                    className={styles.checkpointImage}
                  />
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </section>

      {/* CTA Section */}
      <section className={`${styles.section} ${styles.ctaSection}`}>
        <div className={`${styles.container} ${styles.ctaContent}`}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
            viewport={{ once: true }}
          >
            <h2 className={styles.ctaTitle}>지금 바로 시작하세요!</h2>
            <p className={styles.ctaSubtitle}>3분만에 맞춤형 피부 솔루션을 받아보세요</p>
            <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
              <button className={styles.ctaButton}>
                무료로 체험해보기 <ChevronRight style={{width:'24px', height:'24px'}}/>
              </button>
            </motion.div>
          </motion.div>
        </div>
      </section>

      {/* Footer */}
      <footer className={styles.footer}>
        <div className={`${styles.container} ${styles.footerContainer}`}>
          <div className={styles.footerLogoContainer}>
            <Droplet className={styles.footerLogoIcon} />
            <span className={styles.footerLogoText}>스킨AI</span>
          </div>
          <div className={styles.footerLinks}>
            <Link href="#" className={styles.footerLink}>서비스 소개</Link>
            <Link href="#" className={styles.footerLink}>이용 방법</Link>
            <Link href="#" className={styles.footerLink}>개인정보처리방침</Link>
          </div>
        </div>
        <div className={`${styles.container} ${styles.footerCopyright}`}>
          © {new Date().getFullYear()} 스킨AI. All rights reserved.
        </div>
      </footer>
    </div>
  )
}
