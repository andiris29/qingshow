//
//  QSS21CategorySelectorVC.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSS21CategorySelectionViewController;

@protocol QSS21CategorySelectorVCDelegate <NSObject>

- (void)didSelectCategories:(NSArray*)categoryArray;

@end

@interface QSS21CategorySelectionViewController : UIViewController

- (instancetype)initWithCategories:(NSArray*)array
                selectedCategories:(NSArray*)selectedCategories
               modelParentCategory:(NSDictionary*)modelParentCategory;

@property (weak, nonatomic) NSObject<QSS21CategorySelectorVCDelegate>* delegate;
@end
