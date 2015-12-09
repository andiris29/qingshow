//
//  QSTradeListHeaderView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QSTradeListHeaderViewDelegate <NSObject>
- (void)didTapPhone:(NSString*)phoneNumber;
@end

@interface QSTradeListHeaderView : UIView

@property (weak, nonatomic) IBOutlet UILabel* label1;
@property (weak, nonatomic) IBOutlet UILabel* label2;
@property (strong, nonatomic) IBOutlet UITapGestureRecognizer* tapPhoneGes;
@property (assign,nonatomic) NSObject<QSTradeListHeaderViewDelegate>* delegate;

+ (instancetype)makeView;

- (IBAction)didTapPhone:(UITapGestureRecognizer*)ges;
@end
