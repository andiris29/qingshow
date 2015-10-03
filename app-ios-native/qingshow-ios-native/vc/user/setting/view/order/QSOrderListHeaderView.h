//
//  QSOrderListHeaderView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QSOrderListHeaderViewDelegate <NSObject>

- (void)changeValueOfSegment:(NSInteger)value;
- (void)didTapPhone:(NSString*)phoneNumber;
@end

@interface QSOrderListHeaderView : UIView

@property (weak, nonatomic) IBOutlet UIImageView* headerImageView;
@property (weak, nonatomic) IBOutlet UILabel* label1;
@property (weak, nonatomic) IBOutlet UILabel* label2;
@property (weak, nonatomic) IBOutlet UISegmentedControl *segmentControl;
@property (strong, nonatomic) IBOutlet UITapGestureRecognizer* tapPhoneGes;
@property (assign,nonatomic) NSObject<QSOrderListHeaderViewDelegate>* delegate;

+ (instancetype)makeView;

- (IBAction)changeSegmentValue:(id)sender;
- (IBAction)didTapPhone:(UITapGestureRecognizer*)ges;
@end
