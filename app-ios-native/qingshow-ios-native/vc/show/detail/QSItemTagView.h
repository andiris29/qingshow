//
//  QSItemTagView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/30.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSItemTagView : UIView

@property (weak, nonatomic) IBOutlet UILabel* tagLabel;
@property (weak, nonatomic) IBOutlet UIImageView* backgroundImageView;

+ (instancetype)generateView;
- (void)updateSize;
@end
