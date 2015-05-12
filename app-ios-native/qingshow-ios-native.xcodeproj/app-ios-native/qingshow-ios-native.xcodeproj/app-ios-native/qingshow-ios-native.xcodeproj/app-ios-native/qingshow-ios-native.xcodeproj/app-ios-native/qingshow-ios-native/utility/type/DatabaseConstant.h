//
//  Constant.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/2/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#ifndef qingshow_ios_native_DatabaseConstant_h
#define qingshow_ios_native_DatabaseConstant_h

typedef NS_ENUM(NSInteger, ShowTag) {
    kShowTagFrock = 0,   //女装
    kShowTagBag = 1,        //包包
    kShowTagShow = 2,       //鞋子
    kShowTagAccessory = 3       //配饰
};
typedef NS_ENUM(NSInteger, PeopleRole) {
    kPeopleRoleNormal = 0,
    kPeopleRoleModel = 1
};
typedef NS_ENUM(NSInteger, PeopleGender) {
    kPeopleGenderMale = 0,
    kPeopleGenderFemale = 1
};
typedef NS_ENUM(NSInteger, PeopleHairType) {
    kPeopleHairTypeAll = 0,
    kPeopleHairTypeLong = 1,
    kPeopleHairTypeSuperLong =2,
    kPeopleHairTypeMiddleLong = 3
};

typedef NS_ENUM(NSInteger, ItemCategory) {
    kItemCategoryJacket = 0,
    kItemCategoryPants = 1,
    kItemCategorySkirt = 2,
    kItemCategoryShoe = 3
};

typedef NS_ENUM(NSInteger, BrandType) {
    kBrandTypeBrand = 0,
    kBrandTypeStudio = 1
};
typedef NS_ENUM(NSInteger, ChosenType) {
    ChosenTypeEditor = 0,
    ChosenTypePromotion = 1
};

#endif
