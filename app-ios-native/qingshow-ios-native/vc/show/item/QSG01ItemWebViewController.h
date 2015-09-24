//
//  QSG01ItemWebViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 1/12/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSG01ItemWebViewController : UIViewController<UIAlertViewDelegate>

- (id)initWithItem:(NSDictionary*)item;

- (id)initWithItem:(NSDictionary*)item showId:(NSString *)showId;

@property (weak, nonatomic) IBOutlet UIWebView *webView;

@property (weak, nonatomic) IBOutlet UIButton *discountBtn;

@property (weak, nonatomic) IBOutlet UIButton *cancelBtn;
@property (weak, nonatomic) IBOutlet UIButton *submitBtn;

@property (strong, nonatomic)NSString *showId;
@end
