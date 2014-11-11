//
//  QSS03ItemDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSS03ItemDetailViewController : UIViewController

@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UIButton *brandBtn;
@property (weak, nonatomic) IBOutlet UIButton *shopBtn;

- (id)initWithItemDict:(NSDictionary*)itemDict;

- (IBAction)closeBtnPressed:(id)sender;
- (IBAction)brandBtnPressed:(id)sender;
- (IBAction)shopBtnPressed:(id)sender;

@end
