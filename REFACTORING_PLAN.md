# Database Refactoring Plan: Separate Skin Data from User Entity

## 🎯 Overview
This document outlines the plan to refactor the User entity by separating skin-related data into normalized database tables, following proper database design principles.

## 🔍 Current State Analysis

### Problems with Current User Entity
```java
// Current User.java - PROBLEMATIC DESIGN
@Entity
public class User extends BaseEntity {
    // ... auth fields
    
    // ❌ PROBLEM: Enum stored directly in users table
    @Enumerated(EnumType.STRING)
    private SkinType skinType;
    
    // ❌ PROBLEM: JSON array stored in users table  
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> troubles = new ArrayList<>();
    
    // ✅ GOOD: Already properly normalized
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ProductUserLink> productUserLinks = new ArrayList<>();
}
```

### Issues:
1. **Violates Single Responsibility Principle**: User entity handles authentication AND skin analysis data
2. **Not Normalized**: skinType and troubles are stored directly in users table
3. **Difficult to Extend**: Adding new skin-related features requires modifying User entity
4. **JSONB Complexity**: troubles field uses PostgreSQL-specific JSONB, making queries complex

### What's Already Done Right:
- `productUserLinks` follows proper JPA relationship pattern with separate table
- We need to make `skinType` and `troubles` follow the same pattern

## 🏗️ Proposed New Entity Structure

### 1. SkinMBTI Entity (Master Data)
```java
@Entity
@Table(name = "skin_mbti")
public class SkinMBTI {
    @Id
    private String mbtiCode;        // e.g., "DBIL", "MOST"
    
    @Column(nullable = false)
    private String koreanName;      // e.g., "건성", "민감성"
    
    private String description;     // Optional description
    
    // Static data constants for initialization
    public static final String[][] MBTI_DATA = {
        {"DBIL", "건성", "건조하고 민감하지 않은 탄력 부족 피부"},
        {"DBIT", "건성", "건조하고 민감하지 않은 탄력 있는 피부"},
        {"DBSL", "건성", "건조하고 민감한 탄력 부족 피부"},
        {"DBST", "건성", "건조하고 민감한 탄력 있는 피부"},
        {"DOIL", "수분부족지성", "건조하고 유분이 많은 탄력 부족 피부"},
        {"DOIT", "수분부족지성", "건조하고 유분이 많은 탄력 있는 피부"},
        {"DOSL", "수분부족지성", "건조하고 유분이 많은 민감한 탄력 부족 피부"},
        {"DOST", "수분부족지성", "건조하고 유분이 많은 민감한 탄력 있는 피부"},
        {"MBIL", "복합성", "수분이 있고 민감하지 않은 탄력 부족 피부"},
        {"MBIT", "복합성", "수분이 있고 민감하지 않은 탄력 있는 피부"},
        {"MBSL", "민감성", "수분이 있고 민감한 탄력 부족 피부"},
        {"MBST", "민감성", "수분이 있고 민감한 탄력 있는 피부"},
        {"MOIL", "지성", "수분과 유분이 많은 탄력 부족 피부"},
        {"MOIT", "지성", "수분과 유분이 많은 탄력 있는 피부"},
        {"MOSL", "민감성", "수분과 유분이 많은 민감한 탄력 부족 피부"},
        {"MOST", "민감성", "수분과 유분이 많은 민감한 탄력 있는 피부"}
    };
}
```

### 2. SkinConcern Entity (Master Data)
```java
@Entity
@Table(name = "skin_concerns")
public class SkinConcern {
    @Id
    private String id;              // e.g., "dryness", "oiliness"
    
    @Column(nullable = false)
    private String label;           // e.g., "건조함", "번들거림"
    
    // Static data constants from concerns.ts
    public static final String[][] CONCERN_DATA = {
        {"dryness", "건조함"},
        {"oiliness", "번들거림"},
        {"sensitivity", "민감함"},
        {"elasticity", "탄력 저하"},
        {"redness", "홍조"},
        {"unevenTone", "톤 안정"},
        {"hyperpigment", "색소침착"},
        {"fineLines", "잔주름"},
        {"pores", "모공 케어"},
        {"breakouts", "트러블"},
        {"dullness", "칙칙함"},
        {"darkCircles", "다크써클"},
        {"roughTexture", "결 거칠음"}
    };
}
```

### 3. UserSkinProfile Entity (User-MBTI Relationship)
```java
@Entity
@Table(name = "user_skin_profiles")
public class UserSkinProfile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbti_code", nullable = false)
    private SkinMBTI skinMbti;
    
    // Track when this profile was created (for history)
    // Inherits createdAt, updatedAt from BaseEntity
}
```

### 4. UserSkinConcern Entity (User-Concern Relationship)
```java
@Entity
@Table(name = "user_skin_concerns")
@IdClass(UserSkinConcernId.class)
public class UserSkinConcern {
    @Id
    @Column(name = "user_id")
    private String userId;
    
    @Id
    @Column(name = "concern_id")
    private String concernId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concern_id", insertable = false, updatable = false)
    private SkinConcern skinConcern;
    
    // Note: Max 3 concerns per user (enforced in service layer)
}
```

### 5. Updated User Entity
```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    // ... existing auth fields (id, username, email, password, roles)
    
    // ❌ REMOVE THESE FIELDS:
    // private SkinType skinType;
    // private List<String> troubles;
    
    // ✅ ADD THESE RELATIONSHIPS:
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSkinProfile> skinProfiles = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSkinConcern> skinConcerns = new ArrayList<>();
    
    // ✅ KEEP EXISTING (already properly normalized):
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductUserLink> productUserLinks = new ArrayList<>();
}
```

## 🔄 Migration Strategy

### Phase 1: Create New Entities
1. Create all 4 new entity classes with static data constants
2. Create corresponding repositories
3. Create composite key class for UserSkinConcern

### Phase 2: Update User Entity
1. Remove `skinType` and `troubles` fields
2. Add new JPA relationships
3. Update constructors if needed

### Phase 3: Update CheckListService
```java
// BEFORE (in CheckListService.create):
user.setSkinType(MBTI_TO_SKIN_TYPE.get(mbtiCode));
user.setTroubles(req.getTroubles());
userRepo.save(user);

// AFTER:
// 1. Save skin profile
UserSkinProfile profile = new UserSkinProfile();
profile.setUser(user);
profile.setSkinMbti(skinMbtiRepo.findById(mbtiCode).orElseThrow());
userSkinProfileRepo.save(profile);

// 2. Save concerns (max 3)
user.getSkinConcerns().clear(); // Remove existing
for (String concernId : req.getTroubles()) {
    UserSkinConcern concern = new UserSkinConcern();
    concern.setUser(user);
    concern.setSkinConcern(skinConcernRepo.findById(concernId).orElseThrow());
    userSkinConcernRepo.save(concern);
}
```

### Phase 4: Update Other Services
- Update all services that currently access `user.getSkinType()`
- Update all services that currently access `user.getTroubles()`
- Replace with queries to new entities

## 📝 Implementation Checklist

### New Entities to Create:
- [ ] `SkinMBTI.java` with MBTI_DATA constants
- [ ] `SkinConcern.java` with CONCERN_DATA constants  
- [ ] `UserSkinProfile.java` with User-MBTI relationship
- [ ] `UserSkinConcern.java` with User-Concern relationship
- [ ] `UserSkinConcernId.java` composite key class

### New Repositories to Create:
- [ ] `SkinMBTIRepository`
- [ ] `SkinConcernRepository`
- [ ] `UserSkinProfileRepository`
- [ ] `UserSkinConcernRepository`

### Updates Required:
- [ ] Update `User.java` entity (remove fields, add relationships)
- [ ] Update `CheckListService.java` (modify create method)
- [ ] Update `UserService.java` (skin data access methods)
- [ ] Update any controllers that access skin data
- [ ] Update DTOs that return skin data

### Data Migration:
- [ ] Create migration script to move existing User.skinType → UserSkinProfile
- [ ] Create migration script to move existing User.troubles → UserSkinConcern
- [ ] Ensure no data loss during migration

### Testing:
- [ ] Test skin analysis flow end-to-end
- [ ] Test MBTI calculation and storage
- [ ] Test concern selection (max 3) enforcement
- [ ] Test all existing API endpoints still work
- [ ] Test user profile data retrieval

## 🎯 Expected Benefits

1. **Better Database Design**: Proper normalization following 3NF principles
2. **Easier to Extend**: New skin-related features don't require User entity changes
3. **Better Performance**: Proper indexing on foreign keys vs JSON queries
4. **Cleaner Code**: Separation of concerns between auth and skin data
5. **Maintainability**: Each entity has single responsibility
6. **Consistency**: All user-related data follows same JPA relationship pattern

## 🚀 Final Result

After refactoring, the database will have:
- `users` table: Only authentication and basic user data
- `skin_mbti` table: 16 MBTI types (master data)
- `skin_concerns` table: 13 skin concerns (master data)  
- `user_skin_profiles` table: User-MBTI relationships with history
- `user_skin_concerns` table: User-concern relationships (max 3 per user)
- `product_user_link` table: Existing, unchanged

This structure is professional, maintainable, and follows proper database design principles while maintaining all existing functionality.