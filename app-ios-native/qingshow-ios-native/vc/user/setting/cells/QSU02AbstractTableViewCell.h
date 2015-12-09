//
//  QSU02AbstractTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/5/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, U02Section) {
    U02SectionImage = 0,
    U02SectionManager,
    U02SectionInfo,
    U02SectionOther
};
NSString* u02SectionToTitle(U02Section sec);

@protocol QSU02AbstractTableViewCellDelegate <NSObject>

- (void)prompToChangeImage:(NSInteger)type;
- (void)showAddressList;
- (void)showExpectationVc;
- (void)showChangePasswordVc;
- (void)updateUserInfoKey:(NSString*)key value:(NSString*)value;
- (void)showPickerWithType:(NSInteger)type;
@end


@interface QSU02AbstractTableViewCell : UITableViewCell

@property (assign, nonatomic) U02Section sectionType;
@property (assign, nonatomic) NSInteger rowType;
@property (weak, nonatomic) NSObject<QSU02AbstractTableViewCellDelegate>* delegate;

+ (QSU02AbstractTableViewCell*)generateCellWithSectionType:(U02Section)sectionType rowType:(NSInteger)rowType;

- (void)bindWithUser:(NSDictionary*)userDict;
- (BOOL)cellDidClicked;
- (void)resignKeyboardAndPicker;
@end
