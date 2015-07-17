//
//  QSU12RefundViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/14/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSU12RefundViewController : UIViewController

- (instancetype)initWithDict:(NSDictionary*)orderDict;
//@property (strong, nonatomic) UIScrollView* view;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *widthCon;

@property (weak, nonatomic) IBOutlet UITextField *companyTextField;
@property (weak, nonatomic) IBOutlet UITextField *expressOrderTextField;
@property (weak, nonatomic) IBOutlet UILabel *sendDateTextField;
@property (weak, nonatomic) IBOutlet UILabel *typeAddrLabel;
@property (weak, nonatomic) IBOutlet UILabel *typeReceiverLabel;
@property (weak, nonatomic) IBOutlet UIButton *submitBtn;
@property (weak, nonatomic) IBOutlet UITextField *resonTextField;

@property(nonatomic,assign)NSInteger type;

- (IBAction)submitBtnPressed:(id)sender;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet UIDatePicker *datePicker;
- (IBAction)pickerValueChanged:(UIDatePicker *)sender;

@end
